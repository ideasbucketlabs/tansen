/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.fasterxml.jackson.databind.JsonNode
import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.service.ClusterService
import com.ideasbucket.tansen.service.SchemaExecutor
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/schemas")
class SchemasController(private val schemaExecutor: SchemaExecutor, private val clusterService: ClusterService) {

    @GetMapping("configured")
    suspend fun isConfigured(@PathVariable clusterId: String): Response {
        clusterService.getSchemaRegistryClient(clusterId)

        return Response.withSuccess(mapOf("configured" to true))
    }

    @GetMapping("ids/{id}")
    suspend fun getById(@PathVariable clusterId: String, @PathVariable id: Long): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(HttpMethod.GET, clusterId, "schemas/ids/$id")
        )
    }

    @GetMapping("types")
    suspend fun getTypes(@PathVariable clusterId: String): Response {
        return Response.withSuccess(
            schemaExecutor.execute<List<String>>(HttpMethod.GET, clusterId, "schemas/types")
        )
    }

    @GetMapping("ids/{id}/versions")
    suspend fun getVersions(@PathVariable clusterId: String, @PathVariable id: Long): Response {
        return schemaExecutor.execute<JsonNode>(HttpMethod.GET, clusterId, "schemas/ids/$id/versions").run {
            Response(true, this?.size()?.toLong() ?: 0L, 0, this, null)
        }
    }
}
