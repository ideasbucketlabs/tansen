/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.fasterxml.jackson.databind.JsonNode
import com.ideasbucket.tansen.entity.CompatibilityChangeRequest
import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.service.SchemaExecutor
import org.springframework.http.HttpMethod
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/config")
class ConfigController(private val schemaExecutor: SchemaExecutor) {

    @GetMapping
    suspend fun getConfig(@PathVariable clusterId: String): Response {
        return Response.withSuccess(schemaExecutor.execute<JsonNode>(HttpMethod.GET, clusterId, "/config"))
    }

    @PutMapping
    @Validated
    suspend fun setConfig(
        @PathVariable clusterId: String,
        @RequestBody @Valid
        request: CompatibilityChangeRequest
    ): Response {
        return Response.withSuccess(schemaExecutor.execute<JsonNode>(HttpMethod.PUT, clusterId, "/config", request))
    }

    @PutMapping("{subject}")
    @Validated
    suspend fun setConfigBySubject(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @RequestBody @Valid
        request: CompatibilityChangeRequest
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(HttpMethod.PUT, clusterId, "/config/$subject", request)
        )
    }

    @GetMapping("{subject}")
    suspend fun getConfigBySubject(@PathVariable clusterId: String, @PathVariable subject: String): Response {
        return Response.withSuccess(schemaExecutor.execute<JsonNode>(HttpMethod.GET, clusterId, "/config/$subject"))
    }

    @DeleteMapping("{subject}")
    suspend fun deleteConfigBySubject(@PathVariable clusterId: String, @PathVariable subject: String): Response {
        return Response.withSuccess(schemaExecutor.execute<JsonNode>(HttpMethod.DELETE, clusterId, "/config/$subject"))
    }
}
