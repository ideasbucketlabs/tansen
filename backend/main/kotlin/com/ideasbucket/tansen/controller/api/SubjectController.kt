/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.fasterxml.jackson.databind.JsonNode
import com.ideasbucket.tansen.entity.MessageSelectionCriteria
import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.entity.SaveSchemaRequest
import com.ideasbucket.tansen.entity.TopicSchema
import com.ideasbucket.tansen.exception.SchemaServiceAPIException
import com.ideasbucket.tansen.service.SchemaExecutor
import com.ideasbucket.tansen.service.ValidationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("api/{clusterId:[a-zA-Z0-9][a-zA-Z0-9\\_\\-]+}/subjects")
class SubjectController(private val schemaExecutor: SchemaExecutor, private val validationService: ValidationService) {

    @GetMapping
    suspend fun getAll(
        @PathVariable clusterId: String,
        @RequestParam subjectPrefix: String? = "",
        @RequestParam(name = "deleted") deleted: String = "false"
    ): Response {
        val url = UriComponentsBuilder.fromPath("subjects").queryParam("deleted", deleted)

        if (!subjectPrefix.isNullOrBlank()) {
            url.queryParam("subjectPrefix", subjectPrefix)
        }

        return Response.withSuccess(
            schemaExecutor.execute<List<String>>(HttpMethod.GET, clusterId, url.encode().build().toString())
        )
    }

    @GetMapping("{format:^(?:AVRO|JSON|PROTOBUF|avro|json|protobuf)$}")
    suspend fun getSubjectsByFormat(
        @PathVariable clusterId: String,
        @PathVariable format: String
    ): Response = coroutineScope {
        val subjects: List<String>? =
            UriComponentsBuilder.fromPath("subjects").run {
                schemaExecutor.execute<List<String>>(
                    HttpMethod.GET,
                    clusterId,
                    this.encode().build().toString()
                )
            }

        if (subjects == null) {
            Response.withSuccess(listOf<String>())
        } else {
            val topicSchemas =
                subjects
                    .map {
                        async(Dispatchers.IO) {
                            schemaExecutor.execute<TopicSchema>(
                                HttpMethod.GET,
                                clusterId,
                                "subjects/$it/versions/latest"
                            )
                        }
                    }
                    .awaitAll()
                    .filter { (it != null) && (it.schemaType == format.uppercase()) }
                    .map { it?.subject }

            Response.withSuccess(topicSchemas)
        }
    }

    @GetMapping("{subject}/versions")
    suspend fun getVersionsBySubject(@PathVariable clusterId: String, @PathVariable subject: String): Response {
        return Response.withSuccess(
            schemaExecutor.execute<List<Int>>(HttpMethod.GET, clusterId, "subjects/$subject/versions")
        )
    }

    @DeleteMapping("{subject}")
    suspend fun deleteSubject(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @RequestParam(name = "permanent") permanent: String = "false"
    ): Response {
        lateinit var response: Response

        try {
            response =
                Response.withSuccess(
                    schemaExecutor.execute<List<Int>>(HttpMethod.DELETE, clusterId, "subjects/$subject")
                )

            if (permanent == "true") {
                response =
                    Response.withSuccess(
                        schemaExecutor.execute<List<Int>>(
                            HttpMethod.DELETE,
                            clusterId,
                            "subjects/$subject?permanent=true"
                        )
                    )
            }
        } catch (exception: SchemaServiceAPIException) {
            // This would happen if schema is deleted already.
            if (exception.httpStatus.is4xxClientError &&
                (permanent == "true") &&
                (
                    exception.apiErrorResponse.message ==
                        "Subject '$subject' was soft deleted.Set permanent=true to delete permanently"
                    )
            ) {
                response =
                    Response.withSuccess(
                        schemaExecutor.execute<List<Int>>(
                            HttpMethod.DELETE,
                            clusterId,
                            "subjects/$subject?permanent=true"
                        )
                    )
            } else {
                throw exception
            }
        }

        return response
    }

    @GetMapping("{subject}/versions/{versionId:[\\d]+}")
    suspend fun getBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable versionId: Int
    ): Response {
        val result =
            schemaExecutor.execute<TopicSchema>(
                HttpMethod.GET,
                clusterId,
                "subjects/$subject/versions/$versionId"
            )

        return Response.withSuccess(result)
    }

    @GetMapping("{subject}/versions/{version:^latest|LATEST$}")
    suspend fun getBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable version: String
    ): Response {
        val result =
            schemaExecutor.execute<TopicSchema>(
                HttpMethod.GET,
                clusterId,
                "subjects/$subject/versions/${version.lowercase()}"
            )

        return Response.withSuccess(result)
    }

    @GetMapping("{subject}/versions/{versionId:[\\d]+}/schema")
    suspend fun getSchemaBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable versionId: Int
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.GET,
                clusterId,
                "subjects/$subject/versions/$versionId/schema"
            )
        )
    }

    @GetMapping("{subject}/versions/{version:^latest|LATEST$}/schema")
    suspend fun getSchemaBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable version: String
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.GET,
                clusterId,
                "subjects/$subject/versions/${version.lowercase()}/schema"
            )
        )
    }

    @PostMapping("/{subject}/versions")
    suspend fun saveSchemaBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @RequestParam normalize: Boolean = false,
        @RequestBody request: SaveSchemaRequest
    ): Response {
        validationService.validate(request)

        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.POST,
                clusterId,
                if (normalize) {
                    "subjects/$subject/versions?normalize=true"
                } else {
                    "subjects/$subject/versions"
                },
                request
            )
        )
    }

    @PostMapping("{subject}")
    suspend fun saveSchemaBySubject(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @RequestParam normalize: String = "false",
        @RequestBody request: SaveSchemaRequest
    ): Response {
        validationService.validate(request)

        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.POST,
                clusterId,
                if (normalize === "true") {
                    "subjects/$subject?normalize=true"
                } else {
                    "subjects/$subject"
                },
                request
            )
        )
    }

    @DeleteMapping("{subject}/versions/{versionId}")
    suspend fun deleteSchemaBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable versionId: Int,
        @RequestParam(name = "permanent") permanent: String = "false"
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.DELETE,
                clusterId,
                if (permanent === "true") {
                    "subjects/$subject/versions/$versionId?permanent=true"
                } else {
                    "subjects/$subject/versions/$versionId"
                }
            )
        )
    }

    @DeleteMapping("{subject}/versions/{version:^latest|LATEST\$}")
    suspend fun deleteSchemaBySubjectAndVersion(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable version: String,
        @RequestParam(name = "permanent") permanent: String = "false"
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<JsonNode>(
                HttpMethod.DELETE,
                clusterId,
                "subjects/$subject/versions/${version.lowercase()}"
            )
        )
    }

    @GetMapping("{subject}/versions/{versionId}/referencedby")
    suspend fun getReferencedSchema(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable versionId: Int
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<List<Int>>(
                HttpMethod.GET,
                clusterId,
                "subjects/$subject/versions/$versionId/referencedby"
            )
        )
    }

    @GetMapping("{subject}/versions/{version:^latest|LATEST\$}/referencedby")
    suspend fun getReferencedSchema(
        @PathVariable clusterId: String,
        @PathVariable subject: String,
        @PathVariable version: String
    ): Response {
        return Response.withSuccess(
            schemaExecutor.execute<List<Int>>(
                HttpMethod.GET,
                clusterId,
                "subjects/$subject/versions/${version.lowercase()}/referencedby"
            )
        )
    }
}
