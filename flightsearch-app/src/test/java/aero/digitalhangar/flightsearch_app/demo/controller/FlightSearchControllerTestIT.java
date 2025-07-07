/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import aero.digitalhangar.flightsearch_app.commons.error.handler.HttpMessageNotReadableHandler;
import aero.digitalhangar.flightsearch_app.commons.web.CallIdProvider;
import aero.digitalhangar.flightsearch_app.demo.service.FlightSearchService;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import(FlightSearchControllerTestIT.ControllerTestConfig.class)
@WebMvcTest(controllers = FlightSearchController.class)
public class FlightSearchControllerTestIT {

    @SuppressWarnings("unused")
    @MockitoBean
    private CallIdProvider callIdProvider; // call ID provider is used by ResponseHeaderModifierAdvice, but is not initialized in Spring IOC in WebMvcTest

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightSearchService flightSearchService;

    @SneakyThrows
    @ParameterizedTest(name = "Scenario for origin and destination: {0}")
    @ValueSource(strings = { "FRA,MSP" })
    void should_retrieve_flights_for_valid_origin_and_destination(final String input) {
        String[] parts = input.split(",");
        String origin = parts[0];
        String destination = parts[1];

        this.mockMvc.perform(get("/flights", origin, destination))
            .andDo(log())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

        verify(flightSearchService, atLeast(1)).getFlightsFromSearch(any(), any(), any(), any(), any());
        verify(flightSearchService, atMost(10)).getFlightsFromSearch(any(), any(), any(), any(), any());
    }

    @TestConfiguration
    static class ControllerTestConfig {

        @Bean
        public HttpMessageNotReadableHandler httpMessageNotReadableHandler() {
            return new HttpMessageNotReadableHandler();
        }
    }
}
