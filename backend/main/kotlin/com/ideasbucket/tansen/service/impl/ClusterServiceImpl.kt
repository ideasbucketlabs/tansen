/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service.impl

import com.ideasbucket.tansen.configuration.ApplicationProperties
import com.ideasbucket.tansen.exception.InvalidClusterException
import com.ideasbucket.tansen.exception.SchemaRegistryNotConfiguredException
import com.ideasbucket.tansen.service.ClusterService
import com.ideasbucket.tansen.util.SchemaRegistryClientFactory.getClient
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class ClusterServiceImpl(private val applicationProperties: ApplicationProperties) : ClusterService {

    override fun getConsumer(clusterId: String): Consumer<String, String> {
        if (!commonConfigurations.containsKey(clusterId)) {
            throw InvalidClusterException(clusterId)
        }

        val properties: Map<String, Any> = commonConfigurations.getOrDefault(clusterId, mapOf()) + mapOf(
            ConsumerConfig.GROUP_ID_CONFIG to "tansen-consumer-group-" + UUID.randomUUID().toString(),
            ConsumerConfig.CLIENT_ID_CONFIG to "tansen-consumer-client-" + UUID.randomUUID().toString(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
        )

        return DefaultKafkaConsumerFactory<String, String>(properties).createConsumer()
    }

    override fun getAdminClient(clusterId: String): AdminClient {
        return adminClients[clusterId] ?: throw InvalidClusterException(clusterId)
    }

    override fun getSchemaRegistryClient(clusterId: String): WebClient {
        return webClients[clusterId] ?: throw SchemaRegistryNotConfiguredException("Schema registry is not configured.")
    }

    override fun getCommonConfiguration(clusterId: String): Map<String, String> {
        return commonConfigurations[clusterId] ?: throw InvalidClusterException(clusterId)
    }

    override fun getSchemaRegistryUrl(clusterId: String): String {
        return applicationProperties.keyedCluster[clusterId]?.schemaRegistryUrl
            ?: throw SchemaRegistryNotConfiguredException("Schema registry is not configured.")
    }

    override fun getAdminClients(): Map<String, AdminClient> = adminClients

    override fun getClusterLabels(): Map<String, String> = applicationProperties.keyedCluster.map {
        it.key to it.value.label
    }.associateBy(keySelector = { it.first }, valueTransform = { it.second })

    override fun isTopicAddAllowed(clusterId: String): Boolean {
        return applicationProperties.keyedCluster[clusterId]?.topicAddAllowed ?: false
    }

    override fun isTopicEditAllowed(clusterId: String): Boolean {
        return applicationProperties.keyedCluster[clusterId]?.topicEditAllowed ?: false
    }

    override fun isTopicDeleteAllowed(clusterId: String): Boolean {
        return applicationProperties.keyedCluster[clusterId]?.topicDeleteAllowed ?: false
    }

    private val commonConfigurations: Map<String, Map<String, String>> = applicationProperties.keyedCluster.map {
        it.key to mapOf(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to it.value.bootstrapServers) + it.value.kafkaOptions
    }.associateBy(keySelector = { it.first }, valueTransform = { it.second })

    private val adminClients: Map<String, AdminClient> = applicationProperties.keyedCluster.map {
        it.key to KafkaAdminClient.create(
            Properties().apply {
                this[ConsumerConfig.CLIENT_ID_CONFIG] = "tansen-admin-" + UUID.randomUUID().toString()
                this.putAll(commonConfigurations.getOrDefault(it.key, mapOf()))
            }
        )
    }.associateBy(keySelector = { it.first }, valueTransform = { it.second })

    private val webClients: Map<String, WebClient> = applicationProperties.keyedCluster.filter {
        it.value.isSchemaRegistryConfigured
    }.map { cluster ->
        cluster.key to getClient(
            cluster.value.schemaRegistryOptions.getOrDefault("truststore.location", ""),
            cluster.value.schemaRegistryOptions.getOrDefault("truststore.password", ""),
            cluster.value.schemaRegistryOptions.getOrDefault("keystore.location", ""),
            cluster.value.schemaRegistryOptions.getOrDefault("keystore.password", "")
        )
            .baseUrl(cluster.value.schemaRegistryUrl).apply { builder ->
                if (cluster.value.isSchemaRegistryBasicAuthConfigured) {
                    builder.defaultHeaders {
                        it.setBasicAuth(cluster.value.schemaRegistryUsername, cluster.value.schemaRegistryPassword)
                    }
                }
            }
            .build()
    }.associateBy(keySelector = { it.first }, valueTransform = { it.second })
}
