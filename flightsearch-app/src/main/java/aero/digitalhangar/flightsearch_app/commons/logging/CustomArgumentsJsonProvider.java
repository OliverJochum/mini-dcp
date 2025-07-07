/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.FieldNamesAware;
import net.logstash.logback.fieldnames.LogstashFieldNames;

/**
 * Custom implementation of net.logstash.logback.composite.loggingevent.ArgumentsJsonProvider.
 */
@SuppressWarnings("unused")
public class CustomArgumentsJsonProvider
    extends AbstractFieldJsonProvider<ILoggingEvent>
    implements FieldNamesAware<LogstashFieldNames> {

    @Override
    public void writeTo(final JsonGenerator generator, final ILoggingEvent event) throws IOException {
        Object[] arguments = event.getArgumentArray();

        if (arguments == null || arguments.length == 0) {
            return;
        }

        boolean hasWrittenFieldName = false;

        for (Object argument : arguments) {
            if (argument instanceof StructuredArgument structuredArgument) {
                if (!hasWrittenFieldName && getFieldName() != null) {
                    generator.writeObjectFieldStart(getFieldName());
                    hasWrittenFieldName = true;
                }
                structuredArgument.writeTo(generator);
            }
        }

        if (hasWrittenFieldName) {
            generator.writeEndObject();
        }
    }

    @Override
    public void setFieldNames(LogstashFieldNames fieldNames) {
        setFieldName(fieldNames.getArguments());
    }
}
