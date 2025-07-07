/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.web;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CallIdProvider {

    private final Tracer tracer;

    private static String fallbackValue() {
        String fallbackValue = UUID.randomUUID().toString();
        log.warn("Could not use trace ID; generated dummy UUID for tracking purposes: {}", fallbackValue);

        return fallbackValue;
    }

    public String callId() {
        Span currentSpan = this.tracer.currentSpan();

        // @formatter:off
        return currentSpan == null
            ? fallbackValue()
            : currentSpan.context().traceId();
        // @formatter:on
    }
}
