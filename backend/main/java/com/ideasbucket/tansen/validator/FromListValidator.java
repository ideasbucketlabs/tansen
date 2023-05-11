/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class FromListValidator implements ConstraintValidator<FromList, String> {

    private final Set<String> valueList = new HashSet<>();

    private Boolean isCaseSensitiveComparison;

    public void initialize(FromList constraintAnnotation) {
        isCaseSensitiveComparison = constraintAnnotation.caseSensitive();

        for (String val : constraintAnnotation.acceptedValues()) {
            if (isCaseSensitiveComparison) {
                valueList.add(val);
            } else {
                valueList.add(val.toLowerCase());
            }
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (isCaseSensitiveComparison) ? valueList.contains(value) : valueList.contains(value.toLowerCase());
    }
}
