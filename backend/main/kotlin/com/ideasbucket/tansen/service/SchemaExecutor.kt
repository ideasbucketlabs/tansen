/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service

import com.ideasbucket.tansen.exception.NotSupportedException
import com.ideasbucket.tansen.exception.SchemaServiceAPIException
import org.springframework.http.HttpMethod
import kotlin.jvm.Throws

class SchemaExecutor(val schemaService: SchemaService) {

    @Throws(NotSupportedException::class, SchemaServiceAPIException::class)
    suspend inline fun <reified T> execute(method: HttpMethod, clusterId: String, url: String): T? {
        return when (method) {
            HttpMethod.GET -> schemaService.get(clusterId, url, T::class.java)
            HttpMethod.DELETE -> schemaService.delete(clusterId, url, T::class.java)
            else -> throw NotSupportedException("${method.name} is not supported")
        }
    }

    suspend inline fun <reified T> execute(method: HttpMethod, clusterId: String, url: String, data: Any): T? {
        return when (method) {
            HttpMethod.PUT -> schemaService.put(clusterId, url, data, T::class.java)
            HttpMethod.POST -> schemaService.post(clusterId, url, data, T::class.java)
            HttpMethod.PATCH -> schemaService.patch(clusterId, url, data, T::class.java)
            else -> throw NotSupportedException("${method.name} is not supported")
        }
    }
}
