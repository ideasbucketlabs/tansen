/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service.impl

import com.ideasbucket.tansen.entity.ClusterInformation
import com.ideasbucket.tansen.entity.ConfigurationWithDefinition
import com.ideasbucket.tansen.entity.Definition
import com.ideasbucket.tansen.entity.NodeInformation
import com.ideasbucket.tansen.service.ClusterInformationService
import com.ideasbucket.tansen.service.ClusterService
import com.ideasbucket.tansen.util.BrokerConfigurationDefinition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.ConfigEntry
import org.apache.kafka.clients.admin.ListTopicsOptions
import org.apache.kafka.common.config.ConfigResource
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ClusterInformationServiceImpl(private val clusterService: ClusterService) : ClusterInformationService {
    override suspend fun getInformation(): List<ClusterInformation> {
        return clusterService.getAdminClients().map {
            getClusterInformation(it.value, it.key, clusterService.getClusterLabels().getOrDefault(it.key, ""))
        }
    }

    suspend fun getClusterInformation(
        adminClient: AdminClient,
        clusterName: String,
        clusterLabel: String
    ): ClusterInformation {
        try {
            val clusterId = withContext(Dispatchers.IO) {
                adminClient.describeCluster().clusterId().get()
            }

            val nodes = withContext(Dispatchers.IO) {
                adminClient.describeCluster().nodes().get()
            }

            val topics = withContext(Dispatchers.IO) {
                adminClient.listTopics(ListTopicsOptions().apply { this.listInternal(true) }).listings().get()
            }

            val topicInformation = withContext(Dispatchers.IO) {
                adminClient.describeTopics(topics.map { it.name() }).topicNameValues().values.map { it.get() }
            }

            // formatter:off
            val nodeConfigurations = withContext(Dispatchers.IO) {
                adminClient.describeConfigs(nodes.map { ConfigResource(ConfigResource.Type.BROKER, it.id().toString()) })
                    .all()
                    .get()
            }
                .map { it.key.name() to it.value }
                .associateBy(
                    { it.first },
                    { item -> item.second.entries()
                        .filter { !it.isSensitive }
                        .map {
                            ConfigurationWithDefinition(
                                it.value(),
                                Definition(
                                    it.name(),
                                    getDocumentation(it),
                                    it.type().toString()
                                )
                            )
                        }.sortedBy { it.name }
                    }
                )
            // formatter:on

            val (commonConfigurations, nodeSpecificConfiguration) = accumulateCommonConfigurations(nodeConfigurations)

            topicInformation.map { partition -> partition.partitions().map { it.replicas().size } }
            return ClusterInformation(
                clusterId,
                clusterName,
                clusterLabel,
                true,
                nodes.map {
                    NodeInformation(
                        it.id(),
                        it.port(),
                        it.rack(),
                        it.host(),
                        nodeSpecificConfiguration.getOrDefault(it.id().toString(), listOf())
                    )
                },
                commonConfigurations,
                topics.size,
                topicInformation.sumOf { it.partitions().size },
                topicInformation.sumOf { partition -> partition.partitions().map { it.replicas().size }.size },
                topicInformation.sumOf { partition -> partition.partitions().map { it.isr().size }.size }
            )
        } catch (exception: Exception) {
            return ClusterInformation(
                UUID.randomUUID().toString(),
                clusterName,
                clusterLabel,
                false,
                listOf(),
                listOf(),
                -1,
                -1,
                -1,
                -1,
                -1
            )
        }
    }

    private fun getDocumentation(configEntry: ConfigEntry): String {
        if (configEntry.documentation().isNullOrBlank()) {
            return BrokerConfigurationDefinition.getDefinition(configEntry.name()).definition
        }

        return configEntry.documentation()
    }

    private fun accumulateCommonConfigurations(configurations: Map<String, List<ConfigurationWithDefinition>>):
        Pair<List<ConfigurationWithDefinition>, Map<String, List<ConfigurationWithDefinition>>> {
        if (configurations.isEmpty()) {
            return Pair(listOf(), mapOf())
        }

        val currentCounts = mutableMapOf<String, Int>()

        configurations.values.forEach { configuration ->
            configuration.forEach {
                val key = it.name + it.value
                currentCounts[key] = currentCounts.getOrDefault(key, 0) + 1
            }
        }

        val totalMatch = configurations.keys.size
        val commonConfigurations = configurations.getOrDefault(configurations.keys.first(), listOf()).filter {
            val key = it.name + it.value
            currentCounts.containsKey(key) && currentCounts.getOrDefault(key, 0) == totalMatch
        }

        val filteredConfigurations = configurations.map { it.key to it.value }.associateBy(
            { it.first },
            {
                    item ->
                item.second.filter {
                    currentCounts.getOrDefault((it.name + it.value), 0) != totalMatch
                }.sortedBy { it.name }
            }
        )

        return Pair(commonConfigurations, filteredConfigurations)
    }
}
