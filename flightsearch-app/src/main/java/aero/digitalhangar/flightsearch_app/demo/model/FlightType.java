/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public enum FlightType {
    @JsonProperty("direct")
    @Schema(description = "Direct flight", name = "direct")
    DIRECT,
    @JsonProperty("segmented")
    @Schema(description = "Segmented flight", name = "segmented")
    SEGMENTED,
}
