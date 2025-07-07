/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.service;

import aero.digitalhangar.flightsearch_app.demo.model.ServiceItem;
import java.util.List;

public interface DemoService {
    List<ServiceItem> getServicesFromBooking(final String bookingId);

    ServiceItem getServiceFromBooking(final String bookingId, final String serviceId);
}
