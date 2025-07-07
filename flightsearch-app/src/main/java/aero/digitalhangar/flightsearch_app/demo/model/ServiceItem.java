/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("java:S1105") //auto-formatting
@Schema(description = "Information for each service item retrieved from Services catalogue")
public record ServiceItem(
    @Schema(description = "Service id, as defined in services dictionary", example = "OT01") String id,
    @Schema(
        description = "Operational status. HK = confirmed, HL = waitlist, " +
        "TK = schedule change confirmed, schedule change waitlist, " +
        "UN = unable to confirm not operating, UC = unable to confirm, " +
        "HX = have cancelled, NO = no action taken. Status code is not returned at shopping time",
        example = "HK"
    )
    String statusCode
) {}
