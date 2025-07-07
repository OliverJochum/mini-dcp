/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.controller;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.demo.model.ServiceItem;
import aero.digitalhangar.flightsearch_app.demo.service.DemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Services", description = "Controller provisioning booking services operations")
@RequiredArgsConstructor
@Validated
@RestController
public class DemoController {

    private final DemoService demoService;

    @SuppressWarnings("java:S5867") //Unicode-aware characters are not handled
    @Operation(
        summary = "Retrieves service items which are linked to a booking identified by `bookingId`",
        description = "To retrieve the list of service items, the `bookingId` must provided",
        responses = {
            @ApiResponse(description = "List of service items; might be an empty list", responseCode = "200"),
            @ApiResponse(
                description = "Provided `bookingId` parameter was not valid",
                responseCode = "400",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                description = "Booking for provided `bookingId` was not found",
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
    @GetMapping("/bookings/{bookingId}/services")
    public List<ServiceItem> getServicesFromBooking(
        @Parameter(
            description = "Identifier of the booking for which, services should be retrieved",
            required = true,
            example = "ABCDEF"
        ) @Pattern(regexp = "[a-zA-Z0-9]{6}") @PathVariable("bookingId") final String bookingId
    ) {
        return demoService.getServicesFromBooking(bookingId);
    }

    @SuppressWarnings("java:S5867") //Unicode-aware characters are not handled
    @Operation(
        summary = "Retrieves a particular service item which is linked to a booking identified by `bookingId`",
        description = "To retrieve the service item, the `bookingId` must provided",
        responses = {
            @ApiResponse(description = "Particular service item", responseCode = "200"),
            @ApiResponse(
                description = "Provided `bookingId` parameter was not valid",
                responseCode = "400",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                description = "Provided `serviceId` parameter was not valid",
                responseCode = "400",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                description = "Booking for provided `bookingId` was not found",
                responseCode = "404",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))
            ),
            @ApiResponse(
                description = "Service for provided `serviceId` was not found within the booking",
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
    @GetMapping("/bookings/{bookingId}/services/{serviceId}")
    public ServiceItem getServiceFromBooking(
        @Parameter(
            description = "Identifier of the booking for which, services should be retrieved",
            required = true,
            example = "ABCDEF"
        ) @Pattern(regexp = "[a-zA-Z0-9]{6}") @PathVariable("bookingId") final String bookingId,
        @Parameter(
            description = "Identifier of the booking for which, services should be retrieved",
            required = true,
            example = "OT01"
        ) @Size(min = 3) @PathVariable("serviceId") final String serviceId
    ) {
        return demoService.getServiceFromBooking(bookingId, serviceId);
    }
}
