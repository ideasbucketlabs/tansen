/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = [ "listeners=PLAINTEXT://localhost:9096", "port=9096" ])
class ClusterInformationControllerTest(@Autowired private val context: ApplicationContext) {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("tansen.kafka-clusters.0.bootstrap_servers") { "localhost:9096" }
            registry.add("tansen.kafka-clusters.0.label") { "local" }
            registry.add("tansen.kafka-clusters.0.name") { "local" }
            registry.add("tansen.kafka-clusters.0.schema-registry-url") { "http://localhost:8081" }
            registry.add("tansen.kafka-clusters.0.topic-add-allowed") { true }
            registry.add("tansen.kafka-clusters.0.topic-delete-allowed") { true }
            registry.add("tansen.kafka-clusters.0.topic-edit-allowed") { true }
        }
    }

    @Test
    fun `Can get cluster information`() {
        WebTestClient.bindToApplicationContext(context)
            .build()
            .get()
            .uri("/api/cluster-information")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
    }
}
