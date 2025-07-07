/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.web.header;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HeaderConstants {

    public static final String TRACE_ID = "X-B3-TraceId";
    public static final String RESPONSE_TIME = "Response-Time";
    public static final String VERSION = "version";
    public static final String ENVIRONMENT = "environment";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Middleware {

        public static final String APPLICATION_ID = "ApplicationID";
        public static final String CALL_ID = "CallID";
        public static final String CALLER_ID = "CallerID";
        public static final String CUSTOMER_ID = "CustomerID";
    }
}
