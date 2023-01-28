/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "schema", "schemaType", "references" })
public final class SaveSchemaRequest {

    @JsonProperty("schema")
    @NotNull(message = "Schema cannot be null.")
    @NotBlank(message = "Schema cannot be blank.")
    private final String schema;

    @JsonProperty("schemaType")
    private final String schemaType;

    @JsonProperty("references")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Valid
    private final List<Reference> references;

    @JsonCreator
    public SaveSchemaRequest(
        @JsonProperty("schema") String schema,
        @JsonProperty("schemaType") String schemaType,
        @JsonProperty("references") List<Reference> references
    ) {
        this.schema = schema;
        this.schemaType = schemaType == null ? "AVRO" : schemaType.toUpperCase();
        this.references = references == null ? Collections.emptyList() : references;
    }

    public String getSchema() {
        return schema;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public List<Reference> getReferences() {
        return references;
    }

    @AssertTrue(message = "Schema type must be AVRO (default), PROTOBUF, or JSON.")
    @JsonIgnore
    public boolean hasValidSchemaType() {
        return (this.schemaType.equals("PROTOBUF") || this.schemaType.equals("JSON") || this.schemaType.equals("AVRO"));
    }
}
