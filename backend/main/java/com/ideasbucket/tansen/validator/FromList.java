/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ METHOD, FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { FromListValidator.class })
public @interface FromList {
    String[] acceptedValues();

    String message() default "Invalid value.";

    boolean caseSensitive() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
