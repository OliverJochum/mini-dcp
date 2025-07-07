/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.web.header;

import aero.digitalhangar.flightsearch_app.commons.web.CallIdProvider;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class HeaderProvider {

    private final CallIdProvider callIdProvider;
    private final String environment;
    private final String appId;

    public HeaderProvider(
        final CallIdProvider callIdProvider,
        final @Value("${spring.application.environment:unknown}") String environment,
        final @Value("${spring.application.id}") String appId
    ) {
        this.callIdProvider = callIdProvider;
        this.environment = environment;
        this.appId = appId;
    }

    public HttpHeaders create() {
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(HeaderConstants.Middleware.CUSTOMER_ID, "LH"); // required by middleware; legacy parameter
        headers.set(HeaderConstants.Middleware.APPLICATION_ID, appId);
        headers.set(HeaderConstants.Middleware.CALLER_ID, environment);
        headers.set(HeaderConstants.Middleware.CALL_ID, callIdProvider.callId());

        return headers;
    }

    public HttpHeaders createWithBearerToken(final String token) {
        HttpHeaders headers = create();
        headers.setBearerAuth(token);

        return headers;
    }
}
