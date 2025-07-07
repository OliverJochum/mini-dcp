/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.logging;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Severity {
    CRITICAL("Critical"),
    MAJOR("Major"),
    MINOR("Minor"),

    NON_OPS("Non-OPS"),
    WARNING("Warning"),
    INFORMATION("Information"),
    SUCCESS("Success"),
    DEBUG("Debug");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
