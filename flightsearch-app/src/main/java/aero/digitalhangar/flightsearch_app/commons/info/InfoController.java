/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.info;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.info.GitProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Info", description = "General information about service")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestController
@SuppressWarnings("unused")
public class InfoController {

    private final Info serviceInformation;

    public InfoController(
        @Value("${spring.application.version.service}") final String serviceVersion,
        @Value("${spring.application.name}") final String description,
        final GitProperties gitProperties
    ) {
        this.serviceInformation = new Info(description, new Version(serviceVersion), gitProperties.getCommitId());
    }

    @Operation(
        summary = "General information",
        description = "Provides general, basic information about the service, kind of a `ping` response"
    )
    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Info info() {
        return this.serviceInformation;
    }
}
