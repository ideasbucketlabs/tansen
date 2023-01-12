/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.service.ClusterInformationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/cluster-information")
class ClusterInformationController(private val clusterInformationService: ClusterInformationService) {

    @GetMapping
    suspend fun getClusterInformation(): Response = Response.withSuccess(clusterInformationService.getInformation())
}
