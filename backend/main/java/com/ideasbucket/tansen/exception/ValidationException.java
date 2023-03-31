/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.exception;

import jakarta.validation.ConstraintViolation;
import java.util.Set;

public class ValidationException extends Exception {

    private final Set<ConstraintViolation<Object>> errors;

    public ValidationException(Set<ConstraintViolation<Object>> errors) {
        super("Invalid Data");
        this.errors = errors;
    }

    public ValidationException(String clazz, Set<ConstraintViolation<Object>> errors) {
        super("Invalid data given for entity " + clazz);
        this.errors = errors;
    }

    public Set<ConstraintViolation<Object>> getErrors() {
        return errors;
    }
}
