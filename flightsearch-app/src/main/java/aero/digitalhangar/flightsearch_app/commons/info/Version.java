/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.info;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Service version reference", accessMode = Schema.AccessMode.READ_ONLY)
public record Version(@Schema(description = "Service version", example = "0.0.1") String service) {}
