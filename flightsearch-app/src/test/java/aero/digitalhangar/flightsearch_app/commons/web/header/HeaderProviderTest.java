/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.web.header;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import aero.digitalhangar.flightsearch_app.commons.web.CallIdProvider;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class HeaderProviderTest {

    private static final List<String> DUMMY_NULL_LIST = new ArrayList<>() {
        {
            add(null);
        }
    };

    @Mock
    private CallIdProvider callIdProvider;

    @Test
    void should_create_headers_without_authorization() {
        assertThat(new HeaderProvider(callIdProvider, null, "LH_TEMPLATE").create())
            .contains(entry(HeaderConstants.Middleware.CUSTOMER_ID, List.of("LH")))
            .contains(entry(HeaderConstants.Middleware.APPLICATION_ID, List.of("LH_TEMPLATE")))
            .contains(entry(HeaderConstants.Middleware.CALLER_ID, DUMMY_NULL_LIST))
            .contains(entry(HeaderConstants.Middleware.CALL_ID, DUMMY_NULL_LIST))
            .doesNotContainKey(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void should_create_headers_with_authorization() {
        assertThat(new HeaderProvider(callIdProvider, null, "LH_TEMPLATE").createWithBearerToken("token"))
            .contains(entry(HeaderConstants.Middleware.CUSTOMER_ID, List.of("LH")))
            .contains(entry(HeaderConstants.Middleware.APPLICATION_ID, List.of("LH_TEMPLATE")))
            .contains(entry(HeaderConstants.Middleware.CALLER_ID, DUMMY_NULL_LIST))
            .contains(entry(HeaderConstants.Middleware.CALL_ID, DUMMY_NULL_LIST))
            .contains(entry(HttpHeaders.AUTHORIZATION, List.of("Bearer token")));
    }
}
