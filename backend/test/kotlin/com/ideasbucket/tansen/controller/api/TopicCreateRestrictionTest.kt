/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.ideasbucket.tansen.entity.TopicCreateRequest
import com.ideasbucket.tansen.entity.TopicEditRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = [ "listeners=PLAINTEXT://localhost:9096", "port=9096" ])
class TopicCreateRestrictionTest(@Autowired private val context: ApplicationContext) {

    private val client: WebTestClient = WebTestClient.bindToApplicationContext(context).build()

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
            registry.add("tansen.kafka-clusters.0.topic-edit-allowed") { false }
        }
    }

    @Test
    @DirtiesContext
    fun `Does not allow Topic creation if it is disabled`() {
        client.get().uri("/api/local/topics").exchange().expectStatus().is2xxSuccessful

        client
            .post()
            .uri("/api/local/topics")
            .bodyValue(TopicCreateRequest("test", 1))
            .exchange()
            .expectStatus()
            .is2xxSuccessful

        client
            .put()
            .uri("/api/local/topics/test")
            .bodyValue(TopicEditRequest(TopicCreateRequest("test", 1), TopicCreateRequest("test", 1)))
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_IMPLEMENTED)

        client
            .delete()
            .uri("/api/local/topics/test")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
    }
}
