/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.demo.model;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorId;
import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorIdReturningException;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceNotFoundException extends RuntimeException implements ErrorIdReturningException {

    private final String bookingId;
    private final String serviceId;

    @Override
    public ErrorId getErrorId() {
        return UniqueError.DATA_NOT_FOUND;
    }

    @Override
    public String getLocalizedMessage() {
        return "Service for provided " + serviceId + " was not found in the " + bookingId + " booking";
    }
}
