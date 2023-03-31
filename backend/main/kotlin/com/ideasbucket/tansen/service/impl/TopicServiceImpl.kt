/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service.impl

import com.ideasbucket.tansen.entity.ConfigurationWithDefinition
import com.ideasbucket.tansen.entity.Definition
import com.ideasbucket.tansen.entity.Topic
import com.ideasbucket.tansen.entity.TopicCreateRequest
import com.ideasbucket.tansen.entity.TopicEditRequest
import com.ideasbucket.tansen.entity.TopicPartition
import com.ideasbucket.tansen.exception.NotSupportedException
import com.ideasbucket.tansen.exception.TopicAlreadyExistException
import com.ideasbucket.tansen.exception.TopicOperationException
import com.ideasbucket.tansen.service.ClusterService
import com.ideasbucket.tansen.service.TopicService
import com.ideasbucket.tansen.util.BrokerConfigurationDefinition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AlterConfigOp
import org.apache.kafka.clients.admin.AlterConfigsResult
import org.apache.kafka.clients.admin.ConfigEntry
import org.apache.kafka.clients.admin.CreateTopicsResult
import org.apache.kafka.clients.admin.ListTopicsOptions
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.config.ConfigResource
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException

@Service
class TopicServiceImpl(private val clusterService: ClusterService) : TopicService {

    override suspend fun getTopics(clusterId: String): List<Map<String, Any>> {
        val topics = withContext(Dispatchers.IO) {
            clusterService.getAdminClient(clusterId)
                .listTopics(ListTopicsOptions().apply { this.listInternal(false) })
                .listings()
                .get()
        }
        // formatter:off
        val partitions = clusterService.getConsumer(clusterId)
                                       .listTopics()
                                       .map { it ->
                                            it.key to it.value.map {
                                                partitionInfo -> mapOf(
                                                    "inSyncReplicas" to partitionInfo.inSyncReplicas().size,
                                                    "offlineReplicas" to partitionInfo.offlineReplicas().size,
                                                    "totalReplicas" to partitionInfo.inSyncReplicas().size +
                                                                       partitionInfo.offlineReplicas().size,
                                                    "totalPartitions" to 1,
                                                    "replicationFactor" to partitionInfo.replicas().size
                                                )
                                            }
                                            .flatMap { item -> item.entries }
                                            .groupBy { entry -> entry.key }
                                            .mapValues { (key, value) -> if (key == "replicationFactor")
                                                value.first().value else value.sumOf { it.value }}
                                        }
                                        .associate { it.first to it.second }
        // formatter:on
        return topics.filter { !it.name().startsWith("_") }.map {
            mapOf(
                "topic" to it.name(),
                "uuid" to it.topicId().toString(),
                "isInternal" to it.isInternal,
                "totalPartitions" to partitions.getOrDefault(it.name(), mapOf()).getOrDefault("totalPartitions", 0),
                "inSyncReplicas" to partitions.getOrDefault(it.name(), mapOf()).getOrDefault("inSyncReplicas", 0),
                "offlineReplicas" to partitions.getOrDefault(it.name(), mapOf()).getOrDefault("offlineReplicas", 0),
                "totalReplicas" to partitions.getOrDefault(it.name(), mapOf()).getOrDefault("totalReplicas", 0),
                "replicationFactor" to partitions.getOrDefault(it.name(), mapOf()).getOrDefault("replicationFactor", 0)
            )
        }
    }

    override suspend fun getTopic(clusterId: String, topicName: String): Topic? {
        val adminClient = clusterService.getAdminClient(clusterId)

        try {
            val topicInformation =
                withContext(Dispatchers.IO) {
                    adminClient.describeTopics(listOf(topicName)).allTopicNames().get()
                }

            val configuration =
                withContext(Dispatchers.IO) {
                    adminClient
                        .describeConfigs(
                            listOf(topicName).map { ConfigResource(ConfigResource.Type.TOPIC, it) }
                        )
                        .all()
                        .get()
                }

            val topicDescription =
                topicInformation[topicName]
                    ?: throw UnknownTopicOrPartitionException(
                        "This server does not host this topic-partition"
                    )

            return Topic(
                topicDescription.name(),
                topicDescription.topicId().toString(),
                topicDescription.partitions().size,
                if (topicDescription.partitions() == null || topicDescription.partitions().isEmpty()) {
                    0
                } else {
                    topicDescription.partitions()[0].replicas().size
                },
                topicDescription.partitions().map { TopicPartition(topicName, it) },
                configuration
                    .values
                    .map { configurations ->
                        configurations.entries().sortedBy { it.name() }.map { config ->
                            ConfigurationWithDefinition(
                                config.value(),
                                Definition(
                                    config.name(),
                                    getDocumentation(config),
                                    config.type().toString()
                                )
                            )
                        }
                    }.flatten()
            )
        } catch (exception: ExecutionException) {
            if (exception.cause is UnknownTopicOrPartitionException) {
                throw exception.cause as UnknownTopicOrPartitionException
            }

            if (exception.message?.contains("This server does not host this topic-partition.") == true) {
                throw UnknownTopicOrPartitionException("This server does not host this topic-partition.")
            }

            throw Exception(exception.message)
        }
    }

