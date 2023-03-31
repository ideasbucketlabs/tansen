/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.entity.TopicCreateRequest
import com.ideasbucket.tansen.entity.TopicEditRequest
import com.ideasbucket.tansen.service.TopicService
import com.ideasbucket.tansen.service.ValidationService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/topics")
class TopicsController(private val topicService: TopicService, private val validationService: ValidationService) {

    @GetMapping
    suspend fun getTopics(@PathVariable clusterId: String): Response {
        return Response.withSuccess(topicService.getTopics(clusterId))
    }

    @GetMapping("{topic}")
    suspend fun getTopic(@PathVariable clusterId: String, @PathVariable topic: String): Response {
        return Response.withSuccess(topicService.getTopic(clusterId, topic))
    }

    @PostMapping
    suspend fun addTopic(@PathVariable clusterId: String, @RequestBody request: TopicCreateRequest): Response {
        validationService.validate(request)
        topicService.addTopic(clusterId, request)

        return Response.withSuccess(listOf<Any>())
    }

    @PutMapping("{topic}")
    suspend fun editTopic(
        @PathVariable clusterId: String,
        @PathVariable topic: String,
        @RequestBody request: TopicEditRequest
    ): Response {
        validationService.validate(request)
        topicService.editTopic(clusterId, request)

        return Response.withSuccess(listOf<Any>())
    }

    @DeleteMapping("{topic}")
    suspend fun deleteTopic(@PathVariable clusterId: String, @PathVariable topic: String): Response {
        topicService.deleteTopic(clusterId, topic)

        return Response.withSuccess(listOf<Any>())
    }
}
