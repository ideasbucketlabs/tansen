/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "offset", "partition", "timestamp" })
public final class MessageSelectionCriteria {

    @JsonProperty("offset")
    @NotNull(message = "Offset cannot be null")
    @Min(value = 0, message = "Offset cannot be less than 0.")
    private final Long offset;

    @JsonProperty("partition")
    @NotNull(message = "Partition cannot be null")
    @Min(value = 0, message = "Partition cannot be less than 0.")
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

    @JsonIgnore
    public String getCase() {
        if (this.partition == null) {
            return "";
        }

        return (this.offset != null) ? "offset" : "timestamp";
    }
}
