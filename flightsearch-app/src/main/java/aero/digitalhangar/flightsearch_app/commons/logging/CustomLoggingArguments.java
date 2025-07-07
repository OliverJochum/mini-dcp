/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.logging;

import aero.digitalhangar.flightsearch_app.commons.error.model.ErrorId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Custom implementation of net.logstash.logback.argument.StructuredArguments
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomLoggingArguments {

    /**
     * The default message format used when writing key value pairs to the log message.
     */
    private static final String DEFAULT_KEY_VALUE_MESSAGE_FORMAT_PATTERN = "{0}={1}";

    /**
     * A message format pattern that will only write
     * the argument value to a log message (i.e. it won't write the key).
     */
    private static final String VALUE_ONLY_MESSAGE_FORMAT_PATTERN = "{1}";

    /**
     * Adds OPS severity information as "severity":"severity-value" to the JSON event.
     *
     * @see ObjectAppendingMarker
     * @see Severity
     */
    public static StructuredArgument severity(final Severity severity) {
        return new ObjectAppendingMarker("severity", severity.toString(), DEFAULT_KEY_VALUE_MESSAGE_FORMAT_PATTERN);
    }

    /**
     * Adds error code to ease OPS tracking as "errorId":"errorId-value" to the JSON event.
     * It is currently assumed that <code>errorId</code> is selectable only from known, documented unique errors.
     *
     * @see ObjectAppendingMarker
     * @see ErrorId
     * @see aero.digitalhangar.flightsearch_app.commons.error.model.UniqueError
     */
    public static StructuredArgument errorId(final ErrorId errorId) {
        return new ObjectAppendingMarker("errorId", errorId.getCode(), DEFAULT_KEY_VALUE_MESSAGE_FORMAT_PATTERN);
    }

    /**
     * Adds "OSI":"stack-trace" to the JSON event; OSI stands for other service information.
     *
     * @see ObjectAppendingMarker
     */
    public static StructuredArgument stack(final Throwable throwable) {
        return new ObjectAppendingMarker(
            "OSI",
            ExceptionUtils.getStackTrace(throwable),
            DEFAULT_KEY_VALUE_MESSAGE_FORMAT_PATTERN
        );
    }

    /**
     * Adds "key":"value" to the JSON event AND name/value to the formatted message.
     *
     * @see ObjectAppendingMarker
     */
    public static StructuredArgument keyValue(String key, Object value) {
        return new ObjectAppendingMarker(key, value, DEFAULT_KEY_VALUE_MESSAGE_FORMAT_PATTERN);
    }

    /**
     * Adds "key":"value" to the JSON event AND value <b>only</b> to the formatted message (<b>without</b> the key).
     *
     * @see ObjectAppendingMarker
     */
    public static StructuredArgument value(String key, Object value) {
        return new ObjectAppendingMarker(key, value, VALUE_ONLY_MESSAGE_FORMAT_PATTERN);
    }
}
