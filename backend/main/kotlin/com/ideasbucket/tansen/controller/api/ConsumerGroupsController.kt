/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.service.ConsumerGroupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/consumer-groups")
class ConsumerGroupsController(private val consumerGroupService: ConsumerGroupService) {

    @GetMapping
    suspend fun getConsumerGroups(@PathVariable clusterId: String): Response {
        return Response.withSuccess(consumerGroupService.getConsumerGroups(clusterId))
    }

    @GetMapping("{consumerGroup}")
    suspend fun getConsumerGroup(@PathVariable clusterId: String, @PathVariable consumerGroup: String): Response {
        return Response.withSuccess(
            consumerGroupService.getConsumerGroup(clusterId, consumerGroup).sortedBy { it.partition }
        )
    }
}
