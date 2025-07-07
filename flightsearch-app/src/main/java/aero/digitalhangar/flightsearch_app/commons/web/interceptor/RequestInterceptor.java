/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.web.interceptor;

import aero.digitalhangar.flightsearch_app.commons.web.header.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Component
@SuppressWarnings("unused")
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final Object handler
    ) {
        request.setAttribute(HeaderConstants.RESPONSE_TIME, System.currentTimeMillis());
        return true;
    }
}
