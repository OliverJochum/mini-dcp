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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MimeType;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SuppressWarnings("java:S1200") //generic handler
@Order(200) // priority higher then GenericExceptionHandler
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

    private final HttpMessageNotReadableHandler httpMessageNotReadableHandler;

    private static ErrorMessage errorOf(final TypeMismatchException ex) {
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return ProcessingError
                .builder(UniqueError.PARAMETER_INCORRECT_FORMAT)
                .description(((MethodArgumentTypeMismatchException) (ex)).getName())
                .build()
                .toErrorMessage();
        }

        return ProcessingError.builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM).build().toErrorMessage();
    }

    private static UniqueError extractFrom(final ObjectError error) {
        // @formatter:off
        return Optional
            .ofNullable(error.getArguments())
            .flatMap(arguments ->
                Arrays.stream(arguments)
                    .filter(UniqueError.class::isInstance)
                    .map(UniqueError.class::cast)
                    .findFirst()
            )
            .orElseGet(() -> UniqueError.parseFromConstraintName(error.getCode()));
        // @formatter:on
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        final HttpMessageNotReadableException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        log.error(
            request.getDescription(false) + "; " + ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MAJOR),
            errorId(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
        );

        return handleExceptionInternal(
            ex,
            httpMessageNotReadableHandler.transformExceptionToProcessingError(ex).toErrorMessage(),
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @SuppressWarnings("squid:S2589")
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        final MethodArgumentNotValidException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        log.error(
            request.getDescription(false) + "; " + ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MINOR),
            errorId(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
        );

        List<ObjectError> errors = ex.getBindingResult().getAllErrors();

        return handleValidationErrors(ex, headers, request, errors);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        final MissingServletRequestParameterException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        log.error(
            request.getDescription(false) + "; " + ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MINOR),
            errorId(UniqueError.PARAMETER_MISSING_VALUE)
        );

        return handleExceptionInternal(
            ex,
            ProcessingError
                .builder(UniqueError.PARAMETER_MISSING_VALUE)
                .description(ex.getParameterName())
                .build()
                .toErrorMessage(),
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
        final ServletRequestBindingException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        log.error(
            request.getDescription(false) + "; " + ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MINOR),
            errorId(UniqueError.PARAMETER_MISSING_VALUE)
        );

        ErrorMessage errorMessage;

        if (ex instanceof MissingRequestHeaderException exception) {
            errorMessage =
                ProcessingError
                    .builder(UniqueError.PARAMETER_MISSING_VALUE)
                    .description(exception.getHeaderName())
                    .build()
                    .toErrorMessage();
        } else {
            errorMessage =
                ProcessingError
                    .builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
                    .description(ex.getLocalizedMessage())
                    .build()
                    .toErrorMessage();
        }

        return handleExceptionInternal(ex, errorMessage, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        final TypeMismatchException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        log.error(
            request.getDescription(false) + "; " + ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MINOR),
            errorId(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
        );
        return handleExceptionInternal(ex, errorOf(ex), headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        final HttpRequestMethodNotSupportedException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        log.error(
            request.getDescription(false) + "; " + ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MINOR),
            errorId(UniqueError.METHOD_NOT_ALLOWED)
        );

        return handleExceptionInternal(
            ex,
            ProcessingError
                .builder(UniqueError.METHOD_NOT_ALLOWED)
                .description(
                    "Supported methods: " +
                    String.join(", ", Optional.ofNullable(ex.getSupportedMethods()).orElseGet(() -> new String[] {}))
                )
                .build()
                .toErrorMessage(),
            headers,
            HttpStatus.METHOD_NOT_ALLOWED,
            request
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
        final HttpMediaTypeNotSupportedException ex,
        final HttpHeaders headers,
        final HttpStatusCode status,
        final WebRequest request
    ) {
        log.error(
            request.getDescription(false) + "; " + ex.getLocalizedMessage(),
            stack(ex),
            severity(Severity.MINOR),
            errorId(UniqueError.UNSUPPORTED_MEDIA_TYPE)
        );

        return handleExceptionInternal(
            ex,
            ProcessingError
                .builder(UniqueError.UNSUPPORTED_MEDIA_TYPE)
                .description(
                    "Supported media types: " +
                    ex.getSupportedMediaTypes().stream().map(MimeType::toString).collect(Collectors.joining(", "))
                )
                .build()
                .toErrorMessage(),
            headers,
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            request
        );
    }

    private ResponseEntity<Object> handleValidationErrors(
        final Exception ex,
        final HttpHeaders headers,
        final WebRequest request,
        final List<ObjectError> validationErrors
    ) {
        Set<ProcessingError> processingErrors = validationErrors
            .stream()
            .map(error -> {
                ProcessingError.ProcessingErrorBuilder processingError = ProcessingError.builder(extractFrom(error));

                if (error instanceof FieldError fieldError) {
                    return processingError.description(fieldError.getField()).build();
                } else {
                    return processingError.description(error.getObjectName()).build();
                }
            })
            .collect(Collectors.toSet());

        return handleExceptionInternal(
            ex,
            ErrorMessage.builder().processingErrors(processingErrors).build(),
            headers,
            HttpStatus.BAD_REQUEST,
            request
        );
    }
}
