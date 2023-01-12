/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.service.ClusterService
import com.ideasbucket.tansen.service.ConsumerGroupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ExecutionException

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/topic-consumer-groups")
class TopicConsumerGroups(
    private val clusterService: ClusterService,
    private val consumerGroupService: ConsumerGroupService
) {

    @GetMapping("{topic}")
    suspend fun getTopicConsumerGroups(
        @PathVariable clusterId: String,
        @PathVariable topic: String
    ): Response = coroutineScope {
        try {
            val topicInformation =
                withContext(Dispatchers.IO) {
                    clusterService.getAdminClient(clusterId).describeTopics(listOf(topic)).allTopicNames().get()
                }

            if (!topicInformation.containsKey(topic)) {
                throw UnknownTopicOrPartitionException("This server does not host this topic-partition")
            }

            val result =
                consumerGroupService
                    .getConsumerGroups(clusterId)
                    .map {
                        async(Dispatchers.IO) {
                            consumerGroupService.getConsumerGroup(clusterId, it["name"]!!)
                        }
                    }
                    .awaitAll()
                    .flatten()
                    .filter { it.topic == topic }
                    .sortedBy { it.partition }

            Response.withSuccess(result)
        } catch (exception: ExecutionException) {
            if (exception.cause is UnknownTopicOrPartitionException) {
                throw exception.cause as UnknownTopicOrPartitionException
            }

            throw Exception(exception.message)
        }
    }
}
