/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FareClass {
    @JsonProperty("Economy")
    E,
    @JsonProperty("Premium economy")
    P,
    @JsonProperty("Business")
    B,
    @JsonProperty("First")
    F,
}
