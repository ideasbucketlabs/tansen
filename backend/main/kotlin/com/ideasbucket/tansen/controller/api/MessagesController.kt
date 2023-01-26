/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.google.protobuf.DynamicMessage
import com.google.protobuf.util.JsonFormat
import com.ideasbucket.tansen.entity.MessageSelectionCriteria
import com.ideasbucket.tansen.entity.TopicSchema
import com.ideasbucket.tansen.exception.SchemaRegistryNotConfiguredException
import com.ideasbucket.tansen.service.ClusterService
import com.ideasbucket.tansen.service.SchemaExecutor
import com.ideasbucket.tansen.service.TopicService
import com.ideasbucket.tansen.service.ValidationService
import com.ideasbucket.tansen.util.JsonConverter
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverRecord
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.*

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/messages")
class MessagesController(
    private val clusterService: ClusterService,
    private val schemaExecutor: SchemaExecutor,
    private val topicService: TopicService,
    private val validationService: ValidationService
) {

    private val logger = LoggerFactory.getLogger(MessagesController::class.java)

    @GetMapping("{topic}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getMessagesByTopic(
        @PathVariable clusterId: String,
        @PathVariable topic: String,
        @RequestParam(name = "parameters", required = false) criteria: MessageSelectionCriteria?
    ): Flow<ObjectNode> {
        val topicInformation =
            topicService.getTopic(clusterId, topic)
                ?: throw UnknownTopicOrPartitionException(
                    "This server does not host this topic-partition."
                )
        val partitionSize = topicInformation.partitions.size - 1

        if (criteria != null) {
            validationService.validate(criteria)
        }

        if ((criteria?.partition ?: 0) > partitionSize) {
            throw UnknownTopicOrPartitionException("This server does not host this topic-partition.")
        }

        val valueSchemaFormat = getSchemaFormat(clusterId, "$topic-value")
        val keySchemaFormat = getSchemaFormat(clusterId, "$topic-key")
        val uniqueId = UUID.randomUUID().toString()

        val properties = Properties()

        properties.putAll(addProperties(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueSchemaFormat))
        properties.putAll(addProperties(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keySchemaFormat))
        properties.putAll(clusterService.getCommonConfiguration(clusterId))

        properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        properties[ConsumerConfig.GROUP_ID_CONFIG] = "tansen-group-$uniqueId"
        properties[ConsumerConfig.CLIENT_ID_CONFIG] = "tansen-client-$uniqueId"

        getSchemaRegistryUrl(clusterId)?.let {
            properties["schema.registry.url"] = it
        }

        // properties[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 20

        if ((criteria !== null) && (criteria.case == "timestamp")) {
            properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        }

        val receiverOptions =
            if ((criteria != null) && (criteria.case == "offset")) {
                ReceiverOptions.create<Any, Any>(properties)
                    .commitInterval(Duration.ZERO)
                    .commitBatchSize(0)
                    .addAssignListener { partitions ->
                        partitions.forEach { it.seek(criteria.offset) }
                    }
                    .assignment(setOf(TopicPartition(topic, criteria.partition)))
            } else if ((criteria != null) && (criteria.case == "timestamp")) {
                ReceiverOptions.create<Any, Any>(properties)
                    .commitInterval(Duration.ZERO)
                    .commitBatchSize(0)
                    .addAssignListener { partitions ->
                        partitions.forEach { it.seekToTimestamp(criteria.timestamp.epochSecond) }
                    }
                    .subscription(setOf(topic))
            } else {
                ReceiverOptions.create<Any, Any>(properties)
                    .commitInterval(Duration.ZERO)
                    .commitBatchSize(0)
                    .subscription(setOf(topic))
            }

        return KafkaReceiver.create(receiverOptions)
            .receive()
            .delayElements(Duration.ofMillis(500))
            .map {
                JsonNodeFactory.instance.objectNode().run {
                    populateData(it, this, "valueData")
                    populateData(it, this, "keyData")

                    this.replace(
                        "headerData",
                        JsonConverter.getMapper()
                            .valueToTree(
                                it.headers().map {
                                    mapOf(it.key() to String(it.value(), StandardCharsets.UTF_8))
                                }
                            )
                    )

                    this.put("timestamp", it.timestamp())
                    this.put("partition", it.partition())
                    this.put("valueSchemaFormat", valueSchemaFormat)
                    this.put("keySchemaFormat", keySchemaFormat)
                    this.put("offset", it.offset())
                    this.put("timestampType", it.timestampType().toString())
                }
            }
            .onErrorResume {
                logger.error(it.message, it)
                Flux.just(
                    JsonNodeFactory.instance.objectNode().run {
                        this.put("headerData", "")
                        this.put("valueData", it.cause?.message ?: it.message)
                        this.put("keyData", "")
                        this.put("timestamp", -1)
                        this.put("timestampType", "NoTimestampType")
                        this.put("partition", -1)
                        this.put("valueSchemaFormat", "AVRO")
                        this.put("keySchemaFormat", "AVRO")
                        this.put("offset", -1)
                    }
                )
            }.asFlow()
    }

    private suspend fun getSchemaFormat(clusterId: String, subject: String): String? {
        return try {
            schemaExecutor.execute<TopicSchema>(
                HttpMethod.GET,
                clusterId,
                "subjects/$subject/versions/latest"
            )?.schemaType
        } catch (exception: Exception) {
            null
        }
    }

    private fun populateData(record: ReceiverRecord<*, *>, node: ObjectNode, key: String) {
        when (val data = if (key == "keyData") record.key() else record.value()) {
            is GenericData.Record -> {
                node.replace(key, JsonConverter.getMapper().readTree(data.toString()))
            }
            is DynamicMessage -> {
                node.replace(key, JsonConverter.getMapper().readTree(JsonFormat.printer().print(data)))
            }
            is Map<*, *> -> {
                node.replace(key, JsonConverter.getMapper().valueToTree(data))
            }
            else -> {
                node.put(key, data.toString())
            }
        }
    }

    private fun addProperties(key: String, format: String?): Properties {
        val properties = Properties()

        when (format) {
            "PROTOBUF" -> {
                properties[key] = KafkaProtobufDeserializer::class.java
            }
            "JSON" -> {
                properties[key] = KafkaJsonSchemaDeserializer::class.java
            }
            "AVRO" -> {
                properties[key] = KafkaAvroDeserializer::class.java
            }
            else -> {
                properties[key] = StringDeserializer::class.java
            }
        }

        return properties
    }

    private fun getSchemaRegistryUrl(clusterId: String): String? {
        return try {
            clusterService.getSchemaRegistryUrl(clusterId)
        } catch (exception: SchemaRegistryNotConfiguredException) {
            null
        }
    }
}
