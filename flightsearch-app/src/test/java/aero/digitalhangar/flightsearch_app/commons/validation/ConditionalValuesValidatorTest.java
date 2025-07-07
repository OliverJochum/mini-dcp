/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.util.StringUtils;

class ConditionalValuesValidatorTest {

    @SuppressWarnings("resource")
    private final Validator validator = Validation
        .byDefaultProvider()
        .configure()
        .buildValidatorFactory()
        .getValidator();

    private static Stream<Arguments> validTestData() {
        return Stream.of(
            arguments((ConditionalValuesHolder) () -> true),
            arguments((ConditionalValuesHolder) () -> false),
            arguments(new DummyHolder("value", null)),
            arguments(new DummyHolder(null, 1)),
            arguments(new DummyHolder("", 1)),
            arguments(new DummyHolder(" ", 1)),
            arguments(new DummyHolder("value", 1))
        );
    }

    private static Stream<Arguments> notValidTestData() {
        return Stream.of(
            arguments(new DummyHolder(null, null)),
            arguments(new DummyHolder("", null)),
            arguments(new DummyHolder(" ", null))
        );
    }

    @ParameterizedTest
    @MethodSource("validTestData")
    void should_pass_validation_for_valid_input(final ConditionalValuesHolder conditionalValuesHolder) {
        assertThat(validator.validate(conditionalValuesHolder)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("notValidTestData")
    void should_fail_validation_for_invalid_input(final ConditionalValuesHolder conditionalValuesHolder) {
        assertThat(validator.validate(conditionalValuesHolder)).isNotEmpty().hasSize(1);
    }

    @ConditionalValues(customMessage = "Testing - not provided")
    record DummyHolder(String valueA, Integer valueB) implements ConditionalValuesHolder {
        @Override
        public boolean areValid() {
            return StringUtils.hasText(valueA) || Objects.nonNull(valueB);
        }
    }
}
