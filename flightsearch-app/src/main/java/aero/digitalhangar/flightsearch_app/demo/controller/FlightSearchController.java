/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.controller;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.demo.model.FlightItem;
import aero.digitalhangar.flightsearch_app.demo.model.FlightType;
import aero.digitalhangar.flightsearch_app.demo.service.FlightSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Flights", description = "Controller provisioning flight search operations")
@RequiredArgsConstructor
@Validated
@RestController
public class FlightSearchController {

    private final FlightSearchService flightSearchService;

    @SuppressWarnings("java:S5867") //Unicode-aware characters are not handled
    @Operation(
        summary = "Retrieves flights from search",
        description = "To retrieve the list of service items, at least the `origin` and `destination` must be provided. Other search parameters are optional.",
        responses = {
            @ApiResponse(description = "List of flights", responseCode = "200"),
            @ApiResponse(
                description = "Provided search parameter was not valid",
                responseCode = "400",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                description = "Flight for provided search parameters was not found",
                responseCode = "404",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                description = "Internal server error during processing",
                responseCode = "500",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
        }
    )
    @GetMapping("/flights")
    public List<FlightItem> getFlightsFromSearch(
        @Parameter(description = "Origin airport code", required = true, example = "FRA") @Size(
            min = 3,
            max = 3
        ) String origin,
        @Parameter(description = "Destination airport code", required = true, example = "MSP") @Size(
            min = 3,
            max = 3
        ) String destination,
        @Parameter(
            description = "Departure date in ISO 8601 format",
            example = "2022-01-01T12:00:00Z"
        ) Optional<String> departureDate,
        @Parameter(
            description = "Return date in ISO 8601 format",
            example = "2022-01-01T14:00:00Z"
        ) Optional<String> returnDate,
        @Parameter(
            description = "Type of flight",
            schema = @Schema(type = "string", allowableValues = { "direct", "segmented" }, example = "direct")
        ) @RequestParam Optional<FlightType> flightType
    ) {
        return flightSearchService.getFlightsFromSearch(origin, destination, departureDate, returnDate, flightType);
    }
}
