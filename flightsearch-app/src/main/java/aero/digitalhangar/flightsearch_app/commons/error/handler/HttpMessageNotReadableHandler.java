/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.handler;

import static aero.digitalhangar.flightsearch_app.commons.logging.CustomLoggingArguments.errorId;
import static aero.digitalhangar.flightsearch_app.commons.logging.CustomLoggingArguments.severity;
import static aero.digitalhangar.flightsearch_app.commons.logging.CustomLoggingArguments.stack;

import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import aero.digitalhangar.flightsearch_app.commons.logging.Severity;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@SuppressWarnings({ "PlaceholderCountMatchesArgumentCount", "WeakerAccess" })
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Component
public class HttpMessageNotReadableHandler {

    private static ProcessingError handleJsonParseException(final JsonParseException ex) throws IOException {
        return ProcessingError
            .builder(UniqueError.PARAMETER_INCORRECT_FORMAT)
            .description(nullifyWhenBlank(ex.getProcessor().getCurrentName()))
            .build();
    }

    private static ProcessingError handleJsonMappingException(final JsonMappingException ex) {
        return ProcessingError
            .builder(UniqueError.PARAMETER_INCORRECT_FORMAT)
            .description(
                nullifyWhenBlank(
                    ex
                        .getPath()
                        .stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining("."))
                )
            )
            .build();
    }

    private static String nullifyWhenBlank(final String input) {
        return StringUtils.hasText(input) ? input : null; // `null` is skipped upon jackson serialization
    }

    public ProcessingError transformExceptionToProcessingError(final HttpMessageNotReadableException ex) {
        final Throwable cause = ex.getCause() == null ? ex : ex.getCause();
        ProcessingError error = ProcessingError.builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM).build();

        if (cause instanceof JsonParseException jsonParseException) {
            try {
                error = handleJsonParseException(jsonParseException);
            } catch (final IOException e) {
                log.warn(
                    "Could not extract parameterName from exception",
                    stack(e),
                    severity(Severity.MINOR),
                    errorId(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
                );
            }
        } else if (cause instanceof JsonMappingException jsonMappingException) {
            error = handleJsonMappingException(jsonMappingException);
        } else {
            log.warn(
                "No specific implementation for this httpMessageNotReadableException - {}",
                cause.getClass(),
                severity(Severity.MINOR),
                errorId(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
            );
        }

        return error;
    }
}
