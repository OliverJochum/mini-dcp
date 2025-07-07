/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.service;

import aero.digitalhangar.flightsearch_app.demo.model.FlightItem;
import aero.digitalhangar.flightsearch_app.demo.model.FlightType;
import java.util.List;
import java.util.Optional;

public interface FlightSearchService {
    List<FlightItem> getFlightsFromSearch(
        String origin,
        String destination,
        Optional<String> departureDate,
        Optional<String> returnDate,
        Optional<FlightType> flightType
    );
}
