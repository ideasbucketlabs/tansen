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
@JsonPropertyOrder({ "name", "definition", "type" })
public final class Definition {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("definition")
    private final String definition;

    @JsonProperty("type")
    private final String type;

    @JsonCreator
    public Definition(
        @JsonProperty("name") String name,
        @JsonProperty("definition") String definition,
        @JsonProperty("type") String type
    ) {
        this.name = name;
        this.definition = definition;
        this.type = type;
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
}
