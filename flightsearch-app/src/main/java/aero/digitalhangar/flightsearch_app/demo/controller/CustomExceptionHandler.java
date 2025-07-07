/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.controller;

import static aero.digitalhangar.flightsearch_app.commons.logging.CustomLoggingArguments.errorId;
import static aero.digitalhangar.flightsearch_app.commons.logging.CustomLoggingArguments.severity;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorIdReturningException;
import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.logging.Severity;
import aero.digitalhangar.flightsearch_app.demo.model.BookingNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.model.FlightsNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.model.ServiceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler implements Ordered {

    @ExceptionHandler(
        { BookingNotFoundException.class, ServiceNotFoundException.class, FlightsNotFoundException.class }
    )
    public ResponseEntity<ErrorMessage> handleDataNotFoundException(final Exception cause) {
        ErrorIdReturningException exception = (ErrorIdReturningException) cause;
        log.warn(
            "{} - {} - {} - {}",
            exception.getErrorId().getDescription(),
            cause.getLocalizedMessage(),
            severity(Severity.MINOR),
            errorId(exception.getErrorId())
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ProcessingError
                    .builder(exception.getErrorId())
                    .description(cause.getLocalizedMessage())
                    .build()
                    .toErrorMessage()
            );
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
