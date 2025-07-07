/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.web.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import aero.digitalhangar.flightsearch_app.commons.web.CallIdProvider;
import aero.digitalhangar.flightsearch_app.commons.web.header.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;

@SuppressWarnings({ "ConstantConditions", "resource" })
@ExtendWith(MockitoExtension.class)
class ResponseHeaderModifierAdviceTest {

    @Mock
    private CallIdProvider tracer;

    @Test
    void should_throw_npe_when_response_is_null() {
        ResponseHeaderModifierAdvice responseHeaderModifierAdvice = new ResponseHeaderModifierAdvice(tracer, "", "");

        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> responseHeaderModifierAdvice.beforeBodyWrite(null, null, null, null, null, null))
            .withMessage(
                "Cannot invoke \"org.springframework.http.server.ServerHttpResponse.getHeaders()\" " +
                "because \"response\" is null"
            );
    }

    @Test
    void should_throw_npe_when_request_is_null() {
        ResponseHeaderModifierAdvice responseHeaderModifierAdvice = new ResponseHeaderModifierAdvice(tracer, "", "");

        ServerHttpResponse serverHttpResponse = new ServletServerHttpResponse(mock(HttpServletResponse.class));

        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() ->
                responseHeaderModifierAdvice.beforeBodyWrite(null, null, null, null, null, serverHttpResponse)
            )
            .withMessage(
                "Cannot invoke \"org.springframework.http.server.ServletServerHttpRequest.getServletRequest()\" " +
                "because \"servletServerRequest\" is null"
            );
    }

    @Test
    void should_add_version_and_environment_headers_and_unknown_response_time() {
        ResponseHeaderModifierAdvice responseHeaderModifierAdvice = new ResponseHeaderModifierAdvice(
            tracer,
            "v1.0",
            "env"
        );

        ServerHttpResponse serverHttpResponse = new ServletServerHttpResponse(mock(HttpServletResponse.class));
        ServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(mock(HttpServletRequest.class));

        responseHeaderModifierAdvice.beforeBodyWrite(null, null, null, null, serverHttpRequest, serverHttpResponse);

        assertThat(serverHttpResponse.getHeaders())
            .extracting(Map::entrySet)
            .usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .isEqualTo(
                Map
                    .of("Response-Time", List.of("unknown"), "environment", List.of("env"), "version", List.of("v1.0"))
                    .entrySet()
            );
    }

    @Test
    void should_add_version_and_environment_and_trace_id_headers_and_unknown_response_time() {
        when(tracer.callId()).thenReturn("trace-id");

        ResponseHeaderModifierAdvice responseHeaderModifierAdvice = new ResponseHeaderModifierAdvice(
            tracer,
            "v1.0",
            "env"
        );

        ServerHttpResponse serverHttpResponse = new ServletServerHttpResponse(mock(HttpServletResponse.class));
        ServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(mock(HttpServletRequest.class));

        responseHeaderModifierAdvice.beforeBodyWrite(null, null, null, null, serverHttpRequest, serverHttpResponse);

        assertThat(serverHttpResponse.getHeaders())
            .extracting(Map::entrySet)
            .usingRecursiveComparison()
            .ignoringAllOverriddenEquals()
            .isEqualTo(
                Map
                    .of(
                        "Response-Time",
                        List.of("unknown"),
                        "environment",
                        List.of("env"),
                        "version",
                        List.of("v1.0"),
                        "X-B3-TraceId",
                        List.of("trace-id")
                    )
                    .entrySet()
            );
    }

    @Test
    void should_set_unknown_response_time_when_attribute_is_null() {
        ResponseHeaderModifierAdvice responseHeaderModifierAdvice = new ResponseHeaderModifierAdvice(
            tracer,
            "v1.0",
            "env"
        );

        ServerHttpResponse serverHttpResponse = new ServletServerHttpResponse(mock(HttpServletResponse.class));

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getAttribute(HeaderConstants.RESPONSE_TIME)).thenReturn(null);

        ServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(httpServletRequest);

        responseHeaderModifierAdvice.beforeBodyWrite(null, null, null, null, serverHttpRequest, serverHttpResponse);

        assertThat(serverHttpResponse.getHeaders())
            .extracting(headers -> headers.get(HeaderConstants.RESPONSE_TIME).get(0))
            .isNotNull()
            .isEqualTo("unknown");
    }

    @Test
    void should_set_response_time_other_than_unknown_when_attribute_is_not_null() {
        ResponseHeaderModifierAdvice responseHeaderModifierAdvice = new ResponseHeaderModifierAdvice(
            tracer,
            "v1.0",
            "env"
        );

        ServerHttpResponse serverHttpResponse = new ServletServerHttpResponse(mock(HttpServletResponse.class));

        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getAttribute(HeaderConstants.RESPONSE_TIME)).thenReturn(0L);

        ServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(httpServletRequest);

        responseHeaderModifierAdvice.beforeBodyWrite(null, null, null, null, serverHttpRequest, serverHttpResponse);

        assertThat(serverHttpResponse.getHeaders())
            .extracting(headers -> headers.get(HeaderConstants.RESPONSE_TIME).get(0))
            .isNotNull()
            .isNotEqualTo("unknown");
    }
}
