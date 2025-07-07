/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RequestExceptionHandlerTest {

    private final HttpHeaders DUMMY_HEADERS = HttpHeaders.EMPTY;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MethodArgumentNotValidException exception;

    @InjectMocks
    private RequestExceptionHandler systemUnderTest;

    private static Stream<Arguments> getExceptionTestCases() {
        return Stream.of(
            arguments(
                // only ObjectError
                Collections.singletonList(objectError("IRRELEVANT")),
                Collections.singleton(
                    ProcessingError
                        .builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM)
                        .description("OBJECT_NAME")
                        .build()
                )
            ),
            arguments(
                // unknown 'parseFromConstraintName'
                Collections.singletonList(fieldError("IRRELEVANT")),
                Collections.singleton(
                    ProcessingError.builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM).description("FIELD_NAME").build()
                )
            ),
            arguments(
                // 'parseFromConstraintName' = 'SIZE' / invalid length
                Collections.singletonList(fieldError("SIZE")),
                Collections.singleton(
                    ProcessingError.builder(UniqueError.PARAMETER_INVALID_LENGTH).description("FIELD_NAME").build()
                )
            ),
            arguments(
                // 'parseFromConstraintName' = 'NOT_NULL' / missing value
                Collections.singletonList(fieldError("NOTNULL")),
                Collections.singleton(
                    ProcessingError.builder(UniqueError.PARAMETER_MISSING_VALUE).description("FIELD_NAME").build()
                )
            ),
            arguments(
                // 'parseFromConstraintName' = 'NOT_EMPTY' / missing value
                Collections.singletonList(fieldError("NOTEMPTY")),
                Collections.singleton(
                    ProcessingError.builder(UniqueError.PARAMETER_MISSING_VALUE).description("FIELD_NAME").build()
                )
            ),
            arguments(
                // 'parseFromConstraintName' = 'NOT_EMPTY' + 'NOTNULL' / missing value
                List.of(fieldError("NOTEMPTY"), fieldError("NOTNULL")),
                Collections.singleton(
                    ProcessingError.builder(UniqueError.PARAMETER_MISSING_VALUE).description("FIELD_NAME").build()
                )
            ),
            arguments(
                // 'parseFromConstraintName' = 'SIZE' + 'NOT_EMPTY' + 'NOTNULL' / missing value
                List.of(fieldError("SIZE"), fieldError("NOTEMPTY"), fieldError("NOTNULL")),
                Set.of(
                    ProcessingError.builder(UniqueError.PARAMETER_MISSING_VALUE).description("FIELD_NAME").build(),
                    ProcessingError.builder(UniqueError.PARAMETER_INVALID_LENGTH).description("FIELD_NAME").build()
                )
            )
        );
    }

    private static FieldError fieldError(final String errorCode) {
        return new FieldError(
            "OBJECT_NAME",
            "FIELD_NAME",
            null,
            false,
            new String[] { "IGNORE_ME", errorCode },
            null,
            "MESSAGE"
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static ObjectError objectError(final String errorCode) {
        return objectError(errorCode, null);
    }

    @SuppressWarnings("SameParameterValue")
    private static ObjectError objectError(final String errorCode, @Nullable final List<Object> arguments) {
        return new ObjectError(
            "OBJECT_NAME",
            new String[] { "IGNORE_ME", errorCode },
            Objects.nonNull(arguments) ? arguments.toArray() : null,
            "MESSAGE"
        );
    }

    @BeforeEach
    void init() {
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getMessage()).thenReturn("TEST");
    }

    @Test
    void should_handle_all_errors() {
        List<ObjectError> fieldErrors = List.of(
            fieldError("IRRELEVANT"),
            fieldError("SIZE"),
            fieldError("NOTEMPTY"),
            fieldError("NOTNULL")
        );

        when(bindingResult.getAllErrors()).thenReturn(fieldErrors);

        ResponseEntity<Object> actual = systemUnderTest.handleMethodArgumentNotValid(
            exception,
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        //noinspection ConstantConditions
        assertThat(((ErrorMessage) actual.getBody()).getProcessingErrors())
            .extracting(ProcessingError::getCode)
            .hasSize(3)
            .containsExactlyInAnyOrder("40099", "40001", "40002");
    }

    @Test
    void should_handle_missing_parameter() {
        final String parameterName = "dummy-value";
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException(
            parameterName,
            "type"
        );

        ResponseEntity<Object> actual = systemUnderTest.handleMissingServletRequestParameter(
            exception,
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        //noinspection ConstantConditions
        assertThat(((ErrorMessage) actual.getBody()).getProcessingErrors())
            .extracting(ProcessingError::getCode, ProcessingError::getTitle, ProcessingError::getDescription)
            .hasSize(1)
            .containsExactly(tuple("40001", "Value missing in field", parameterName));
    }

    @ParameterizedTest
    @MethodSource("getExceptionTestCases")
    void should_handle_not_valid_errors(final List<ObjectError> fieldErrors, final Set<ProcessingError> expected) {
        when(bindingResult.getAllErrors()).thenReturn(fieldErrors);

        ResponseEntity<Object> actual = systemUnderTest.handleMethodArgumentNotValid(
            exception,
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        assertThat(actual.getBody()).isInstanceOf(ErrorMessage.class);
        ErrorMessage ErrorMessage = (ErrorMessage) actual.getBody();
        //noinspection ConstantConditions
        assertThat(ErrorMessage.getProcessingErrors())
            .hasSize(expected.size())
            .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void should_handle_media_type_not_supported() {
        ResponseEntity<Object> actual = systemUnderTest.handleHttpMediaTypeNotSupported(
            new HttpMediaTypeNotSupportedException(
                MediaType.ALL,
                List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            ),
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        assertThat(actual.getBody()).isInstanceOf(ErrorMessage.class);
        ErrorMessage ErrorMessage = (ErrorMessage) actual.getBody();
        //noinspection ConstantConditions
        assertThat(ErrorMessage.getProcessingErrors())
            .containsExactly(
                ProcessingError
                    .builder(UniqueError.UNSUPPORTED_MEDIA_TYPE)
                    .description("Supported media types: application/json, application/xml")
                    .build()
            );
    }

    @Test
    void should_handle_method_not_supported() {
        ResponseEntity<Object> actual = systemUnderTest.handleHttpRequestMethodNotSupported(
            new HttpRequestMethodNotSupportedException("test", List.of("get", "post")),
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        assertThat(actual.getBody()).isInstanceOf(ErrorMessage.class);
        ErrorMessage ErrorMessage = (ErrorMessage) actual.getBody();
        //noinspection ConstantConditions
        assertThat(ErrorMessage.getProcessingErrors())
            .containsExactly(
                ProcessingError
                    .builder(UniqueError.METHOD_NOT_ALLOWED)
                    .description("Supported methods: get, post")
                    .build()
            );
    }

    @Test
    void should_handle_type_mismatch_for_generic_exception() {
        ResponseEntity<Object> actual = systemUnderTest.handleTypeMismatch(
            new TypeMismatchException("", String.class),
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        assertThat(actual.getBody()).isInstanceOf(ErrorMessage.class);
        ErrorMessage ErrorMessage = (ErrorMessage) actual.getBody();
        //noinspection ConstantConditions
        assertThat(ErrorMessage.getProcessingErrors())
            .containsExactly(ProcessingError.builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM).build());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void should_handle_type_mismatch_for_concrete_exception() {
        ResponseEntity<Object> actual = systemUnderTest.handleTypeMismatch(
            new MethodArgumentTypeMismatchException(
                "",
                String.class,
                "some-name",
                null,
                new RuntimeException("some-error")
            ),
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        assertThat(actual.getBody()).isInstanceOf(ErrorMessage.class);
        ErrorMessage ErrorMessage = (ErrorMessage) actual.getBody();
        //noinspection ConstantConditions
        assertThat(ErrorMessage.getProcessingErrors())
            .containsExactly(
                ProcessingError.builder(UniqueError.PARAMETER_INCORRECT_FORMAT).description("some-name").build()
            );
    }

    @Test
    void should_handle_generic_servlet_request_binding_exception() {
        ServletRequestBindingException bindException = mock(ServletRequestBindingException.class);

        ResponseEntity<Object> actual = systemUnderTest.handleServletRequestBindingException(
            bindException,
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        assertThat(actual.getBody()).isInstanceOf(ErrorMessage.class);
        ErrorMessage ErrorMessage = (ErrorMessage) actual.getBody();
        //noinspection ConstantConditions
        assertThat(ErrorMessage.getProcessingErrors())
            .containsExactlyInAnyOrder(ProcessingError.builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM).build());
    }

    @Test
    void should_handle_specific_servlet_request_binding_exception() {
        MissingRequestHeaderException bindException = mock(MissingRequestHeaderException.class);
        when(bindException.getHeaderName()).thenReturn("test-header");

        ResponseEntity<Object> actual = systemUnderTest.handleServletRequestBindingException(
            bindException,
            DUMMY_HEADERS,
            HttpStatus.I_AM_A_TEAPOT,
            mock(WebRequest.class)
        );

        assertThat(actual.getBody()).isInstanceOf(ErrorMessage.class);
        ErrorMessage ErrorMessage = (ErrorMessage) actual.getBody();
        //noinspection ConstantConditions
        assertThat(ErrorMessage.getProcessingErrors())
            .containsExactlyInAnyOrder(
                ProcessingError.builder(UniqueError.PARAMETER_MISSING_VALUE).description("test-header").build()
            );
    }
}
