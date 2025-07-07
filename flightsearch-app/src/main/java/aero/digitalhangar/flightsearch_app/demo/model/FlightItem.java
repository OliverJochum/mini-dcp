/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("java:S1105") //auto-formatting
@Schema(description = "Information for each flight item retrieved from Flights catalogue")
public record FlightItem(
    @Schema(description = "Flight id", example = "LH1234") String id,
    @Schema(description = "departure date & time in ISO 8601 format", example = "2022-01-01T12:00:00Z")
    String departureDateTime,
    @Schema(description = "arrival date & time in ISO 8601 format", example = "2022-01-01T14:00:00Z")
    String arrivalDateTime,
    @Schema(description = "origin airport code", example = "FRA") String origin,
    @Schema(description = "destination airport code", example = "MSP") String destination,
    @Schema(description = "airline code", example = "LH") String airlineCode,
    @Schema(description = "price in cents", example = "10000") int price,
    @Schema(description = "currency code", example = "USD") String currency,
    @Schema(description = "Fare class", example = "Business") FareClass fareClass,
    @Schema(description = "type", example = "direct") FlightType flightType,
    @Schema(description = "via", example = "viaFlightItems [ FlightItem {...}, ...]") FlightItem[] viaFlightItems
) {}
