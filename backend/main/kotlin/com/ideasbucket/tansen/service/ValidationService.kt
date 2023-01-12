/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service

import com.ideasbucket.tansen.exception.ValidationException
import org.springframework.stereotype.Service
import javax.validation.ConstraintViolation
import javax.validation.Validator

@Service
class ValidationService(private val validator: Validator) {

    @Throws(ValidationException::class)
    fun validate(entity: Any) {
        val violations: Set<ConstraintViolation<Any>> = validator.validate(entity)

        if (violations.isNotEmpty()) {
            throw ValidationException(entity.javaClass.name, violations)
        }
    }
}
