/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class ConsumerFactory(private val commonKakaProperties: Map<String, String>) {

    fun createConsumer(): Consumer<String, String> {
        val properties: MutableMap<String, Any> = HashMap<String, Any>(commonKakaProperties)
        properties[ConsumerConfig.GROUP_ID_CONFIG] = "tansen-consumer-group-" + UUID.randomUUID().toString()
        properties[ConsumerConfig.CLIENT_ID_CONFIG] = "tansen-consumer-client-" + UUID.randomUUID().toString()
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java

        return DefaultKafkaConsumerFactory<String, String>(properties).createConsumer()
    }
}
