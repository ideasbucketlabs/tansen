/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller.api

import com.ideasbucket.tansen.AbstractIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class SchemasControllerTest(@Autowired private val context: ApplicationContext) : AbstractIntegrationTest() {

    private val client: WebTestClient = WebTestClient.bindToApplicationContext(context).build()

    @Test
    fun `Returns 200 if schema registry is configured`() {
        client.get().uri("/api/local/schemas/configured").exchange().expectStatus().is2xxSuccessful
    }

    @Test
    fun `Returns 404 if schema does not exist`() {
        client.get().uri("/api/local/schemas/ids/200").exchange().expectStatus().is4xxClientError
    }

    @Test
    fun `Returns 200 for schema types`() {
        client.get().uri("/api/local/schemas/types").exchange().expectStatus().is2xxSuccessful
    }

    @Test
    fun `Returns 404 if schema versions does not exist`() {
        client.get().uri("/api/local/schemas/ids/200/versions").exchange().expectStatus().is4xxClientError
    }
}
