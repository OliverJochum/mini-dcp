/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UniqueErrorTest {

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, String> seen = new ConcurrentHashMap<>();
        return t -> seen.put(keyExtractor.apply(t), "") != null;
    }

    private static Stream<Arguments> getUniqueErrorTestCases() {
        return Stream.of(
            Arguments.arguments("size", UniqueError.PARAMETER_INVALID_LENGTH),
            Arguments.arguments("sIzE", UniqueError.PARAMETER_INVALID_LENGTH),
            Arguments.arguments("SIZE ", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("notblank", UniqueError.PARAMETER_MISSING_VALUE),
            Arguments.arguments("not_blank", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("notnull", UniqueError.PARAMETER_MISSING_VALUE),
            Arguments.arguments("not_null", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("NotEmpty", UniqueError.PARAMETER_MISSING_VALUE),
            Arguments.arguments("not_empty", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("digits", UniqueError.PARAMETER_INCORRECT_FORMAT),
            Arguments.arguments("DIGITS", UniqueError.PARAMETER_INCORRECT_FORMAT),
            Arguments.arguments("POSITIVE_OR_ZERO", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("positive_or_zero", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("positiveorzero", UniqueError.PARAMETER_OUT_OF_RANGE),
            Arguments.arguments("POSITIVEORZERO", UniqueError.PARAMETER_OUT_OF_RANGE),
            Arguments.arguments("Negative", UniqueError.PARAMETER_OUT_OF_RANGE),
            Arguments.arguments("Positive", UniqueError.PARAMETER_OUT_OF_RANGE),
            Arguments.arguments("NegativeOrZero", UniqueError.PARAMETER_OUT_OF_RANGE),
            Arguments.arguments("DURATIONMAX", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("durationmax", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("SomeUnhandledError", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM),
            Arguments.arguments("", UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
        );
    }

    @Test
    void should_unique_error_codes_be_unique() {
        assertThat(
            Arrays
                .stream(UniqueError.values())
                .filter(distinctByKey(UniqueError::getCode))
                .map(UniqueError::getCode)
                .collect(Collectors.toList())
        )
            .as("Found non-unique code(s): ")
            .isEmpty();
    }

    @ParameterizedTest(name = "Constraint name: {0}")
    @MethodSource(value = "getUniqueErrorTestCases")
    void should_parse_constraint_name(final String constraintName, final UniqueError uniqueError) {
        assertThat(UniqueError.parseFromConstraintName(constraintName)).isEqualTo(uniqueError);
    }

    @Test
    void should_return_unspecified_problem_when_constraint_name_is_null() {
        assertThat(UniqueError.parseFromConstraintName(null)).isEqualTo(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM);
    }
}
