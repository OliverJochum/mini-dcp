/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import aero.digitalhangar.flightsearch_app.demo.model.BookingNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.model.FlightsNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.model.ServiceNotFoundException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CustomExceptionHandlerTest {

    private final CustomExceptionHandler systemUnderTest = new CustomExceptionHandler();

    @SuppressWarnings("java:S5778")
    @Test
    void should_throw_error_if_invalid_exception_is_passed_as_argument() {
        assertThatExceptionOfType(ClassCastException.class)
            .isThrownBy(() -> systemUnderTest.handleDataNotFoundException(new RuntimeException("ABC")))
            .withMessageContainingAll(
                "class java.lang.RuntimeException cannot be cast to class",
                "commons.error.model.ErrorIdReturningException"
            );
    }

    @Test
    void should_return_proper_error_for_booking_not_found_exception() {
        assertThat(systemUnderTest.handleDataNotFoundException(new BookingNotFoundException("ABC")))
            .isEqualTo(
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                        ErrorMessage
                            .builder()
                            .type(ErrorMessage.TypeEnum.E)
                            .retryIndicator(Boolean.FALSE)
                            .processingErrors(
                                Set.of(
                                    ProcessingError
                                        .builder(UniqueError.DATA_NOT_FOUND)
                                        .description("Booking for provided ABC was not found")
                                        .build()
                                )
                            )
                            .build()
                    )
            );
    }

    @Test
    void should_return_proper_error_for_service_not_found_exception() {
        assertThat(systemUnderTest.handleDataNotFoundException(new ServiceNotFoundException("ABC", "OTXX")))
            .isEqualTo(
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                        ErrorMessage
                            .builder()
                            .type(ErrorMessage.TypeEnum.E)
                            .retryIndicator(Boolean.FALSE)
                            .processingErrors(
                                Set.of(
                                    ProcessingError
                                        .builder(UniqueError.DATA_NOT_FOUND)
                                        .description("Service for provided OTXX was not found in the ABC booking")
                                        .build()
                                )
                            )
                            .build()
                    )
            );
    }

    @Test
    void should_return_proper_error_for_flights_not_found_exception() {
        assertThat(systemUnderTest.handleDataNotFoundException(new FlightsNotFoundException()))
            .isEqualTo(
                ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                        ErrorMessage
                            .builder()
                            .type(ErrorMessage.TypeEnum.E)
                            .retryIndicator(Boolean.FALSE)
                            .processingErrors(
                                Set.of(
                                    ProcessingError
                                        .builder(UniqueError.DATA_NOT_FOUND)
                                        .description("Flight for provided parameters was not found")
                                        .build()
                                )
                            )
                            .build()
                    )
            );
    }
}
