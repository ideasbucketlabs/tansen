/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "value", "definition", "type" })
public final class ConfigurationWithDefinition {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("value")
    private final String value;

    @JsonProperty("definition")
    private final String definition;

    @JsonProperty("type")
    private final String type;

    @JsonCreator
    public ConfigurationWithDefinition(
        @JsonProperty("name") String name,
        @JsonProperty("value") String value,
        @JsonProperty("definition") String definition,
        @JsonProperty("type") String type
    ) {
        this.name = name;
        this.definition = definition;
        this.type = type;
        this.value = value;
    }

    public ConfigurationWithDefinition(String value, Definition definition) {
        this.value = value;
        this.name = definition.getName();
        this.definition = definition.getDefinition();
        this.type = definition.getType();
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
