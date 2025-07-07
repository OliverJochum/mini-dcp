/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FlightTypeConverter implements Converter<String, FlightType> {

    @Override
    public FlightType convert(String source) {
        return switch (source.toLowerCase()) {
            case "direct" -> FlightType.DIRECT;
            case "segmented" -> FlightType.SEGMENTED;
            default -> throw new IllegalArgumentException("Unknown FlightType: " + source);
        };
    }
}
