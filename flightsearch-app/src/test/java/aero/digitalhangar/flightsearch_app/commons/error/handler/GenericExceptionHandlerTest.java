/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.handler;

import static aero.digitalhangar.flightsearch_app.commons.error.handler.GenericExceptionHandler.CUSTOM_MESSAGE;
import static aero.digitalhangar.flightsearch_app.commons.error.handler.GenericExceptionHandler.CUSTOM_UNIQUE_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import aero.digitalhangar.flightsearch_app.commons.validation.ConditionalValues;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuppressWarnings({ "ConstantConditions", "rawtypes" })
@ExtendWith(MockitoExtension.class)
class GenericExceptionHandlerTest {

    private final ConstraintDescriptor descriptor = mock(ConstraintDescriptor.class);

    private final GenericExceptionHandler systemUnderTest = new GenericExceptionHandler();

    @Test
    void should_handle_generic_exception() {
        Exception exception = new Exception("");

        assertThat(systemUnderTest.handleGenericError(exception))
            .extracting(ResponseEntity::getStatusCode, e -> extractFirstError(e.getBody()))
            .containsExactly(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProcessingError.builder(UniqueError.INTERNAL_ERROR).build()
            );
    }

    @Test
    void should_handle_illegal_state_exception() {
        IllegalStateException exception = new IllegalStateException("");

        assertThat(systemUnderTest.handleIllegalState(exception))
            .extracting(ResponseEntity::getStatusCode, e -> extractFirstError((ErrorMessage) e.getBody()))
            .containsExactly(
                HttpStatus.BAD_REQUEST,
                ProcessingError.builder(UniqueError.PARAMETER_UNSPECIFIED_PROBLEM).build()
            );
    }

    @Test
    void should_handle_io_exception_with_broken_pipe() {
        IOException exception = new IOException(".... Broken pipe ....");

        assertThat(systemUnderTest.handleIOException(exception)).isNull();
    }

    @Test
    void should_handle_io_exception_without_broken_pipe() {
        IOException exception = new IOException("some-error");

        assertThat(systemUnderTest.handleIOException(exception))
            .extracting(ResponseEntity::getStatusCode, e -> extractFirstError(e.getBody()))
            .containsExactly(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProcessingError.builder(UniqueError.INTERNAL_ERROR).build()
            );
    }

    @Test
    void should_handle_constraint_exception() {
        NotEmpty contractId = mock(NotEmpty.class);

        when(descriptor.getAnnotation()).thenReturn(contractId);
        doReturn(NotEmpty.class).when(contractId).annotationType();

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(new DummyViolation()));

        assertThat(systemUnderTest.handleConstraintViolation(exception))
            .extracting(ResponseEntity::getStatusCode, e -> extractFirstError(e.getBody()))
            .containsExactly(
                HttpStatus.BAD_REQUEST,
                ProcessingError.builder(UniqueError.PARAMETER_MISSING_VALUE).build()
            );
    }

    @Test
    void should_apply_both_custom_attributes_from_violation() {
        ConditionalValues conditionalHeaders = mock(ConditionalValues.class);
        doReturn(ConditionalValues.class).when(conditionalHeaders).annotationType();

        when(descriptor.getAnnotation()).thenReturn(conditionalHeaders);
        when(descriptor.getAttributes())
            .thenReturn(Map.of(CUSTOM_MESSAGE, "Some error", CUSTOM_UNIQUE_ERROR, UniqueError.PARAMETER_OUT_OF_RANGE));

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(new DummyViolation()));

        assertThat(systemUnderTest.handleConstraintViolation(exception))
            .extracting(ResponseEntity::getStatusCode, e -> extractFirstError(e.getBody()))
            .containsExactly(
                HttpStatus.BAD_REQUEST,
                ProcessingError.builder(UniqueError.PARAMETER_OUT_OF_RANGE).description("Some error").build()
            );
    }

    @Test
    void should_apply_custom_message_attribute_from_violation() {
        ConditionalValues conditionalHeaders = mock(ConditionalValues.class);
        doReturn(ConditionalValues.class).when(conditionalHeaders).annotationType();

        when(descriptor.getAnnotation()).thenReturn(conditionalHeaders);
        when(descriptor.getAttributes()).thenReturn(Map.of(CUSTOM_MESSAGE, "Some error"));

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(new DummyViolation()));

        assertThat(systemUnderTest.handleConstraintViolation(exception))
            .extracting(ResponseEntity::getStatusCode, e -> extractFirstError(e.getBody()))
            .containsExactly(
                HttpStatus.BAD_REQUEST,
                ProcessingError.builder(UniqueError.CONDITIONAL_VALUES_INCORRECT).description("Some error").build()
            );
    }

    @Test
    void should_apply_custom_unique_error_attribute_from_violation() {
        ConditionalValues conditionalHeaders = mock(ConditionalValues.class);
        doReturn(ConditionalValues.class).when(conditionalHeaders).annotationType();

        when(descriptor.getAnnotation()).thenReturn(conditionalHeaders);
        when(descriptor.getAttributes()).thenReturn(Map.of(CUSTOM_UNIQUE_ERROR, UniqueError.PARAMETER_OUT_OF_RANGE));

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(new DummyViolation()));

        assertThat(systemUnderTest.handleConstraintViolation(exception))
            .extracting(ResponseEntity::getStatusCode, e -> extractFirstError(e.getBody()))
            .containsExactly(
                HttpStatus.BAD_REQUEST,
                ProcessingError.builder(UniqueError.PARAMETER_OUT_OF_RANGE).build()
            );
    }

    private ProcessingError extractFirstError(final ErrorMessage errorMessage) {
        return errorMessage.getProcessingErrors().iterator().next();
    }

    class DummyViolation implements ConstraintViolation<String> {

        @Override
        public String getMessage() {
            return null;
        }

        @Override
        public String getMessageTemplate() {
            return null;
        }

        @Override
        public String getRootBean() {
            return null;
        }

        @Override
        public Class<String> getRootBeanClass() {
            return null;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return mock(Path.class);
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return descriptor;
        }

        @Override
        public <U> U unwrap(final Class<U> type) {
            return null;
        }
    }
}
