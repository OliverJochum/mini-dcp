/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.model;

import java.util.Arrays;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConstraintViolation {
    SIZE("SIZE"),
    MIN("MIN"),
    MAX("MAX"),
    NOT_NULL("NOTNULL"),
    NOT_EMPTY("NOTEMPTY"),
    NOT_BLANK("NOTBLANK"),
    DIGITS_ONLY("DIGITS"),
    POSITIVE_OR_ZERO("POSITIVEORZERO"),
    POSITIVE("POSITIVE"),
    NEGATIVE_OR_ZERO("NEGATIVEORZERO"),
    NEGATIVE("NEGATIVE"),
    PATTERN("PATTERN"),

    CONDITIONAL_VALUES("CONDITIONALVALUES");

    private final String name;

    public static Optional<ConstraintViolation> findByName(final String constraintName) {
        // @formatter:off
        return Arrays.stream(values())
            .filter(e -> e.name.equals(constraintName))
            .findFirst();
        // @formatter:on
    }
}
