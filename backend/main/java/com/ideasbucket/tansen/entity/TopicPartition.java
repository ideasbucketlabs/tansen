/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;

@JsonPropertyOrder(
    {
        "topic",
        "partition",
        "leader",
        "replicas",
        "isr",
        "isOffline",
        "inSyncReplicaCount",
        "outOfSyncReplicaCount",
        "isUnderReplicated",
    }
)
public class TopicPartition {

    @JsonProperty("topic")
    private final String topic;

    @JsonProperty("partition")
    private final Integer partition;

    @JsonProperty("leader")
    private final Integer leader;

    @JsonProperty("replicas")
    private final List<Integer> replicas;

    @JsonProperty("isr")
    private final List<Integer> isr;

    @JsonProperty("isOffline")
    private final Boolean isOffline;

    @JsonProperty("inSyncReplicaCount")
    private final Integer inSyncReplicaCount;

    @JsonProperty("outOfSyncReplicaCount")
    private final Integer outOfSyncReplicaCount;

    @JsonProperty("isUnderReplicated")
    private final Boolean isUnderReplicated;

    @JsonCreator
    public TopicPartition(
        @JsonProperty("topic") String topic,
        @JsonProperty("partition") Integer partition,
        @JsonProperty("leader") Integer leader,
        @JsonProperty("replicas") List<Integer> replicas,
        @JsonProperty("isr") List<Integer> isr,
        @JsonProperty("isOffline") Boolean isOffline,
        @JsonProperty("inSyncReplicaCount") Integer inSyncReplicaCount,
        @JsonProperty("outOfSyncReplicaCount") Integer outOfSyncReplicaCount,
        @JsonProperty("isUnderReplicated") Boolean isUnderReplicated
    ) {
        this.topic = topic;
        this.partition = partition;
        this.leader = leader;
        this.replicas = replicas == null ? Collections.emptyList() : replicas;
        this.isr = isr == null ? Collections.emptyList() : isr;
        this.isOffline = isOffline;
        this.inSyncReplicaCount = inSyncReplicaCount;
        this.outOfSyncReplicaCount = outOfSyncReplicaCount;
        this.isUnderReplicated = isUnderReplicated;
    }

    public TopicPartition(String topic, TopicPartitionInfo topicPartitionInfo) {
        this.topic = topic;
        this.partition = topicPartitionInfo.partition();
        this.leader = topicPartitionInfo.leader() == null ? null : topicPartitionInfo.leader().id();
        this.replicas =
            (topicPartitionInfo.replicas() == null)
                ? Collections.emptyList()
                : topicPartitionInfo.replicas().stream().map(Node::id).collect(Collectors.toList());
        this.isr =
            topicPartitionInfo.isr() == null
                ? Collections.emptyList()
                : topicPartitionInfo.isr().stream().map(Node::id).collect(Collectors.toList());
        this.isOffline = topicPartitionInfo.leader() == null;

        this.inSyncReplicaCount = topicPartitionInfo.isr() == null ? 0 : topicPartitionInfo.isr().size();
        this.outOfSyncReplicaCount =
            (topicPartitionInfo.replicas() == null || topicPartitionInfo.isr() == null)
                ? 0
                : (topicPartitionInfo.replicas().size() - topicPartitionInfo.isr().size());

        this.isUnderReplicated =
            topicPartitionInfo.replicas() != null &&
            topicPartitionInfo.isr() != null &&
            (topicPartitionInfo.replicas().size() > topicPartitionInfo.isr().size());
    }

    public String getTopic() {
        return topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public Integer getLeader() {
        return leader;
    }

    public List<Integer> getReplicas() {
        return replicas;
    }

    public List<Integer> getIsr() {
        return isr;
    }

    @JsonProperty(value = "isOffline")
    public Boolean isOffline() {
        return isOffline;
    }

    public Integer getInSyncReplicaCount() {
        return inSyncReplicaCount;
    }

    public Integer getOutOfSyncReplicaCount() {
        return outOfSyncReplicaCount;
    }

    @JsonProperty(value = "isUnderReplicated")
    public Boolean isUnderReplicated() {
        return isUnderReplicated;
    }
}
