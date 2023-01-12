/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity

import com.ideasbucket.tansen.exception.ValidationException
import com.ideasbucket.tansen.service.ValidationService
import com.ideasbucket.tansen.util.FormError
import com.ideasbucket.tansen.util.JsonConverter
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import javax.validation.Validation

class NewTopicCreateRequestTest {

    private val objectMapper = JsonConverter.getMapper()
    private val validationService = ValidationService(Validation.buildDefaultValidatorFactory().validator)

    @Test
    fun `Can decode JSON properly`() {
        val json = """
    {"cleanup.policy":"compact,delete","compression.type":"producer","delete.retention.ms":"86400000","file.delete.delay.ms":"60000","flush.messages":"922337203685477580","flush.ms":"9223372036854775807","follower.replication.throttled.replicas":"","index.interval.bytes":4096,"leader.replication.throttled.replicas":"","max.compaction.lag.ms":"9223372036854775807","max.message.bytes":1048588,"message.downconversion.enable":true,"message.timestamp.difference.max.ms":"9223372036854775807","message.timestamp.type":"CreateTime","min.cleanable.dirty.ratio":0.5,"min.compaction.lag.ms":"0","min.insync.replicas":2,"replication.factor":3,"retention.bytes":"-1","retention.ms":"604800000","segment.bytes":1073741824,"segment.index.bytes":10485760,"segment.jitter.ms":"0","segment.ms":"604800000","unclean.leader.election.enable":false,"name":"Test","partition":1,"preallocate":false}
        """.trimIndent()

        val data = objectMapper.readValue(json, NewTopicCreateRequest::class.java)
        data.shouldBeInstanceOf<NewTopicCreateRequest>()
        try {
            validationService.validate(data)
        } catch (exception: ValidationException) {
            fail(exception)
        }
    }

    @Test
    fun `Can validate clean up policy properly`() {
        val json = """
    {"cleanup.policy":"","compression.type":"producer","delete.retention.ms":"86400000","file.delete.delay.ms":"60000","flush.messages":"922337203685477580","flush.ms":"9223372036854775807","follower.replication.throttled.replicas":"","index.interval.bytes":4096,"leader.replication.throttled.replicas":"","max.compaction.lag.ms":"9223372036854775807","max.message.bytes":1048588,"message.downconversion.enable":true,"message.timestamp.difference.max.ms":"9223372036854775807","message.timestamp.type":"CreateTime","min.cleanable.dirty.ratio":0.5,"min.compaction.lag.ms":"0","min.insync.replicas":2,"replication.factor":3,"retention.bytes":"-1","retention.ms":"604800000","segment.bytes":1073741824,"segment.index.bytes":10485760,"segment.jitter.ms":"0","segment.ms":"604800000","unclean.leader.election.enable":false,"name":"Test","partition":1,"preallocate":false}
        """.trimIndent()

        FormError.format(
            shouldThrowExactly<ValidationException> {
                validationService.validate(objectMapper.readValue(json, NewTopicCreateRequest::class.java))
            }.errors
        ).shouldContain("cleanupPolicy", "cleanup.policy must be either delete or compact or compact, delete.")
    }

    @Test
    fun canValidateCompressionType() {
        val json = """
    {"cleanup.policy":"delete","compression.type":"","delete.retention.ms":"86400000","file.delete.delay.ms":"60000","flush.messages":"922337203685477580","flush.ms":"9223372036854775807","follower.replication.throttled.replicas":"","index.interval.bytes":4096,"leader.replication.throttled.replicas":"","max.compaction.lag.ms":"9223372036854775807","max.message.bytes":1048588,"message.downconversion.enable":true,"message.timestamp.difference.max.ms":"9223372036854775807","message.timestamp.type":"CreateTime","min.cleanable.dirty.ratio":0.5,"min.compaction.lag.ms":"0","min.insync.replicas":2,"replication.factor":3,"retention.bytes":"-1","retention.ms":"604800000","segment.bytes":1073741824,"segment.index.bytes":10485760,"segment.jitter.ms":"0","segment.ms":"604800000","unclean.leader.election.enable":false,"name":"Test","partition":1,"preallocate":false}
        """.trimIndent()

        FormError.format(
            shouldThrowExactly<ValidationException> {
                validationService.validate(objectMapper.readValue(json, NewTopicCreateRequest::class.java))
            }.errors
        ).shouldContain(
            "compressionType",
            "Compression type must be producer or uncompressed or zstd or lz4 or snappy or gzip."
        )
    }
}
