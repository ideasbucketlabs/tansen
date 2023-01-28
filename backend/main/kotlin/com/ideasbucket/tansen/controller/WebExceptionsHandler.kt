/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller

import com.ideasbucket.tansen.entity.Response
import com.ideasbucket.tansen.exception.InvalidClusterException
import com.ideasbucket.tansen.exception.NotSupportedException
import com.ideasbucket.tansen.exception.SchemaRegistryNotConfiguredException
import com.ideasbucket.tansen.exception.SchemaServiceAPIException
import com.ideasbucket.tansen.exception.TopicAlreadyExistException
import com.ideasbucket.tansen.exception.TopicOperationException
import com.ideasbucket.tansen.exception.ValidationException
import com.ideasbucket.tansen.util.FormError
import org.apache.kafka.common.errors.InvalidTopicException
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException
import org.slf4j.LoggerFactory
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import java.lang.reflect.UndeclaredThrowableException

@RestControllerAdvice
class WebExceptionsHandler {

    private val logger = LoggerFactory.getLogger(WebExceptionsHandler::class.java)

    @ExceptionHandler(MethodNotAllowedException::class)
    suspend fun handleMethodNotAllowedError(exception: MethodNotAllowedException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(HttpStatus.METHOD_NOT_ALLOWED, exception.message)
    }

    @ExceptionHandler(InvalidClusterException::class)
    suspend fun handleInvalidClusterException(exception: InvalidClusterException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(HttpStatus.NOT_FOUND, exception.message ?: "Invalid cluster")
    }

    @ExceptionHandler(UnknownTopicOrPartitionException::class)
    suspend fun handleTopicNotFoundError(exception: UnknownTopicOrPartitionException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(HttpStatus.NOT_FOUND, exception.message!!)
    }

    @ExceptionHandler(InvalidTopicException::class)
    suspend fun handleInvalidTopicError(exception: InvalidTopicException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(HttpStatus.NOT_FOUND, exception.message!!)
    }

    @ExceptionHandler(TopicAlreadyExistException::class)
    suspend fun handleTopicAlreadyExistError(exception: TopicAlreadyExistException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.message!!)
    }

    @ExceptionHandler(TopicOperationException::class)
    suspend fun handleTopicCreationError(exception: TopicOperationException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            exception.message!!
        )
    }

    @ExceptionHandler(SchemaServiceAPIException::class)
    suspend fun handleSchemaServiceAPIExceptionError(exception: SchemaServiceAPIException): ResponseEntity<Response> {
        return ResponseEntity(
            Response.withError(
                mapOf(
                    "httpCode" to exception.httpStatus.value(),
                    "httpStatus" to exception.httpStatus,
                    "response" to exception.errorResponse,
                    "detail" to exception.apiErrorResponse
                )
            ),
            exception.httpStatus
        )
    }

    @ExceptionHandler(ServerWebInputException::class)
    suspend fun handleDecodingError(exception: ServerWebInputException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(HttpStatus.BAD_REQUEST, exception.message)
    }

    @ExceptionHandler(DecodingException::class)
    suspend fun handleDecodingError(exception: DecodingException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(
            HttpStatus.BAD_REQUEST,
            exception.message ?: "Bad input"
        )
    }

    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun handleValidationError(exception: WebExchangeBindException): ResponseEntity<Response> {
        if ((exception.reason == null) || (exception.reason != "Validation failure") || !exception.hasFieldErrors()) {
            return getHttpResponseWithoutDetail(HttpStatus.BAD_REQUEST, exception.message)
        }

        if ((exception.reason == "Validation failure") && (exception.bindingResult.fieldErrors.isNotEmpty())) {
            return ResponseEntity(
                Response.withError(FormError.format(exception.bindingResult.fieldErrors)),
                HttpStatus.UNPROCESSABLE_ENTITY
            )
        }

        return getHttpResponseWithoutDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.message)
    }

    @ExceptionHandler(ValidationException::class)
    suspend fun handleValidationError(exception: ValidationException): ResponseEntity<Response> {
        return ResponseEntity(
            Response.withError(FormError.format(exception.errors)),
            HttpStatus.UNPROCESSABLE_ENTITY
        )
    }

    @ExceptionHandler(SchemaRegistryNotConfiguredException::class, NotSupportedException::class)
    suspend fun handleSchemaRegistryNotConfiguredException(exception: Exception): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(HttpStatus.NOT_IMPLEMENTED, exception.message!!)
    }

    @ExceptionHandler(ResponseStatusException::class)
    suspend fun handleResponseStatusException(exception: ResponseStatusException): ResponseEntity<Response> {
        return getHttpResponseWithoutDetail(exception.statusCode, exception.message)
    }

    @ExceptionHandler(UndeclaredThrowableException::class)
    suspend fun handleUndeclaredException(
        exception: UndeclaredThrowableException,
        exchange: ServerWebExchange
    ): ResponseEntity<Response> {
        if (exception.undeclaredThrowable == null) {
            return handleAllError(exception, exchange)
        }

        return when (val inferredException = exception.undeclaredThrowable) {
            is SchemaServiceAPIException -> handleSchemaServiceAPIExceptionError(inferredException)
            is InvalidTopicException -> handleInvalidTopicError(inferredException)
            is SchemaRegistryNotConfiguredException -> handleSchemaRegistryNotConfiguredException(inferredException)
            is TopicOperationException -> handleTopicCreationError(inferredException)
            is TopicAlreadyExistException -> handleTopicAlreadyExistError(inferredException)
            is ValidationException -> handleValidationError(inferredException)
            else -> handleAllError(exception, exchange)
        }
    }

    // @ExceptionHandler(Exception::class)
    suspend fun handleAllError(exception: Exception, exchange: ServerWebExchange): ResponseEntity<Response> {
        // This happens when user is trying to submit data in other format other than JSON which we
        // do not support.
        if (exception.message.equals(
                "In a WebFlux application, form data is accessed via ServerWebExchange.getFormData()."
            )
        ) {
            return getHttpResponseWithoutDetail(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.reasonPhrase)
        }

        // For some reason Actuator 400 are getting here...only happening in prod.
        // TODO: Investigate this issue as to why "handleDecodingError" is not catching this.
        if (exception.message.equals("Each tag parameter must be in the form 'key:value'", ignoreCase = true)) {
            return getHttpResponseWithoutDetail(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.reasonPhrase
            )
        }

        logger.error(exception.message, exception)

        return getHttpResponseWithoutDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception.message ?: HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
        )
    }

    private fun getHttpResponseWithoutDetail(httpStatus: HttpStatusCode, message: String): ResponseEntity<Response> {
        return ResponseEntity(
            Response.withError(
                mapOf(
                    "httpCode" to httpStatus.value(),
                    "httpStatus" to httpStatus,
                    "response" to message
                )
            ),
            httpStatus
        )
    }
}
