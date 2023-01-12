/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "topic", "topicUuid", "numberOfPartition", "partitions", "configurations", "replicationFactor" })
public final class Topic {

    @JsonProperty("topic")
    private final String topic;

    @JsonProperty("topicUuid")
    private final String topicUuid;

    @JsonProperty("numberOfPartition")
    private final Integer numberOfPartition;

    @JsonProperty("partitions")
    private final List<TopicPartition> partitions;

    @JsonProperty("configurations")
    private final List<ConfigurationWithDefinition> configurations;

    @JsonProperty("replicationFactor")
    private final Integer replicationFactor;

    @JsonCreator
    public Topic(
        @JsonProperty("topic") String topic,
        @JsonProperty("topicUuid") String topicUuid,
        @JsonProperty("numberOfPartition") Integer numberOfPartition,
        @JsonProperty("replicationFactor") Integer replicationFactor,
        @JsonProperty("partitions") List<TopicPartition> partitions,
        @JsonProperty("configurations") List<ConfigurationWithDefinition> configurations
    ) {
        this.topic = topic;
        this.topicUuid = topicUuid;
        this.numberOfPartition = numberOfPartition;
        this.partitions = partitions == null ? Collections.emptyList() : partitions;
        this.configurations = configurations == null ? Collections.emptyList() : configurations;
        this.replicationFactor = replicationFactor == null ? -1 : replicationFactor;
    }

    public String getTopic() {
        return topic;
    }

    public String getTopicUuid() {
        return topicUuid;
    }

    public Integer getNumberOfPartition() {
        return numberOfPartition;
    }

    public List<TopicPartition> getPartitions() {
        return partitions;
    }

    public List<ConfigurationWithDefinition> getConfigurations() {
        return configurations;
    }

    public Integer getReplicationFactor() {
        return replicationFactor;
    }

    @JsonIgnore
    public Boolean topicKeyUsesConfluentTopicNamingStrategy() {
        if (this.configurations.isEmpty()) {
            return false;
        }

        var strategy =
            this.configurations.stream()
                .filter(it -> Objects.equals(it.getName(), "confluent.key.subject.name.strategy"))
                .findFirst();

        return (
            strategy.isPresent() &&
            strategy.get().getValue().equals("io.confluent.kafka.serializers.subject.TopicNameStrategy")
        );
    }

    @JsonIgnore
    public Boolean topicValueUsesConfluentTopicNamingStrategy() {
        if (this.configurations.isEmpty()) {
            return false;
        }

        var strategy =
            this.configurations.stream()
                .filter(it -> Objects.equals(it.getName(), "confluent.value.subject.name.strategy"))
                .findFirst();

        return (
            strategy.isPresent() &&
            strategy.get().getValue().equals("io.confluent.kafka.serializers.subject.TopicNameStrategy")
        );
    }
}
