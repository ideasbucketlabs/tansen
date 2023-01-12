/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service

import com.ideasbucket.tansen.exception.SchemaRegistryNotConfiguredException
import com.ideasbucket.tansen.exception.SchemaServiceAPIException
import kotlin.jvm.Throws

interface SchemaService {

    @Throws(SchemaServiceAPIException::class, SchemaRegistryNotConfiguredException::class)
    suspend fun <T> get(clusterId: String, url: String, clazz: Class<T>): T

    @Throws(SchemaServiceAPIException::class, SchemaRegistryNotConfiguredException::class)
    suspend fun <T> post(clusterId: String, url: String, body: Any, clazz: Class<T>): T

    @Throws(SchemaServiceAPIException::class, SchemaRegistryNotConfiguredException::class)
    suspend fun <T> put(clusterId: String, url: String, body: Any, clazz: Class<T>): T

    @Throws(SchemaServiceAPIException::class, SchemaRegistryNotConfiguredException::class)
    suspend fun <T> patch(clusterId: String, url: String, body: Any, clazz: Class<T>): T

    @Throws(SchemaServiceAPIException::class, SchemaRegistryNotConfiguredException::class)
    suspend fun <T> delete(clusterId: String, url: String, clazz: Class<T>): T
}
