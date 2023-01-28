/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service.impl

import com.ideasbucket.tansen.exception.SchemaRegistryNotConfiguredException
import com.ideasbucket.tansen.exception.SchemaServiceAPIException
import com.ideasbucket.tansen.service.ClusterService
import com.ideasbucket.tansen.service.SchemaService
import com.ideasbucket.tansen.util.JsonConverter
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.lang.UnsupportedOperationException

@Service
class SchemaServiceImpl(private val clusterService: ClusterService) : SchemaService {

    override suspend fun <T> get(clusterId: String, url: String, clazz: Class<T>): T {
        try {
            val response = getClient(clusterId, url, HttpMethod.GET).retrieve().toEntity(clazz).awaitSingleOrNull()

            if ((response == null) || !response.statusCode.is2xxSuccessful) {
                throw SchemaServiceAPIException(
                    response?.statusCode,
                    response?.body?.toString() ?: "Error received from Schema service",
                    null
                )
            }

            return response.body
                ?: throw SchemaServiceAPIException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Received null response from schema registry.",
                    null
                )
        } catch (exception: WebClientResponseException) {
            throw SchemaServiceAPIException(
                exception.statusCode,
                exception.message?.replace(clusterService.getSchemaRegistryUrl(clusterId), "")
                    ?: "Received invalid response. ",
                JsonConverter.parse(exception.responseBodyAsString)
            )
        } catch (exception: SchemaRegistryNotConfiguredException) {
            throw exception
        }
    }

    override suspend fun <T> post(clusterId: String, url: String, body: Any, clazz: Class<T>): T {
        try {
            // formatter:off
            val response = getClientWithBodySupport(clusterId, url, HttpMethod.POST).contentType(APPLICATION_JSON)
                                                                                    .bodyValue(body)
                                                                                    .retrieve()
                                                                                    .toEntity(clazz)
                                                                                    .awaitSingle()

            // formatter:on
            if ((response == null) || !response.statusCode.is2xxSuccessful) {
                throw SchemaServiceAPIException(
                    response?.statusCode,
                    response?.body?.toString() ?: "Error received from Schema service",
                    null
                )
            }

            return response.body
                ?: throw SchemaServiceAPIException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Received null response from schema registry.",
                    null
                )
        } catch (exception: WebClientResponseException) {
            throw SchemaServiceAPIException(
                exception.statusCode,
                exception.message?.replace(clusterService.getSchemaRegistryUrl(clusterId), "")
                    ?: "Received invalid response. ",
                JsonConverter.parse(exception.responseBodyAsString)
            )
        } catch (exception: SchemaRegistryNotConfiguredException) {
            throw exception
        }
    }

    override suspend fun <T> put(clusterId: String, url: String, body: Any, clazz: Class<T>): T {
        try {
            // formatter:off
            val response = getClientWithBodySupport(clusterId, url, HttpMethod.PUT).contentType(APPLICATION_JSON)
                                                                                   .bodyValue(body)
                                                                                   .retrieve()
                                                                                   .toEntity(clazz)
                                                                                   .awaitSingle()

            // formatter:on
            if ((response == null) || !response.statusCode.is2xxSuccessful) {
                throw SchemaServiceAPIException(
                    response?.statusCode,
                    response?.body?.toString() ?: "Error received from Schema service",
                    null
                )
            }

            return response.body
                ?: throw SchemaServiceAPIException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Received null response from schema registry.",
                    null
                )
        } catch (exception: WebClientResponseException) {
            throw SchemaServiceAPIException(
                exception.statusCode,
                exception.message?.replace(clusterService.getSchemaRegistryUrl(clusterId), "")
                    ?: "Received invalid response. ",
                JsonConverter.parse(exception.responseBodyAsString)
            )
        } catch (exception: SchemaRegistryNotConfiguredException) {
            throw exception
        }
    }

    override suspend fun <T> patch(clusterId: String, url: String, body: Any, clazz: Class<T>): T {
        try {
            // formatter:off
            val response = getClientWithBodySupport(clusterId, url, HttpMethod.PATCH).contentType(APPLICATION_JSON)
                                                                                     .bodyValue(body)
                                                                                     .retrieve()
                                                                                     .toEntity(clazz)
                                                                                     .awaitSingle()

            // formatter:on
            if ((response == null) || !response.statusCode.is2xxSuccessful) {
                throw SchemaServiceAPIException(
                    response?.statusCode,
                    response?.body?.toString() ?: "Error received from Schema service",
                    null
                )
            }

            return response.body
                ?: throw SchemaServiceAPIException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Received null response from schema registry.",
                    null
                )
        } catch (exception: WebClientResponseException) {
            throw SchemaServiceAPIException(
                exception.statusCode,
                exception.message?.replace(clusterService.getSchemaRegistryUrl(clusterId), "")
                    ?: "Received invalid response. ",
                JsonConverter.parse(exception.responseBodyAsString)
            )
        } catch (exception: SchemaRegistryNotConfiguredException) {
            throw exception
        }
    }

    override suspend fun <T> delete(clusterId: String, url: String, clazz: Class<T>): T {
        try {
            val response = getClient(clusterId, url, HttpMethod.DELETE).retrieve().toEntity(clazz).awaitSingle()

            if ((response == null) || !response.statusCode.is2xxSuccessful) {
                throw SchemaServiceAPIException(
                    response?.statusCode,
                    response?.body?.toString() ?: "Error received from Schema service",
                    null
                )
            }

            return response.body
                ?: throw SchemaServiceAPIException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Received null response from schema registry.",
                    null
                )
        } catch (exception: WebClientResponseException) {
            throw SchemaServiceAPIException(
                exception.statusCode,
                exception.message?.replace(clusterService.getSchemaRegistryUrl(clusterId), "")
                    ?: "Received invalid response. ",
                JsonConverter.parse(exception.responseBodyAsString)
            )
        } catch (exception: SchemaRegistryNotConfiguredException) {
            throw exception
        }
    }

    private fun getClient(clusterId: String, url: String, method: HttpMethod): WebClient.RequestHeadersSpec<*> {
        return when (method) {
            HttpMethod.GET -> clusterService.getSchemaRegistryClient(clusterId).get().uri(url)
            HttpMethod.DELETE -> clusterService.getSchemaRegistryClient(clusterId).delete().uri(url)
            else -> {
                throw UnsupportedOperationException("${method.name()} is not supported")
            }
        }
    }

    private fun getClientWithBodySupport(
        clusterId: String,
        url: String,
        method: HttpMethod
    ): WebClient.RequestBodySpec {
        return when (method) {
            HttpMethod.PUT -> clusterService.getSchemaRegistryClient(clusterId).put().uri(url)
            HttpMethod.PATCH -> clusterService.getSchemaRegistryClient(clusterId).patch().uri(url)
            HttpMethod.POST -> clusterService.getSchemaRegistryClient(clusterId).post().uri(url)
            else -> {
                throw UnsupportedOperationException("${method.name()} is not supported")
            }
        }
    }
}
