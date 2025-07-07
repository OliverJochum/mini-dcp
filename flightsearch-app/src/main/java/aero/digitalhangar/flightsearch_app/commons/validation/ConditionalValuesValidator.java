/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConditionalValuesValidator implements ConstraintValidator<ConditionalValues, ConditionalValuesHolder> {

    @Override
    public void initialize(final ConditionalValues constraintAnnotation) {
        // intentionally left empty
    }

    @Override
    public boolean isValid(final ConditionalValuesHolder valuesHolder, final ConstraintValidatorContext context) {
        return valuesHolder.areValid();
    }
}
