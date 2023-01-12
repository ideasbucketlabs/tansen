/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.fasterxml.jackson.databind.JsonNode
import com.ideasbucket.tansen.entity.ModeChangeRequest
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/mode")
class ModeController(private val schemaExecutor: SchemaExecutor) {

    @GetMapping
    suspend fun getMode(@PathVariable clusterId: String): Response {
        return Response.withSuccess(schemaExecutor.execute<JsonNode>(HttpMethod.GET, clusterId, "/mode"))
    }

    @PutMapping
    @Validated
    suspend fun setMode(
        @RequestBody @Valid
        @PathVariable
        clusterId: String,
        request: ModeChangeRequest,
        @RequestParam force: Boolean = false
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(HttpMethod.PUT, clusterId, "/mode?force=$force", request)
        )
    }

    @GetMapping("{subject}")
    suspend fun getModeBySubject(@PathVariable clusterId: String, @PathVariable subject: String): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(HttpMethod.GET, clusterId, "/mode/$subject")
        )
    }

    @PutMapping("{subject}")
    @Validated
    suspend fun setModeBySubject(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @RequestBody @Valid
        request: ModeChangeRequest,
        @RequestParam force: Boolean = false
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.PUT,
                clusterId,
                "/mode/$subject?force=$force",
                request
            )
        )
    }

    @DeleteMapping("{subject}")
    suspend fun deleteModeBySubject(@PathVariable clusterId: String, @PathVariable subject: String): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(HttpMethod.DELETE, clusterId, "/mode/$subject")
        )
    }
}
