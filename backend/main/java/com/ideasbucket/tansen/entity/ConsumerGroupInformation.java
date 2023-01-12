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
@JsonPropertyOrder({ "group", "topic", "partition", "currentOffset", "logEndOffset", "lag" })
public final class ConsumerGroupInformation {

    @JsonProperty("name")
    private final String name;

    @JsonProperty("topic")
    private final String topic;

    @JsonProperty("partition")
    private final Integer partition;

    @JsonProperty("currentOffset")
    private final Long currentOffset;

    @JsonProperty("logEndOffset")
    private final Long logEndOffset;

    @JsonProperty("lag")
    private final Long lag;

    @JsonCreator
    public ConsumerGroupInformation(
        @JsonProperty("name") String name,
        @JsonProperty("topic") String topic,
        @JsonProperty("partition") Integer partition,
        @JsonProperty("currentOffset") Long currentOffset,
        @JsonProperty("logEndOffset") Long logEndOffset,
        @JsonProperty("lag") Long lag
    ) {
        this.name = name;
        this.topic = topic;
        this.partition = partition;
        this.currentOffset = currentOffset;
        this.logEndOffset = logEndOffset;
        this.lag = lag;
    }

    public String getName() {
        return name;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public Long getCurrentOffset() {
        return currentOffset;
    }

    public Long getLogEndOffset() {
        return logEndOffset;
    }

    public Long getLag() {
        return lag;
    }
}
