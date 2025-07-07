/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import aero.digitalhangar.flightsearch_app.demo.model.BookingNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.model.ServiceItem;
import aero.digitalhangar.flightsearch_app.demo.model.ServiceNotFoundException;
import aero.digitalhangar.flightsearch_app.demo.service.impl.MockDemoService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DemoServiceTest {

    private final DemoService systemUnderTest = new MockDemoService();

    @ParameterizedTest(name = "Scenario for booking ID: {0}")
    @ValueSource(strings = { "", "A", "AA", "AAA", "AAAA", "AAAAA", "AAAAAA", "AAAAAAA" })
    void should_throw_error_when_services_cannot_be_found_for_given_booking_id(final String bookingId) {
        assertThatExceptionOfType(BookingNotFoundException.class)
            .isThrownBy(() -> systemUnderTest.getServicesFromBooking(bookingId))
            .extracting(BookingNotFoundException::getLocalizedMessage)
            .isEqualTo("Booking for provided %s was not found".formatted(bookingId));
    }

    @ParameterizedTest(name = "Scenario for booking ID: {0}")
    @ValueSource(strings = { "", "A", "AA", "AAA", "AAAA", "AAAAA", "AAAAAA", "AAAAAAA" })
    void should_throw_error_when_service_cannot_be_found_for_given_booking_id(final String bookingId) {
        assertThatExceptionOfType(BookingNotFoundException.class)
            .isThrownBy(() -> systemUnderTest.getServiceFromBooking(bookingId, "OT01"))
            .extracting(BookingNotFoundException::getLocalizedMessage)
            .isEqualTo("Booking for provided %s was not found".formatted(bookingId));
    }

    @ParameterizedTest(name = "Scenario for service ID: {0}")
    @ValueSource(strings = { "OT05", "OT4", "XXXX", "" })
    void should_throw_error_when_service_cannot_be_found_for_given_service_id(final String serviceId) {
        assertThatExceptionOfType(ServiceNotFoundException.class)
            .isThrownBy(() -> systemUnderTest.getServiceFromBooking("ABCDEF", serviceId))
            .extracting(ServiceNotFoundException::getLocalizedMessage)
            .isEqualTo("Service for provided %s was not found in the ABCDEF booking".formatted(serviceId));
    }

    @ParameterizedTest(name = "Scenario for booking ID: {0}")
    @ValueSource(strings = { "ABCDEF", "abcdef", "ABCDef", "abcDEF" })
    void should_return_available_services_for_valid_booking_id(final String bookingId) {
        assertThat(systemUnderTest.getServicesFromBooking(bookingId))
            .isNotEmpty()
            .hasSize(4)
            .extracting(ServiceItem::id)
            .containsExactlyInAnyOrder("OT01", "OT02", "OT03", "OT04");
    }

    @ParameterizedTest(name = "Scenario for service ID: {0}")
    @ValueSource(strings = { "OT01", "OT02", "OT03", "OT04" })
    void should_return_service_for_valid_booking_id_and_service_id(final String serviceId) {
        assertThat(systemUnderTest.getServiceFromBooking("ABCDEF", serviceId))
            .isNotNull()
            .extracting(ServiceItem::id)
            .isEqualTo(serviceId);
    }
}
