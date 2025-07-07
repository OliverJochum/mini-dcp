/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.model;

import java.util.Locale;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public enum UniqueError implements ErrorId {
    PARAMETER_MISSING_VALUE("40001", "Value missing in field"),
    PARAMETER_INVALID_LENGTH("40002", "Invalid value length in field"),
    PARAMETER_INCORRECT_FORMAT("40003", "Incorrect format in field"),
    PARAMETER_VALUE_NOT_IN_LIST("40004", "Value of field not in list"),
    PARAMETER_OUT_OF_RANGE("40005", "Value of field is out of range"),

    CONDITIONAL_VALUES_INCORRECT("40010", "Conditional values not provided as expected"),

    PARAMETER_UNSPECIFIED_PROBLEM("40099", "Invalid input value"),

    UNAUTHORIZED("40100", "Authorization has been refused"),

    FORBIDDEN("40300", "Insufficient authentication credentials to grant access"),

    DATA_NOT_FOUND("40400", "Requested data could not be found"),

    METHOD_NOT_ALLOWED("40500", "Method Not Allowed"),

    CONFLICT("40900", "Conflict with the current state of the target resource"),

    NOT_ACCEPTABLE(
        "40600",
        "Not Acceptable - resource does not have a current representation " +
        "that would be acceptable to the user agent"
    ),

    UNSUPPORTED_MEDIA_TYPE("41500", "Content type not supported"),

    INTERNAL_ERROR("50000", "An unexpected error occurred"),

    UNDEFINED_INTERNAL_ERROR("50099", "Undefined error"),
    NOT_IMPLEMENTED("50100", "Operation for this request has not been implemented yet");

    @Getter
    private final String code;

    @Getter
    private final String description;

    @SuppressWarnings({ "java:S103" })
    public static UniqueError parseFromConstraintName(final String constraintName) {
        return getConstraintViolationIfMatched(constraintName)
            .map(violation ->
                switch (violation) {
                    case SIZE -> UniqueError.PARAMETER_INVALID_LENGTH;
                    case NOT_BLANK, NOT_EMPTY, NOT_NULL -> UniqueError.PARAMETER_MISSING_VALUE;
                    case PATTERN, DIGITS_ONLY -> UniqueError.PARAMETER_INCORRECT_FORMAT;
                    case POSITIVE_OR_ZERO,
                        POSITIVE,
                        NEGATIVE_OR_ZERO,
                        NEGATIVE,
                        MIN,
                        MAX -> UniqueError.PARAMETER_OUT_OF_RANGE;
                    case CONDITIONAL_VALUES -> UniqueError.CONDITIONAL_VALUES_INCORRECT;
                }
            )
            .orElse(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM);
    }

    private static Optional<ConstraintViolation> getConstraintViolationIfMatched(final String constraintName) {
        Optional<ConstraintViolation> constraintViolation = Optional
            .ofNullable(constraintName)
            .flatMap(constraint -> ConstraintViolation.findByName(constraint.toUpperCase(Locale.getDefault())));

        if (constraintViolation.isEmpty()) {
            log.info("Not supported ConstraintViolation found - {}", constraintName);
        }

        return constraintViolation;
    }
}
