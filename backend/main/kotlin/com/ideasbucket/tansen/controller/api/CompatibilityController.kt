/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.fasterxml.jackson.databind.JsonNode
import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.entity.SaveSchemaRequest
import com.ideasbucket.tansen.service.SchemaExecutor
import org.springframework.http.HttpMethod
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/compatibility")
class CompatibilityController(private val schemaExecutor: SchemaExecutor) {

    @PostMapping("/subjects/{subject}/versions/{versionId:[\\d]+}")
    @Validated
    suspend fun checkCompatibilityBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable versionId: Int,
        @RequestParam verbose: Boolean = false,
        @RequestBody @Valid
        request: SaveSchemaRequest
    ): Response {
        return formatResponse(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.POST,
                clusterId,
                "compatibility/subjects/$subject/versions/$versionId?verbose=$verbose",
                request
            )
        )
    }

    @PostMapping("/subjects/{subject}/versions/{version:^latest|LATEST\$}")
    @Validated
    suspend fun checkCompatibilityBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable version: String,
        @RequestParam verbose: Boolean = false,
        @RequestBody @Valid
        request: SaveSchemaRequest
    ): Response {
        return formatResponse(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.POST,
                clusterId,
                "compatibility/subjects/$subject/versions/${version.lowercase()}?verbose=$verbose",
                request
            )
        )
    }

    @PostMapping("/subjects/{subject}/versions")
    @Validated
    suspend fun checkCompatibilityBySubject(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @RequestParam verbose: Boolean = false,
        @RequestBody @Valid
        request: SaveSchemaRequest
    ): Response {
        return formatResponse(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.POST,
                clusterId,
                "compatibility/subjects/$subject/versions?verbose=$verbose",
                request
            )
        )
    }

    private fun formatResponse(request: JsonNode?): Response {
        return Response.withSuccess(mapOf("isCompatible" to (request?.get("is_compatible")?.asBoolean() ?: false)))
    }
}
