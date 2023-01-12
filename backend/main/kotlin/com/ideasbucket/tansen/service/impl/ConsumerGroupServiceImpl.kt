/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service.impl

import com.ideasbucket.tansen.entity.ConsumerGroupInformation
import com.ideasbucket.tansen.service.ClusterService
import com.ideasbucket.tansen.service.ConsumerGroupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.GroupAuthorizationException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class ConsumerGroupServiceImpl(private val clusterService: ClusterService) : ConsumerGroupService {

    private val logger = LoggerFactory.getLogger(ConsumerGroupService::class.java)

    override suspend fun getConsumerGroups(clusterId: String): List<Map<String, String>> = coroutineScope {
        val result = async(Dispatchers.IO) { getAdminClient(clusterId).listConsumerGroups().all().get() }

        result
            .await()
            .filter {
                it.groupId().isNotBlank() &&
                    !it.groupId().startsWith("Confluent")
            }
            .map { mapOf("name" to it.groupId()) }
    }

    override suspend fun getConsumerGroup(clusterId: String, groupName: String): List<ConsumerGroupInformation> =
        coroutineScope {
            val consumerOffsets =
                try {
                    withContext(Dispatchers.IO) {
                        getAdminClient(clusterId)
                            .listConsumerGroupOffsets(groupName)
                            .partitionsToOffsetAndMetadata()
                            .get()
                            .map {
                                TopicPartition(it.key.topic(), it.key.partition()) to it.value?.offset()
                            }
                            .toMap()
                    }
                } catch (exception: Exception) {
                    if (exception.cause is GroupAuthorizationException) {
                        logger.info("Not authorized to view consumer group {}; skipping", groupName)
                        mapOf()
                    } else {
                        throw exception
                    }
                }

            val producerOffsets = getConsumer(clusterId).endOffsets(consumerOffsets.keys)

            consumerOffsets.map {
                ConsumerGroupInformation(
                    groupName,
                    it.key.topic(),
                    it.key.partition(),
                    it.value,
                    (producerOffsets[it.key] ?: 0),
                    abs((producerOffsets[it.key] ?: 0).minus(it.value ?: 0))
                )
            }
        }

    private fun getAdminClient(clusterId: String): AdminClient = clusterService.getAdminClient(clusterId)

    private fun getConsumer(clusterId: String): Consumer<String, String> = clusterService.getConsumer(clusterId)
}
