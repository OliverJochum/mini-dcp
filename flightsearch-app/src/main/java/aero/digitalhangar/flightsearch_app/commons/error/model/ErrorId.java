/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.model;

/**
 * Extension of typical {@link UniqueError}, allowing to define service/scope specific IDs,
 * that are not need to be promoted to a 'common' structure.
 */
public interface ErrorId {
    String getCode();

    String getDescription();
}
