/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.AssertFalse;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "offset", "partition", "timestamp" })
public final class MessageSelectionCriteria {

    @JsonProperty("offset")
    private final Long offset;

    @JsonProperty("partition")
    private final Integer partition;

    @JsonProperty("timestamp")
    private final Instant timestamp;

    @JsonCreator
    public MessageSelectionCriteria(
        @JsonProperty("offset") Long offset,
        @JsonProperty("partition") Integer partition,
        @JsonProperty("timestamp") Instant timestamp
    ) {
        this.offset = offset;
        this.partition = partition;
        this.timestamp = timestamp;
    }

    public Long getOffset() {
        return offset;
    }

    public Integer getPartition() {
        return partition;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @AssertFalse(message = "Invalid offset.")
    @JsonIgnore
    private boolean hasValidOffset() {
        return this.offset != null && this.offset < 0;
    }

    @AssertFalse(message = "Invalid partition.")
    @JsonIgnore
    private boolean hasValidPartition() {
        return this.partition != null && this.partition < -1;
    }

    @JsonIgnore
    public String getCase() {

        if (this.partition == null) {
            return "";
        }

        return (this.offset != null) ? "offset" : "timestamp";
    }
}