    override suspend fun addTopic(clusterId: String, request: TopicCreateRequest): CreateTopicsResult {
        if (!clusterService.isTopicAddAllowed(clusterId)) {
            throw NotSupportedException("This action is disabled in this cluster.")
        }

        val adminClient = clusterService.getAdminClient(clusterId)

        if (doesTopicExistAlready(request.name, adminClient)) {
            throw TopicAlreadyExistException("Topic with name ${request.name} already exists.")
        }

        val nodes = withContext(Dispatchers.IO) {
            adminClient.describeCluster().nodes().get()
        }

        if (request.replicationFactor > nodes.size) {
            "Replication factor is more than nodes available".run {
                throw TopicOperationException(
                    request.name,
                    this,
                    Exception(this)
                )
            }
        }

        if (request.minInsyncReplicas > nodes.size) {
            "Minimum in sync replica is more than nodes available".run {
                throw TopicOperationException(
                    request.name,
                    this,
                    Exception(this)
                )
            }
        }

        try {
            val createTopicsResult = withContext(Dispatchers.IO) {
                adminClient.createTopics(
                    setOf(
                        NewTopic(
                            request.name,
                            request.partition,
                            request.replicationFactor
                        ).configs(request.topicOptions)
                    )
                )
            }
            return createTopicsResult
        } catch (exception: ExecutionException) {
            throw TopicOperationException(
                request.name,
                "Unable to create topic #${request.name}. ${exception.cause?.message ?: ""}",
                exception
            )
        }
    }

    override suspend fun editTopic(clusterId: String, request: TopicEditRequest): AlterConfigsResult? {
        if (!clusterService.isTopicEditAllowed(clusterId)) {
            throw NotSupportedException("This action is disabled in this cluster.")
        }

        val adminClient = clusterService.getAdminClient(clusterId)

        if (!doesTopicExistAlready(request.newRecord.name, adminClient)) {
            throw TopicAlreadyExistException("Topic with name ${request.newRecord.name} does not exists.")
        }

        val nodes = withContext(Dispatchers.IO) {
            adminClient.describeCluster().nodes().get()
        }

        if (request.newRecord.minInsyncReplicas > nodes.size) {
            "Minimum in sync replica is more than nodes available".run {
                throw TopicOperationException(
                    request.newRecord.name,
                    this,
                    Exception(this)
                )
            }
        }

        val changes = request.changes

        if (changes.isEmpty()) {
            return null
        }

        try {
            val editTopicResult = withContext(Dispatchers.IO) {
                adminClient.incrementalAlterConfigs(changes)
            }
            return editTopicResult
        } catch (exception: ExecutionException) {
            throw TopicOperationException(
                request.newRecord.name,
                "Unable to edit topic #${request.newRecord.name}. ${exception.cause?.message ?: ""}",
                exception
            )
        }
    }

    override suspend fun deleteTopic(clusterId: String, topicName: String) {
        if (!clusterService.isTopicDeleteAllowed(clusterId)) {
            throw NotSupportedException("This action is disabled in this cluster.")
        }
        val adminClient = clusterService.getAdminClient(clusterId)
        if (!doesTopicExistAlready(topicName, adminClient)) {
            throw UnknownTopicOrPartitionException("This server does not host this topic-partition")
        }
        try {
            withContext(Dispatchers.IO) {
                adminClient.deleteTopics(listOf(topicName)).all().get()
            }
        } catch (exception: ExecutionException) {
            throw TopicOperationException(
                topicName,
                "Unable to delete topic #$topicName. ${exception.cause?.message ?: ""}",
                exception
            )
        }
    }

    private suspend fun doesTopicExistAlready(topicName: String, adminClient: AdminClient): Boolean {
        try {
            AlterConfigOp.OpType.APPEND
            val topicInformation = withContext(Dispatchers.IO) {
                adminClient.describeTopics(listOf(topicName)).allTopicNames().get()
            }

            if (topicInformation.containsKey(topicName)) {
                return true
            }

            return false
        } catch (exception: Exception) {
            return false
        }
    }

    private fun getDocumentation(configEntry: ConfigEntry): String {
        if (configEntry.documentation().isNullOrBlank()) {
            return BrokerConfigurationDefinition.getDefinition(configEntry.name()).definition
        }

        return configEntry.documentation()
    }
}
