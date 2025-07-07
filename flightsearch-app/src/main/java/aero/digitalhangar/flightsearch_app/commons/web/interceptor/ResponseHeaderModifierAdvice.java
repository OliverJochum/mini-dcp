/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.web.interceptor;

import aero.digitalhangar.flightsearch_app.commons.web.CallIdProvider;
import aero.digitalhangar.flightsearch_app.commons.web.header.HeaderConstants;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@SuppressWarnings("unused")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ControllerAdvice(basePackages = "aero.digitalhangar")
public class ResponseHeaderModifierAdvice implements ResponseBodyAdvice<Object> {

    private final CallIdProvider callIdProvider;
    private final String version;
    private final String environment;

    public ResponseHeaderModifierAdvice(
        final CallIdProvider callIdProvider,
        @Value("${spring.application.version.service}") final String version,
        @Value("${spring.application.environment}") final String environment
    ) {
        this.callIdProvider = callIdProvider;
        this.version = version;
        this.environment = environment;
    }

    private static String getExecutionTime(final ServerHttpRequest request) {
        ServletServerHttpRequest servletServerRequest = (ServletServerHttpRequest) request;
        Object timeAttr = servletServerRequest.getServletRequest().getAttribute(HeaderConstants.RESPONSE_TIME);
        if (timeAttr != null) {
            long startTime = (long) timeAttr;
            long timeElapsed = System.currentTimeMillis() - startTime;

            return String.valueOf(timeElapsed);
        } else {
            return "unknown";
        }
    }

    @Override
    public boolean supports(
        final MethodParameter returnType,
        final Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
        final Object body,
        final MethodParameter returnType,
        final MediaType selectedContentType,
        final Class<? extends HttpMessageConverter<?>> selectedConverterType,
        final ServerHttpRequest request,
        final ServerHttpResponse response
    ) {
        response.getHeaders().add(HeaderConstants.VERSION, version);
        response.getHeaders().add(HeaderConstants.ENVIRONMENT, environment);
        response.getHeaders().add(HeaderConstants.RESPONSE_TIME, getExecutionTime(request));

        Optional
            .ofNullable(callIdProvider.callId())
            .ifPresent(callId -> response.getHeaders().add(HeaderConstants.TRACE_ID, callId));

        return body;
    }
}
