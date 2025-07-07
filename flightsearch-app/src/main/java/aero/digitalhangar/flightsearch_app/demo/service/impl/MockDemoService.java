/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.service.impl;

import aero.digitalhangar.flightsearch_app.demo.model.BookingNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.model.ServiceItem;
import aero.digitalhangar.flightsearch_app.demo.model.ServiceNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.service.DemoService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class MockDemoService implements DemoService {

    private static final String DUMMY_BOOKING_ID = "ABCDEF";

    private static final Map<String, ServiceItem> MOCK_DATA = Stream
        .of(
            new ServiceItem("OT01", "HK"),
            new ServiceItem("OT02", "HL"),
            new ServiceItem("OT03", "HX"),
            new ServiceItem("OT04", "HK")
        )
        .collect(Collectors.toMap(ServiceItem::id, Function.identity()));

    @Override
    public List<ServiceItem> getServicesFromBooking(final String bookingId) {
        return DUMMY_BOOKING_ID.equalsIgnoreCase(bookingId)
            ? new ArrayList<>(MOCK_DATA.values())
            : throwError(bookingId);
    }

    @Override
    public ServiceItem getServiceFromBooking(final String bookingId, final String serviceId) {
        return DUMMY_BOOKING_ID.equalsIgnoreCase(bookingId)
            ? Optional
                .ofNullable(MOCK_DATA.get(serviceId))
                .orElseThrow(() -> new ServiceNotFoundException(bookingId, serviceId))
            : throwError(bookingId);
    }

    private static <T> T throwError(final String orderId) {
        throw new BookingNotFoundException(orderId);
    }
}
