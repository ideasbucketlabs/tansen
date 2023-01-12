/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service

import com.ideasbucket.tansen.exception.InvalidClusterException
import com.ideasbucket.tansen.exception.SchemaRegistryNotConfiguredException
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.web.reactive.function.client.WebClient
import kotlin.jvm.Throws

interface ClusterService {

    @Throws(InvalidClusterException::class)
    fun getConsumer(clusterId: String): Consumer<String, String>

    @Throws(InvalidClusterException::class)
    fun getAdminClient(clusterId: String): AdminClient

    fun getAdminClients(): Map<String, AdminClient>

    fun getClusterLabels(): Map<String, String>

    @Throws(SchemaRegistryNotConfiguredException::class)
    fun getSchemaRegistryClient(clusterId: String): WebClient

    @Throws(SchemaRegistryNotConfiguredException::class)
    fun getSchemaRegistryUrl(clusterId: String): String

    @Throws(InvalidClusterException::class)
    fun getCommonConfiguration(clusterId: String): Map<String, String>

    fun isTopicAddAllowed(clusterId: String): Boolean

    fun isTopicEditAllowed(clusterId: String): Boolean

    fun isTopicDeleteAllowed(clusterId: String): Boolean
}
