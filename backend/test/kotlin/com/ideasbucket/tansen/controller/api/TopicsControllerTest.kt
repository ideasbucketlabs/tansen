/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.entity.Topic
import com.ideasbucket.tansen.entity.TopicCreateRequest
import com.ideasbucket.tansen.entity.TopicEditRequest
import com.ideasbucket.tansen.util.JsonConverter.getMapper
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = [ "listeners=PLAINTEXT://localhost:9096", "port=9096" ])
class TopicsControllerTest(@Autowired private val context: ApplicationContext) {

    private val client: WebTestClient = WebTestClient.bindToApplicationContext(context).build()

    @Test
    @DirtiesContext
    fun `Can get all the topics`() {
        client.get().uri("/api/local/topics").exchange().expectStatus().is2xxSuccessful

        client
            .post()
            .uri("/api/local/topics")
            .bodyValue(TopicCreateRequest("test", 1))
            .exchange()
            .expectStatus()
            .is2xxSuccessful

        val result = client
            .get()
            .uri("/api/local/topics")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(Response::class.java)
            .returnResult()
            .responseBody

        result.shouldNotBeNull()
        (result.data as List<Any>).size.shouldBe(1)

        client.delete().uri("/api/local/topics/test").exchange().expectStatus().is2xxSuccessful
    }

    @Test
    @DirtiesContext
    fun `Returns 404 if invalid cluster name is given`() {
        client
            .get()
            .uri("/api/donotexist/topics")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    @DirtiesContext
    fun `Can get the topic if it exists`() {
        client
            .post()
            .uri("/api/local/topics")
            .bodyValue(TopicCreateRequest("test", 1))
            .exchange()
            .expectStatus()
            .is2xxSuccessful

        val result = WebTestClient.bindToApplicationContext(context).build()
            .get()
            .uri("/api/local/topics/test")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(Response::class.java)
            .returnResult()
            .responseBody

        result.shouldNotBeNull()
        result.data.shouldNotBeNull()
        val topic = getMapper().readValue(getMapper().writeValueAsString(result.data), Topic::class.java)
        topic.shouldNotBeNull()
        topic.topic.shouldBe("test")
        topic.partitions.size.shouldBe(1)

        client.delete().uri("/api/local/topics/test").exchange().expectStatus().is2xxSuccessful
    }

    @Test
    @DirtiesContext
    fun `Will return 404 if topic does not exist`() {
        client
            .get()
            .uri("/api/local/topics/test")
            .exchange()
            .expectStatus()
            .is4xxClientError
    }

    @Test
    @DirtiesContext
    fun `Can edit topic properly`() {
        val newTopicData = getMapper().readValue(
            getMapper().writeValueAsString(TopicCreateRequest("test", 1)).replace("\"delete\"", "\"compact\""),
            TopicCreateRequest::class.java
        )

        client
            .post()
            .uri("/api/local/topics")
            .bodyValue(TopicCreateRequest("test", 1))
            .exchange()
            .expectStatus()
            .is2xxSuccessful

        val resultBeforeEdit = WebTestClient.bindToApplicationContext(context).build()
            .get()
            .uri("/api/local/topics/test")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(Response::class.java)
            .returnResult()
            .responseBody

        resultBeforeEdit.shouldNotBeNull()
        resultBeforeEdit.data.shouldNotBeNull()
        val topicBeforeEdit = getMapper().readValue(
            getMapper().writeValueAsString(resultBeforeEdit.data),
            Topic::class.java
        )
        topicBeforeEdit.shouldNotBeNull()
        topicBeforeEdit.topic.shouldBe("test")
        topicBeforeEdit.partitions.size.shouldBe(1)

        client
            .put()
            .uri("/api/local/topics/test")
            .bodyValue(
                TopicEditRequest(
                    TopicCreateRequest("test", 1),
                    newTopicData
                )
            )
            .exchange()
            .expectStatus()
            .is2xxSuccessful

        val resultAfterEdit = WebTestClient.bindToApplicationContext(context).build()
            .get()
            .uri("/api/local/topics/test")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(Response::class.java)
            .returnResult()
            .responseBody

        resultAfterEdit.shouldNotBeNull()
        resultAfterEdit.data.shouldNotBeNull()
        val topicAfterEdit = getMapper().readValue(
            getMapper().writeValueAsString(resultAfterEdit.data),
            Topic::class.java
        )
        topicAfterEdit.shouldNotBeNull()
        topicAfterEdit.configurations.filter { it.name == "cleanup.policy" && it.value == "compact" }.size.shouldBe(1)
        topicAfterEdit.partitions.size.shouldBe(1)

        client.delete().uri("/api/local/topics/test").exchange().expectStatus().is2xxSuccessful
    }

    @Test
    @DirtiesContext
    fun `Can delete topic`() {
        client
            .post()
            .uri("/api/local/topics")
            .bodyValue(TopicCreateRequest("test", 1))
            .exchange()
            .expectStatus()
            .is2xxSuccessful

        client.delete().uri("/api/local/topics/test").exchange().expectStatus().is2xxSuccessful

        client
            .get()
            .uri("/api/local/topics/test")
            .exchange()
            .expectStatus()
            .is4xxClientError
    }
}
