/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.reactive.awaitSingle
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

object JsonConverter {

    private val objectMapper = setMapper()

    /** ObjectMapper.writeValueAsString is a blocking call hence using schedulers. */
    suspend fun <T> toJson(payload: T): String {
        return Mono.fromCallable { getMapper().writeValueAsString(payload) }
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingle()
    }

    @JvmStatic
    fun getMapper(): ObjectMapper {
        return objectMapper
    }

    inline fun <reified T> parse(input: String): T? {
        return try {
            getMapper().readValue(input, T::class.java)
        } catch (exception: JsonMappingException) {
            null
        } catch (exception: JsonProcessingException) {
            null
        }
    }

    private fun setMapper(): ObjectMapper {
        return JsonMapper.builder()
            .addModules(
                JavaTimeModule(),
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            )
            .defaultDateFormat(StdDateFormat().withColonInTimeZone(true))
            .build()
    }
}
