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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "subject", "version" })
public final class Reference {

    @JsonProperty("name")
    @NotNull(message = "Name cannot be null.")
    @NotBlank(message = "Name cannot be blank.")
    private final String name;

    @JsonProperty("subject")
    @NotNull(message = "Subject cannot be null.")
    @NotBlank(message = "Subject cannot be blank.")
    private final String subject;

    @JsonProperty("version")
    @NotNull(message = "Version cannot be null.")
    private final Integer version;

    @JsonCreator
    public Reference(
        @JsonProperty("name") String name,
        @JsonProperty("subject") String subject,
        @JsonProperty("version") Integer version
    ) {
        this.name = name;
        this.subject = subject;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public Integer getVersion() {
        return version;
    }
}
