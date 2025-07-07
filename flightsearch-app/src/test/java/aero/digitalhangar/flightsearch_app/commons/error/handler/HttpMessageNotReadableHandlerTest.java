/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.handler;

import static aero.digitalhangar.flightsearch_app.commons.error.JsonParserHelper.prepareParserForField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ExtendWith(MockitoExtension.class)
class HttpMessageNotReadableHandlerTest {

    private static final String DUMMY_JSON = "{ \"param\": \"value\" }";

    private final HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

    private final HttpMessageNotReadableHandler systemUnderTest = new HttpMessageNotReadableHandler();

    private static Stream<Arguments> getTestCases() {
        return Stream.of(
            arguments(
                new JsonParseException(prepareParserForField(DUMMY_JSON), ""),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).description("param").build()
            ),
            arguments(
                new RuntimeException(""),
                ProcessingError.builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM).build()
            ),
            // JsonMappingException cases
            arguments(
                mappingExceptionFrom(null),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).build()
            ),
            arguments(
                mappingExceptionFrom(Collections.emptyList()),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).build()
            ),
            arguments(
                mappingExceptionFrom(Collections.singletonList("A")),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).description("A").build()
            ),
            arguments(
                mappingExceptionFrom(Arrays.asList("A", null, "B")),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).description("B.A").build()
            ),
            arguments(
                mappingExceptionFrom(Arrays.asList("A", "B", "C")),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).description("C.B.A").build()
            ),
            // JsonMappingException / InvalidFormatException with enums cases
            arguments(
                formatExceptionFrom(null),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).build()
            ),
            arguments(
                formatExceptionFrom(Collections.emptyList()),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).build()
            ),
            arguments(
                formatExceptionFrom(Collections.singletonList("A")),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).description("A").build()
            ),
            arguments(
                formatExceptionFrom(Arrays.asList("A", null, "B")),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).description("B.A").build()
            ),
            arguments(
                formatExceptionFrom(Arrays.asList("A", "B", "C")),
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).description("C.B.A").build()
            )
        );
    }

    private static InvalidFormatException formatExceptionFrom(final List<String> fieldNames) {
        InvalidFormatException invalidFormatException = new InvalidFormatException(
            prepareParserForField(DUMMY_JSON),
            "ABC",
            "XYZ",
            UniqueError.class
        );

        if (fieldNames != null) {
            exceptionReferencesFrom(fieldNames).forEach(invalidFormatException::prependPath);
        }

        return invalidFormatException;
    }

    private static JsonMappingException mappingExceptionFrom(final List<String> fieldNames) {
        JsonMappingException jsonMappingException = new JsonMappingException(prepareParserForField(DUMMY_JSON), "XYZ");

        if (fieldNames != null) {
            exceptionReferencesFrom(fieldNames).forEach(jsonMappingException::prependPath);
        }

        return jsonMappingException;
    }

    private static List<JsonMappingException.Reference> exceptionReferencesFrom(final List<String> fieldNames) {
        List<JsonMappingException.Reference> result = new ArrayList<>();

        fieldNames.forEach(fieldName ->
            result.add(
                fieldName == null
                    ? new JsonMappingException.Reference(null)
                    : new JsonMappingException.Reference("", fieldName)
            )
        );

        return result;
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    void should_transform_exception_to_processing_error(final Exception cause, final ProcessingError expected) {
        when(exception.getCause()).thenReturn(cause);

        ProcessingError actual = systemUnderTest.transformExceptionToProcessingError(exception);
        assertThat(actual).isEqualTo(expected);
    }
}
