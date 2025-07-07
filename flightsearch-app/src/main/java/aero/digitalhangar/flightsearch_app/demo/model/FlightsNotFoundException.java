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
public class FlightsNotFoundException extends RuntimeException implements ErrorIdReturningException {

    @Override
    public ErrorId getErrorId() {
        return UniqueError.DATA_NOT_FOUND;
    }

    @Override
    public String getLocalizedMessage() {
        return "Flight for provided parameters was not found";
    }
}
