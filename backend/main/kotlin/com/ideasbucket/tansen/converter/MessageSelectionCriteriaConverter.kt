/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.converter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ideasbucket.tansen.entity.MessageSelectionCriteria
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class MessageSelectionCriteriaConverter(private val objectMapper: ObjectMapper) :
    Converter<String, MessageSelectionCriteria> {

    override fun convert(source: String): MessageSelectionCriteria {
        return try {
            objectMapper.readValue(source)
        } catch (exception: JsonProcessingException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid criteria given.")
        } catch (exception: JsonMappingException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid criteria given.")
        }
    }
}
