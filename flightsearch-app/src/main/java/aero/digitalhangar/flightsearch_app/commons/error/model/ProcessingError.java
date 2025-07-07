/*
 * Copyright (C) 2022 Lufthansa Group Digital Hangar. All rights reserved.
 *
 * This software is the confidential and proprietary information of Lufthansa Group Digital Hangar.
 */
package aero.digitalhangar.flightsearch_app.commons.error.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("ProcessingError")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "hiddenBuilder")
@EqualsAndHashCode
@ToString
public class ProcessingError {

    // Placeholder to describe that the broken constraint applies to the entire payload object
    public static final String REQUEST_OBJECT_NAME = "request";

    public static final String JSON_PROPERTY_CODE = "code";
    public static final String JSON_PROPERTY_TITLE = "title";
    public static final String JSON_PROPERTY_DESCRIPTION = "description";

    private String code;
    private String title;
    private String description;

    public static ProcessingErrorBuilder builder(ErrorId errorId) {
        return hiddenBuilder().code(errorId.getCode()).title(errorId.getDescription());
    }

    private static ProcessingError.ProcessingErrorBuilder hiddenBuilder() {
        return new ProcessingError.ProcessingErrorBuilder();
    }

    @org.springframework.lang.NonNull
    @NotNull
    @JsonProperty(JSON_PROPERTY_CODE)
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    public void setCode(final String code) {
        this.code = code;
    }

    @org.springframework.lang.NonNull
    @NotNull
    @JsonProperty(JSON_PROPERTY_TITLE)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    public void setTitle(final String title) {
        this.title = title;
    }

    @org.springframework.lang.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(final String description) {
        this.description = description;
    }

    public ErrorMessage toErrorMessage() {
        return ErrorMessage
            .builder()
            .type(ErrorMessage.TypeEnum.E)
            .retryIndicator(Boolean.FALSE)
            .processingErrors(Collections.singleton(this))
            .build();
    }
}
