/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.validation;

import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConditionalValuesValidator.class)
public @interface ConditionalValues {
    @SuppressWarnings("unused")
    String message() default "Conditional values not provided as expected";

    @SuppressWarnings("unused")
    Class<?>[] groups() default {};

    @SuppressWarnings("unused")
    Class<? extends Payload>[] payload() default {};

    @SuppressWarnings("unused")
    UniqueError customUniqueError() default UniqueError.CONDITIONAL_VALUES_INCORRECT;

    @SuppressWarnings("unused")
    String customMessage() default "Conditional values not provided as expected";
}
