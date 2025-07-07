/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.handler;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorId;
import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorMessage;
import aero.digitalhangar.flightsearch_app.commons.error.model.ProcessingError;
import aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestController
public class DefaultErrorController implements ErrorController {

    private static ErrorId resolveErrorIdFrom(final HttpStatus httpStatus) {
        return switch (httpStatus) {
            case NOT_FOUND -> UniqueError.DATA_NOT_FOUND;
            case BAD_REQUEST -> UniqueError.PARAMETER_UNSPECIFIED_PROBLEM;
            default -> UniqueError.INTERNAL_ERROR;
        };
    }

    @SuppressWarnings({ "java:S1166", "java:S2221" }) // special error handling not needed
    private static HttpStatus resolveStatusFrom(final HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        try {
            return HttpStatus.valueOf(statusCode);
        } catch (final Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<ErrorMessage> handleError(final HttpServletRequest request) {
        HttpStatus httpStatus = resolveStatusFrom(request);

        return ResponseEntity
            .status(httpStatus)
            .headers(httpHeaders -> httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)))
            .body(
                ProcessingError
                    .builder(resolveErrorIdFrom(httpStatus))
                    .description(
                        "Requested URI: " + request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString()
                    )
                    .build()
                    .toErrorMessage()
            );
    }
}
