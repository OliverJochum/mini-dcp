/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.info;

import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("java:S1105") //auto-formatting
@Schema(description = "Base information about given service", accessMode = Schema.AccessMode.READ_ONLY)
public record Info(
    @Schema(description = "Service name", example = "Dummy Service") String name,
    Version version,
    @Schema(description = "Full id of the commit (hash)", example = "59c6b7232ca3e1") String commitId
) {}
