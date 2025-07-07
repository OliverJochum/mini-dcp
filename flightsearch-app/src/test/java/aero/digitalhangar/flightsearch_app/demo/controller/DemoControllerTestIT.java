/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import aero.digitalhangar.flightsearch_app.commons.error.handler.HttpMessageNotReadableHandler;
import aero.digitalhangar.flightsearch_app.commons.web.CallIdProvider;
import aero.digitalhangar.flightsearch_app.demo.model.ServiceItem;
import aero.digitalhangar.flightsearch_app.demo.service.DemoService;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import(DemoControllerTestIT.ControllerTestConfig.class)
@WebMvcTest(controllers = DemoController.class)
class DemoControllerTestIT {

    private static final String INVALID_BOOKING_ID_ERROR =
        """
        {
          "type": "E",
          "retryIndicator": false,
          "processingErrors": [
            {
              "code": "40003",
              "title": "Incorrect format in field"
            }
          ]
        }""";

    private static final String INVALID_SERVICE_ID_ERROR =
        """
        {
          "type": "E",
          "retryIndicator": false,
          "processingErrors": [
            {
              "code": "40002",
              "title": "Invalid value length in field"
            }
          ]
        }""";

    private final BasicJsonTester jsonTester = new BasicJsonTester(ServiceItem.class);

    @SuppressWarnings("unused")
    @MockitoBean
    private CallIdProvider callIdProvider; // call ID provider is used by ResponseHeaderModifierAdvice, but is not initialized in Spring IOC in WebMvcTest

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DemoService demoService;

    @SneakyThrows
    @ParameterizedTest(name = "Scenario for booking ID: {0}")
    @ValueSource(strings = { "AAAAAA", "aaaaaa", "000000", "111111", "1a2A3x" })
    void should_retrieve_services_for_valid_booking_id(final String input) {
        this.mockMvc.perform(get("/bookings/%s/services".formatted(input)))
            .andDo(log())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

        verify(demoService, times(1)).getServicesFromBooking(any());
    }

    @SneakyThrows
    @ParameterizedTest(name = "Scenario for booking ID: {0}")
    @ValueSource(strings = { "A", "AA", "AAA", "AAAA", "AAAAA", "AAAAAAX", "AAA.AA" })
    void should_fail_services_retrieval_for_invalid_booking_id(final String input) {
        String actual =
            this.mockMvc.perform(get("/bookings/%s/services".formatted(input)))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(jsonTester.from(INVALID_BOOKING_ID_ERROR)).isEqualToJson(actual, JSONCompareMode.NON_EXTENSIBLE);

        verify(demoService, never()).getServicesFromBooking(any());
    }

    @SneakyThrows
    @ParameterizedTest(name = "Scenario for booking ID: {0}")
    @ValueSource(strings = { "aaa", "AAA", "...", "1a.", "AAaa", "1a2A3x" })
    void should_retrieve_service_for_valid_booking_id_and_service_id(final String input) {
        this.mockMvc.perform(get("/bookings/AAAAAA/services/%s".formatted(input)))
            .andDo(log())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

        verify(demoService, times(1)).getServiceFromBooking(any(), any());
    }

    @SneakyThrows
    @ParameterizedTest(name = "Scenario for service ID: {0}")
    @ValueSource(strings = { "A", "AA", "aa", "00", "..", "a?1" })
    void should_fail_service_retrieval_for_invalid_service_id(final String input) {
        String actual =
            this.mockMvc.perform(get("/bookings/AAAAAA/services/%s".formatted(input)))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(jsonTester.from(INVALID_SERVICE_ID_ERROR)).isEqualToJson(actual, JSONCompareMode.NON_EXTENSIBLE);

        verify(demoService, never()).getServiceFromBooking(any(), any());
    }

    @TestConfiguration
    static class ControllerTestConfig {

        @Bean
        public HttpMessageNotReadableHandler httpMessageNotReadableHandler() {
            return new HttpMessageNotReadableHandler();
        }
    }
}
