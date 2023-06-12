/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.validation.FieldError;

public class FormError {

    public static Map<String, String> format(List<FieldError> violations) {
        return violations
            .stream()
            .collect(
                Collectors.toMap(
                    violation -> getErrorMappedValue(violation.getField()),
                    fieldError -> (fieldError.getDefaultMessage() == null) ? "ERROR" : fieldError.getDefaultMessage(),
                    (left, right) -> left.equals(right) ? left : (left + " " + right)
                )
            );
    }

    public static Map<String, String> format(Set<? extends ConstraintViolation<?>> constraintViolations) {
        return constraintViolations
            .stream()
            .collect(
                Collectors.toMap(
                    violation -> getErrorMappedValue(violation.getPropertyPath().toString()),
                    ConstraintViolation::getMessage,
                    (left, right) -> left.equals(right) ? left : (left + ". " + right)
                )
            );
    }

    private static String getErrorMappedValue(String key) {
        if (key.equalsIgnoreCase("validMode")) {
            return "mode";
        }

        if (key.equalsIgnoreCase("validSchemaType")) {
            return "schemaType";
        }

        if (key.equalsIgnoreCase("validPartition")) {
            return "partition";
        }

        if (key.equalsIgnoreCase("getMessagesByTopic.criteria.partition")) {
            return "partition";
        }

        if (key.equalsIgnoreCase("getMessagesByTopic.criteria.offset")) {
            return "offset";
        }

        return key;
    }
}
