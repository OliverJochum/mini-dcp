/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CallIdProviderTest {

    private static final String DUMMY_TRACE_ID = "dummy-trace-id";

    @Mock
    private Tracer tracer;

    @InjectMocks
    private CallIdProvider callIdProvider;

    @Test
    void should_generate_UUID_instead_of_trace_id() {
        when(tracer.currentSpan()).thenReturn(null);

        assertThat(callIdProvider.callId()).isNotNull().isNotBlank().isNotEqualToIgnoringCase(DUMMY_TRACE_ID);
    }

    @Test
    void should_throw_npe_if_context_is_null() {
        Span span = mock(Span.class);
        when(tracer.currentSpan()).thenReturn(span);

        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> callIdProvider.callId())
            .withMessage(
                "Cannot invoke \"io.micrometer.tracing.TraceContext.traceId()\" " +
                "because the return value of \"io.micrometer.tracing.Span.context()\" is null"
            );
    }

    @Test
    void should_return_null_if_trace_is_null() {
        TraceContext traceContext = mock(TraceContext.class);

        Span span = mock(Span.class);
        when(span.context()).thenReturn(traceContext);

        when(tracer.currentSpan()).thenReturn(span);

        assertThat(callIdProvider.callId()).isNull();
    }

    @Test
    void should_return_actual_trace_id() {
        TraceContext traceContext = mock(TraceContext.class);
        when(traceContext.traceId()).thenReturn(DUMMY_TRACE_ID);

        Span span = mock(Span.class);
        when(span.context()).thenReturn(traceContext);

        when(tracer.currentSpan()).thenReturn(span);

        assertThat(callIdProvider.callId()).isNotNull().isNotBlank().isEqualTo(DUMMY_TRACE_ID);
    }
}
