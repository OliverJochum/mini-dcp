/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@SuppressWarnings({ "unused", "java:S2384" }) //false-positive
@JsonTypeName("ErrorMessage")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "hiddenBuilder")
@EqualsAndHashCode
@ToString
public class ErrorMessage {

    public static final String JSON_PROPERTY_TYPE = "type";
    public static final String JSON_PROPERTY_RETRY_INDICATOR = "retryIndicator";
    public static final String JSON_PROPERTY_PROCESSING_ERRORS = "processingErrors";

    private TypeEnum type;
    private Boolean retryIndicator;
    private Set<ProcessingError> processingErrors = new HashSet<>();

    public static ErrorMessage.ErrorMessageBuilder builder() {
        return hiddenBuilder().type(TypeEnum.E).retryIndicator(Boolean.FALSE);
    }

    private static ErrorMessage.ErrorMessageBuilder hiddenBuilder() {
        return new ErrorMessage.ErrorMessageBuilder();
    }

    @org.springframework.lang.NonNull
    @NotNull
    @JsonProperty(JSON_PROPERTY_TYPE)
    public TypeEnum getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(final TypeEnum type) {
        this.type = type;
    }

    @org.springframework.lang.NonNull
    @NotNull
    @JsonProperty(JSON_PROPERTY_RETRY_INDICATOR)
    public Boolean getRetryIndicator() {
        return retryIndicator;
    }

    @JsonProperty(JSON_PROPERTY_RETRY_INDICATOR)
    public void setRetryIndicator(final Boolean retryIndicator) {
        this.retryIndicator = retryIndicator;
    }

    @org.springframework.lang.NonNull
    @NotEmpty
    @Valid
    @JsonProperty(JSON_PROPERTY_PROCESSING_ERRORS)
    public Set<ProcessingError> getProcessingErrors() {
        return processingErrors;
    }

    @JsonProperty(JSON_PROPERTY_PROCESSING_ERRORS)
    public void setProcessingErrors(final Set<ProcessingError> processingErrors) {
        this.processingErrors = processingErrors;
    }

    public enum TypeEnum {
        E,
        W;

        @SuppressWarnings("unused")
        @JsonCreator
        public static TypeEnum fromValue(final String value) {
            for (TypeEnum enumItem : TypeEnum.values()) {
                if (enumItem.name().equals(value)) {
                    return enumItem;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "' for 'TypeEnum' enum.");
        }
    }
}
