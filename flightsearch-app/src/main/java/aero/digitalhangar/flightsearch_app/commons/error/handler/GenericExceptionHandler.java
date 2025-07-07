/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.handler;

import static aero.digitalhangar.flightsearch_app.commons.logging.CustomLoggingArguments.errorId;
import static aero.digitalhangar.flightsearch_app.commons.logging.CustomLoggingArguments.severity;
import static aero.digitalhangar.flightsearch_app.commons.logging.CustomLoggingArguments.stack;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import aero.digitalhangar.flightsearch_app.commons.logging.Severity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@SuppressWarnings("unused")
@Order(500) // priority lower then RequestExceptionHandler
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ControllerAdvice
@Slf4j
public class GenericExceptionHandler {

    protected static final String CUSTOM_UNIQUE_ERROR = "customUniqueError";
    protected static final String CUSTOM_MESSAGE = "customMessage";
    private static final String BROKEN_PIPE_ERROR_MESSAGE = "Broken pipe";

    private static ErrorDetailsHolder extractFrom(final ConstraintDescriptor<?> constraintDescriptor) {
        String annotationName = constraintDescriptor.getAnnotation().annotationType().getSimpleName();

        Map<String, Object> annotationAttributes = Optional
            .ofNullable(constraintDescriptor.getAttributes())
            .orElseGet(Collections::emptyMap);

        return new ErrorDetailsHolder(
            (UniqueError) annotationAttributes.getOrDefault(
                CUSTOM_UNIQUE_ERROR,
                UniqueError.parseFromConstraintName(annotationName)
            ),
            (String) annotationAttributes.get(CUSTOM_MESSAGE)
        );
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<ErrorMessage> handleConstraintViolation(final ConstraintViolationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorMessage
                    .builder()
                    .processingErrors(
                        (
                            ex
                                .getConstraintViolations()
                                .stream()
                                .map(constraintViolation -> prepareErrorMessage(constraintViolation, ex))
                                .collect(Collectors.toSet())
                        )
                    )
                    .build()
            );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(final Exception ex) {
        log.error(
            ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MAJOR),
            errorId(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ProcessingError.builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM).build().toErrorMessage());
    }

    @SuppressWarnings("PlaceholderCountMatchesArgumentCount")
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorMessage> handleIOException(final IOException exception) {
        if (StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(exception), BROKEN_PIPE_ERROR_MESSAGE)) {
            log.warn(
                "Encountered '{}' error - indicating client disconnection",
                BROKEN_PIPE_ERROR_MESSAGE,
                stack(exception)
            );
            return null;
        } else {
            log.error(
                "Encountered 'IOException' with details {}",
                exception.getLocalizedMessage(),
                severity(Severity.MAJOR),
                errorId(UniqueError.INTERNAL_ERROR),
                stack(exception)
            );

            ErrorMessage errorMessage = ProcessingError.builder(UniqueError.INTERNAL_ERROR).build().toErrorMessage();
            errorMessage.setRetryIndicator(Boolean.TRUE);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericError(final Exception ex) {
        log.error(
            ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MAJOR),
            errorId(UniqueError.UNDEFINED_INTERNAL_ERROR)
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ProcessingError.builder(UniqueError.INTERNAL_ERROR).build().toErrorMessage());
    }

    private ProcessingError prepareErrorMessage(
        final ConstraintViolation<?> constraintViolation,
        final ConstraintViolationException constraintViolationException
    ) {
        ErrorDetailsHolder errorDetailsHolder = extractFrom(constraintViolation.getConstraintDescriptor());
        log.warn("{} -> {}", constraintViolationException.getLocalizedMessage(), errorDetailsHolder.uniqueError);

        return ProcessingError.builder(errorDetailsHolder.uniqueError).description(errorDetailsHolder.details).build();
    }

    @SuppressWarnings({ "java:S100", "java:S1186" }) //false-positive
    private record ErrorDetailsHolder(UniqueError uniqueError, String details) {}
}
