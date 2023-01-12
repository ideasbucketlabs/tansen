/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service

import com.ideasbucket.tansen.entity.NewTopicCreateRequest
import com.ideasbucket.tansen.entity.Topic
import org.apache.kafka.clients.admin.CreateTopicsResult

interface TopicService {

    suspend fun getTopics(clusterId: String): List<Any>

    suspend fun getTopic(clusterId: String, topicName: String): Topic?

    suspend fun addTopic(clusterId: String, newTopic: NewTopicCreateRequest): CreateTopicsResult

    suspend fun deleteTopic(clusterId: String, topicName: String)
}
