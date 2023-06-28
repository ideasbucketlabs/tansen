/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util

import com.fasterxml.jackson.databind.node.ObjectNode
import com.ideasbucket.tansen.configuration.auth.AuthenticationProperties
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

object RequestResponseUtil {

    @JvmStatic
    fun isAjaxRequest(httpHeaders: HttpHeaders): Boolean {
        return httpHeaders.getFirst("X-Requested-With")?.equals("XMLHttpRequest", ignoreCase = true) ?: false
    }

    @JvmStatic
    fun setJsonResponse(message: ObjectNode, swe: ServerWebExchange, httpStatus: HttpStatus): Mono<Void> {
        return setJsonResponse(JsonConverter.getMapper().writeValueAsString(message), swe, httpStatus)
    }

    @JvmStatic
    fun getLoginPath(authenticationProperties: AuthenticationProperties): String {
        if (authenticationProperties.oauth2.clients.size > 1) {
            return "/login"
        }

        return "/oauth2/authorization/${authenticationProperties.oauth2.clients.keys.first()}"
    }

    private fun setJsonResponse(message: String, swe: ServerWebExchange, httpStatus: HttpStatus): Mono<Void> {
        val bytes: ByteArray = message.toByteArray(StandardCharsets.UTF_8)
        val buffer: DataBuffer = swe.response.bufferFactory().wrap(bytes)
        return Mono
            .just<String>(message)
            .flatMap {
                val response: ServerHttpResponse = swe.response
                response.setStatusCode(httpStatus)
                response.headers.contentType = MediaType.APPLICATION_JSON
                response.headers.contentLength = buffer.readableByteCount().toLong()
                response.writeWith(Mono.just<DataBuffer>(buffer))
            }
            .doOnSuccess { DataBufferUtils.release(buffer) }
    }
}
