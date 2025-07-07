/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class DefaultErrorControllerTest {

    @SuppressWarnings("ConstantConditions")
    static final Function<ResponseEntity<ErrorMessage>, ProcessingError> firstProcessingError = responseEntity ->
        responseEntity.getBody().getProcessingErrors().iterator().next();

    private final DefaultErrorController systemUnderTest = new DefaultErrorController();

    private static Stream<Arguments> getTestCases() {
        return Stream.of(
            arguments(
                null,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProcessingError.builder(UniqueError.INTERNAL_ERROR).description("Requested URI: dummy").build()
            ),
            arguments(
                0,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProcessingError.builder(UniqueError.INTERNAL_ERROR).description("Requested URI: dummy").build()
            ),
            arguments(
                200,
                HttpStatus.OK,
                ProcessingError.builder(UniqueError.INTERNAL_ERROR).description("Requested URI: dummy").build()
            ),
            arguments(
                400,
                HttpStatus.BAD_REQUEST,
                ProcessingError
                    .builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
                    .description("Requested URI: dummy")
                    .build()
            ),
            arguments(
                401,
                HttpStatus.UNAUTHORIZED,
                ProcessingError.builder(UniqueError.INTERNAL_ERROR).description("Requested URI: dummy").build()
            ),
            arguments(
                404,
                HttpStatus.NOT_FOUND,
                ProcessingError.builder(UniqueError.DATA_NOT_FOUND).description("Requested URI: dummy").build()
            ),
            arguments(
                500,
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProcessingError.builder(UniqueError.INTERNAL_ERROR).description("Requested URI: dummy").build()
            )
        );
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    void should_resolve_error_for_invalid_request(
        final Integer httpStatusCode,
        final HttpStatus expectedStatus,
        final ProcessingError expectedError
    ) {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(httpStatusCode);
        when(httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("dummy");

        assertThat(systemUnderTest.handleError(httpServletRequest))
            .extracting(ResponseEntity::getStatusCode, firstProcessingError)
            .containsExactly(expectedStatus, expectedError);
    }
}
