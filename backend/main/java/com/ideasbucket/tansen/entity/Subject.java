/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ideasbucket.tansen.validator.FromList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "provider", "type", "value" })
public class Subject {

    @JsonProperty("provider")
    @NotNull(message = "Provider cannot be null.")
    @NotBlank(message = "Provider cannot be blank.")
    @FromList(acceptedValues = { "okta" }, message = "Currently only Okta provider is supported.")
    private final String provider;

    @JsonProperty("type")
    @NotNull(message = "Type cannot be null.")
    @NotBlank(message = "Type cannot be blank.")
    @FromList(
        acceptedValues = { "domain", "user", "organization", "group" },
        message = "Type must be either domain, user, organization, group."
    )
    private final String type;

    @JsonProperty("value")
    @NotNull(message = "Value cannot be null.")
    @NotBlank(message = "Value cannot be blank.")
    private final String value;

    public Subject(
        @JsonProperty("provider") String provider,
        @JsonProperty("type") String type,
        @JsonProperty("value") String value
    ) {
        this.provider = provider.toLowerCase();
        this.type = type.toLowerCase();
        this.value = value;
    }

    public String getProvider() {
        return provider;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
