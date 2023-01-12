/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.converter

import com.ideasbucket.tansen.util.JsonConverter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException

internal class MessageSelectionCriteriaConverterTest {

    private val converter = MessageSelectionCriteriaConverter(JsonConverter.getMapper())

    @Test
    fun `Can convert criteria to valid object`() {
        shouldThrow<ResponseStatusException> { converter.convert("") }
        shouldThrow<ResponseStatusException> {
            converter.convert(
                """
            {
                "offset": 0,
                "partition" : 0,
                "timestamp" : "202209-21T20:05:49.081Z"
            }
                """.trimIndent()
            )
        }
        converter
            .convert(
                """
            {
                "offset": 0,
                "partition" : 0,
                "timestamp" : null
            }
                """.trimIndent()
            )
            .shouldNotBeNull()
        converter.convert(
            """
            {
                "offset": 0,
                "partition" : 0,
                "timestamp" : "2022-09-21T20:05:49.081Z"
            }
            """.trimIndent()
        )
            .run {
                this.shouldNotBeNull()
                this.timestamp.epochSecond.shouldBe(1663790749L)
                this.timestamp.toEpochMilli().shouldBe(1663790749081L)
                this.offset.shouldNotBeNull()
                this.offset.shouldBe(0)
                this.partition.shouldNotBeNull()
                this.partition.shouldBe(0)
            }
    }
}
