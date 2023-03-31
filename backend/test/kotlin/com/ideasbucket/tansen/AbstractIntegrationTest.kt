/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen

import org.junit.ClassRule
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import java.io.File
import java.time.Duration

@SpringBootTest
@ContextConfiguration(initializers = [AbstractIntegrationTest.Initializer::class])
abstract class AbstractIntegrationTest {

    companion object {
        @JvmField
        @Container
        @ClassRule
        val container: DockerComposeContainer<*> =
            DockerComposeContainer(File("backend/test/resources/docker-compose-test.yml"))
                .withExposedService(
                    "broker_1",
                    9092,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
                .withExposedService(
                    "broker_1",
                    9101,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
                .withExposedService(
                    "schema-registry_1",
                    8081,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30))
                )
                .withLocalCompose(false)
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            val brokerHost = container.getContainerByServiceName("broker_1").get().host
            val brokerFirstPort = container.getContainerByServiceName("broker_1").get().exposedPorts[0]
            val brokerSecondPort = container.getContainerByServiceName("broker_1").get().exposedPorts[1]

            val schemaHost = container.getContainerByServiceName("schema-registry_1").get().host
            val schemaPort = container.getContainerByServiceName("schema-registry_1").get().exposedPorts[0]

            TestPropertyValues.of(
                "tansen.kafka-clusters.0.bootstrap-servers=$brokerHost:$brokerFirstPort,$brokerHost:$brokerSecondPort",
                "tansen.kafka-clusters.0.name=local",
                "tansen.kafka-clusters.0.label=local",
                "tansen.kafka-clusters.0.schema-registry-url=http://$schemaHost:$schemaPort"
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}
