/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.service.impl;

import aero.digitalhangar.flightsearch_app.demo.model.FareClass;
import aero.digitalhangar.flightsearch_app.demo.model.FlightItem;
import aero.digitalhangar.flightsearch_app.demo.model.FlightType;
import aero.digitalhangar.flightsearch_app.demo.model.FlightsNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.service.FlightSearchService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class MockFlightSearchService implements FlightSearchService {

    private static final Map<String, FlightItem> MOCK_DATA = Stream
        .of(
            new FlightItem(
                "LH9742",
                "2025-01-01T08:00:00Z",
                "2025-01-01T18:00:00Z",
                "FRA",
                "MSP",
                "SQ",
                24573,
                "USD",
                FareClass.B,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "LH9742-C1",
                        "2025-01-01T08:00:00Z",
                        "2025-01-01T12:00:00Z",
                        "FRA",
                        "LHR",
                        "SQ",
                        12286,
                        "USD",
                        FareClass.B,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "LH9742-C2",
                        "2025-01-01T13:00:00Z",
                        "2025-01-01T18:00:00Z",
                        "LHR",
                        "MSP",
                        "SQ",
                        12287,
                        "USD",
                        FareClass.B,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "LH7500",
                "2025-01-01T11:00:00Z",
                "2025-01-01T13:00:00Z",
                "FRA",
                "MSP",
                "SQ",
                117370,
                "USD",
                FareClass.P,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "LH7600-C1",
                        "2025-01-01T11:00:00Z",
                        "2025-01-01T12:00:00Z",
                        "FRA",
                        "CDG",
                        "SQ",
                        58685,
                        "USD",
                        FareClass.P,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "LH7500-C2",
                        "2025-01-01T12:30:00Z",
                        "2025-01-01T13:00:00Z",
                        "CDG",
                        "MSP",
                        "SQ",
                        58685,
                        "USD",
                        FareClass.P,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "LH3671",
                "2025-01-01T14:00:00Z",
                "2025-01-01T15:00:00Z",
                "FRA",
                "MSP",
                "EK",
                103455,
                "USD",
                FareClass.F,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "LH3671-C1",
                        "2025-01-01T14:00:00Z",
                        "2025-01-01T14:30:00Z",
                        "FRA",
                        "JFK",
                        "EK",
                        51727,
                        "USD",
                        FareClass.F,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "LH3671-C2",
                        "2025-01-01T14:45:00Z",
                        "2025-01-01T15:00:00Z",
                        "JFK",
                        "MSP",
                        "EK",
                        51728,
                        "USD",
                        FareClass.F,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "LH5806",
                "2025-01-01T17:00:00Z",
                "2025-01-01T20:00:00Z",
                "FRA",
                "MSP",
                "AF",
                162532,
                "USD",
                FareClass.B,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "LH5806-C1",
                        "2025-01-01T17:00:00Z",
                        "2025-01-01T18:30:00Z",
                        "FRA",
                        "AMS",
                        "AF",
                        81266,
                        "USD",
                        FareClass.B,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "LH5806-C2",
                        "2025-01-01T19:00:00Z",
                        "2025-01-01T20:00:00Z",
                        "AMS",
                        "MSP",
                        "AF",
                        81266,
                        "USD",
                        FareClass.B,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "LH2093",
                "2025-01-02T02:00:00Z",
                "2025-01-02T13:00:00Z",
                "FRA",
                "MSP",
                "DL",
                109067,
                "USD",
                FareClass.P,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "LH2093-C1",
                        "2025-01-02T02:00:00Z",
                        "2025-01-02T07:00:00Z",
                        "FRA",
                        "CDG",
                        "DL",
                        54533,
                        "USD",
                        FareClass.P,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "LH2093-C2",
                        "2025-01-02T08:00:00Z",
                        "2025-01-02T13:00:00Z",
                        "CDG",
                        "MSP",
                        "DL",
                        54534,
                        "USD",
                        FareClass.P,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "LH6447",
                "2025-01-02T08:00:00Z",
                "2025-01-02T11:00:00Z",
                "FRA",
                "MSP",
                "DL",
                81918,
                "USD",
                FareClass.P,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "LH6447-C1",
                        "2025-01-02T08:00:00Z",
                        "2025-01-02T09:30:00Z",
                        "FRA",
                        "ORD",
                        "DL",
                        40959,
                        "USD",
                        FareClass.P,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "LH6447-C2",
                        "2025-01-02T10:00:00Z",
                        "2025-01-02T11:00:00Z",
                        "ORD",
                        "MSP",
                        "DL",
                        40959,
                        "USD",
                        FareClass.P,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "LH4157",
                "2025-01-02T11:00:00Z",
                "2025-01-02T21:00:00Z",
                "FRA",
                "MSP",
                "AA",
                119250,
                "USD",
                FareClass.F,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "LH4157-C1",
                        "2025-01-02T11:00:00Z",
                        "2025-01-02T15:00:00Z",
                        "FRA",
                        "LHR",
                        "AA",
                        59625,
                        "USD",
                        FareClass.F,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "LH4157-C2",
                        "2025-01-02T17:00:00Z",
                        "2025-01-02T21:00:00Z",
                        "LHR",
                        "MSP",
                        "AA",
                        59625,
                        "USD",
                        FareClass.F,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "FL9874",
                "2025-01-02T09:00:00Z",
                "2025-01-02T13:00:00Z",
                "LAX",
                "JFK",
                "BA",
                19797,
                "USD",
                FareClass.P,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "FL9874-C1",
                        "2025-01-02T09:00:00Z",
                        "2025-01-02T11:00:00Z",
                        "LAX",
                        "ORD",
                        "BA",
                        9898,
                        "USD",
                        FareClass.P,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "FL9874-C2",
                        "2025-01-02T11:30:00Z",
                        "2025-01-02T13:00:00Z",
                        "ORD",
                        "JFK",
                        "BA",
                        9899,
                        "USD",
                        FareClass.P,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "FL8543",
                "2025-01-03T15:00:00Z",
                "2025-01-03T20:00:00Z",
                "SFO",
                "BOS",
                "UA",
                87230,
                "USD",
                FareClass.B,
                FlightType.SEGMENTED,
                new FlightItem[] {
                    new FlightItem(
                        "FL8543-C1",
                        "2025-01-03T15:00:00Z",
                        "2025-01-03T17:00:00Z",
                        "SFO",
                        "DEN",
                        "UA",
                        43615,
                        "USD",
                        FareClass.B,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                    new FlightItem(
                        "FL8543-C2",
                        "2025-01-03T18:00:00Z",
                        "2025-01-03T20:00:00Z",
                        "DEN",
                        "BOS",
                        "UA",
                        43615,
                        "USD",
                        FareClass.B,
                        FlightType.DIRECT,
                        new FlightItem[] {}
                    ),
                }
            ),
            new FlightItem(
                "FL1001",
                "2025-01-03T08:00:00Z",
                "2025-01-03T12:00:00Z",
                "FRA",
                "MSP",
                "LH",
                120000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1002",
                "2025-01-03T13:00:00Z",
                "2025-01-03T17:00:00Z",
                "FRA",
                "MSP",
                "LH",
                125000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1003",
                "2025-01-04T06:30:00Z",
                "2025-01-04T10:30:00Z",
                "FRA",
                "MSP",
                "LH",
                130000,
                "USD",
                FareClass.B,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1004",
                "2025-01-04T09:45:00Z",
                "2025-01-04T13:45:00Z",
                "FRA",
                "MSP",
                "LH",
                115000,
                "USD",
                FareClass.F,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1005",
                "2025-01-05T07:15:00Z",
                "2025-01-05T11:15:00Z",
                "FRA",
                "MSP",
                "LH",
                110000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL2006",
                "2025-01-06T05:00:00Z",
                "2025-01-06T09:00:00Z",
                "LAX",
                "JFK",
                "AA",
                45000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1206",
                "2025-01-06T05:00:00Z",
                "2025-01-06T09:00:00Z",
                "MSP",
                "JFK",
                "AA",
                45000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1007",
                "2025-01-06T06:00:00Z",
                "2025-01-06T10:00:00Z",
                "LAX",
                "JFK",
                "AA",
                47000,
                "USD",
                FareClass.B,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1008",
                "2025-01-06T07:00:00Z",
                "2025-01-06T11:00:00Z",
                "LAX",
                "JFK",
                "AA",
                49000,
                "USD",
                FareClass.F,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1009",
                "2025-01-06T08:00:00Z",
                "2025-01-06T12:00:00Z",
                "LAX",
                "JFK",
                "AA",
                46000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1010",
                "2025-01-07T09:00:00Z",
                "2025-01-07T13:00:00Z",
                "LAX",
                "JFK",
                "AA",
                50000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1011",
                "2025-01-08T10:00:00Z",
                "2025-01-08T14:00:00Z",
                "CDG",
                "LHR",
                "AF",
                30000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1012",
                "2025-01-08T11:00:00Z",
                "2025-01-08T15:00:00Z",
                "CDG",
                "LHR",
                "AF",
                32000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1013",
                "2025-01-08T12:00:00Z",
                "2025-01-08T16:00:00Z",
                "CDG",
                "LHR",
                "AF",
                34000,
                "USD",
                FareClass.B,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1014",
                "2025-01-08T13:00:00Z",
                "2025-01-08T17:00:00Z",
                "CDG",
                "LHR",
                "AF",
                36000,
                "USD",
                FareClass.F,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1015",
                "2025-01-08T14:00:00Z",
                "2025-01-08T18:00:00Z",
                "CDG",
                "LHR",
                "AF",
                31000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1016",
                "2025-01-09T06:00:00Z",
                "2025-01-09T10:00:00Z",
                "JFK",
                "ORD",
                "DL",
                48000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1017",
                "2025-01-09T07:00:00Z",
                "2025-01-09T11:00:00Z",
                "JFK",
                "ORD",
                "DL",
                50000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1018",
                "2025-01-09T08:00:00Z",
                "2025-01-09T12:00:00Z",
                "JFK",
                "ORD",
                "DL",
                52000,
                "USD",
                FareClass.B,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1019",
                "2025-01-09T09:00:00Z",
                "2025-01-09T13:00:00Z",
                "JFK",
                "ORD",
                "DL",
                54000,
                "USD",
                FareClass.F,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1020",
                "2025-01-09T10:00:00Z",
                "2025-01-09T14:00:00Z",
                "JFK",
                "ORD",
                "DL",
                56000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1021",
                "2025-01-10T11:00:00Z",
                "2025-01-10T15:00:00Z",
                "SFO",
                "SEA",
                "UA",
                35000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1022",
                "2025-01-10T12:00:00Z",
                "2025-01-10T16:00:00Z",
                "SFO",
                "SEA",
                "UA",
                37000,
                "USD",
                FareClass.B,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1023",
                "2025-01-10T13:00:00Z",
                "2025-01-10T17:00:00Z",
                "SFO",
                "SEA",
                "UA",
                39000,
                "USD",
                FareClass.F,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1024",
                "2025-01-10T14:00:00Z",
                "2025-01-10T18:00:00Z",
                "SFO",
                "SEA",
                "UA",
                41000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1025",
                "2025-01-10T15:00:00Z",
                "2025-01-10T19:00:00Z",
                "SFO",
                "SEA",
                "UA",
                43000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1026",
                "2025-01-11T07:00:00Z",
                "2025-01-11T10:00:00Z",
                "BOS",
                "MIA",
                "BA",
                54000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1027",
                "2025-01-11T08:00:00Z",
                "2025-01-11T11:00:00Z",
                "BOS",
                "MIA",
                "BA",
                56000,
                "USD",
                FareClass.P,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1028",
                "2025-01-11T09:00:00Z",
                "2025-01-11T12:00:00Z",
                "BOS",
                "MIA",
                "BA",
                58000,
                "USD",
                FareClass.B,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1029",
                "2025-01-11T10:00:00Z",
                "2025-01-11T13:00:00Z",
                "BOS",
                "MIA",
                "BA",
                60000,
                "USD",
                FareClass.F,
                FlightType.DIRECT,
                new FlightItem[] {}
            ),
            new FlightItem(
                "FL1030",
                "2025-01-11T11:00:00Z",
                "2025-01-11T14:00:00Z",
                "BOS",
                "MIA",
                "BA",
                62000,
                "USD",
                FareClass.E,
                FlightType.DIRECT,
                new FlightItem[] {}
            )
        )
        .collect(Collectors.toMap(FlightItem::id, Function.identity()));

    @Override
    public List<FlightItem> getFlightsFromSearch(
        String origin,
        String destination,
        Optional<String> departureDate,
        Optional<String> returnDate,
        Optional<FlightType> flightType
    ) {
        // search for flights with given parameters
        List<FlightItem> foundFlights = MOCK_DATA
            .values()
            .stream()
            .filter(flightItem ->
                flightItem.origin().equalsIgnoreCase(origin) && flightItem.destination().equalsIgnoreCase(destination)
            )
            .filter(flightItem ->
                departureDate.isPresent() ? flightItem.departureDateTime().equalsIgnoreCase(departureDate.get()) : true
            )
            .filter(flightItem ->
                returnDate.isPresent() ? flightItem.arrivalDateTime().equalsIgnoreCase(returnDate.get()) : true
            )
            .filter(flightItem -> flightType.isPresent() ? flightItem.flightType().equals(flightType.get()) : true)
            .sorted((flight1, flight2) -> flight1.departureDateTime().compareTo(flight2.departureDateTime()))
            .limit(10)
            .toList();

        return foundFlights.isEmpty() ? throwError() : foundFlights;
    }

    private static <T> T throwError() {
        throw new FlightsNotFoundException();
    }
}
