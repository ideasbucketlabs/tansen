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
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "subject", "version", "id", "schema", "schemaType" })
public final class TopicSchema {

    @JsonProperty("subject")
    private final String subject;

    @JsonProperty("version")
    private final Integer version;

    @JsonProperty("id")
    private final Integer id;

    @JsonProperty("schema")
    private final String schema;

    @JsonProperty("schemaType")
    private final String schemaType;

    @JsonProperty("references")
    private final List<Reference> references;

    @JsonCreator
    public TopicSchema(
        @JsonProperty("subject") String subject,
        @JsonProperty("version") Integer version,
        @JsonProperty("id") Integer id,
        @JsonProperty("schema") String schema,
        @JsonProperty("schemaType") String schemaType,
        @JsonProperty("references") List<Reference> references
    ) {
        this.subject = subject;
        this.version = version;
        this.id = id;
        this.schema = schema;
        this.schemaType = schemaType == null ? "AVRO" : schemaType;
        this.references = references == null ? Collections.emptyList() : Collections.unmodifiableList(references);
    }

    public String getSubject() {
        return subject;
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getId() {
        return id;
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
}
