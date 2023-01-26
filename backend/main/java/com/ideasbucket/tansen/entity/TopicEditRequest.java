/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.*;
import java.util.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.common.config.ConfigResource;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "oldRecord", "newRecord" })
public final class TopicEditRequest {

    @JsonProperty("oldRecord")
    @NotNull(message = "Old record cannot be null.")
    @Valid
    private final TopicCreateRequest oldRecord;

    @JsonProperty("newRecord")
    @NotNull(message = "New record cannot be null.")
    @Valid
    private final TopicCreateRequest newRecord;

    @JsonCreator
    public TopicEditRequest(
        @JsonProperty("oldRecord") TopicCreateRequest oldRecord,
        @JsonProperty("newRecord") TopicCreateRequest newRecord
    ) {
        this.oldRecord = oldRecord;
        this.newRecord = newRecord;
    }

    public TopicCreateRequest getNewRecord() {
        return newRecord;
    }

    public TopicCreateRequest getOldRecord() {
        return oldRecord;
    }

    @JsonIgnore
    public Map<ConfigResource, Collection<AlterConfigOp>> getChanges() {
        List<AlterConfigOp> alterations = new ArrayList<>();

        if (!this.oldRecord.getCleanupPolicy().equalsIgnoreCase(this.newRecord.getCleanupPolicy())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("cleanup.policy", this.newRecord.getCleanupPolicy()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getRetentionMs(), this.newRecord.getRetentionMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("retention.ms", this.newRecord.getRetentionMs().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getRetentionBytes(), this.newRecord.getRetentionBytes())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("retention.bytes", this.newRecord.getRetentionBytes().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getMaxMessageBytes(), this.newRecord.getMaxMessageBytes())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("max.message.bytes", this.newRecord.getMaxMessageBytes().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!this.oldRecord.getCompressionType().equalsIgnoreCase(this.newRecord.getCompressionType())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("compression.type", this.newRecord.getCompressionType()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getDeleteRetentionMs(), this.newRecord.getDeleteRetentionMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("delete.retention.ms", this.newRecord.getDeleteRetentionMs().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getFileDeleteDelayMs(), this.newRecord.getFileDeleteDelayMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("file.delete.delay.ms", this.newRecord.getFileDeleteDelayMs().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getFlushMs(), this.newRecord.getFlushMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("flush.ms", this.newRecord.getFlushMs().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getFlushMessages(), this.newRecord.getFlushMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("flush.messages", this.newRecord.getFlushMessages().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (
            !this.oldRecord.getFollowerReplicationThrottledReplicas()
                .equalsIgnoreCase(this.newRecord.getFollowerReplicationThrottledReplicas())
        ) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry(
                        "follower.replication.throttled.replicas",
                        this.newRecord.getFollowerReplicationThrottledReplicas()
                    ),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getIndexIntervalBytes(), this.newRecord.getIndexIntervalBytes())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("index.interval.bytes", this.newRecord.getIndexIntervalBytes().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (
            !this.oldRecord.getLeaderReplicationThrottledReplicas()
                .equalsIgnoreCase(this.newRecord.getLeaderReplicationThrottledReplicas())
        ) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry(
                        "leader.replication.throttled.replicas",
                        this.newRecord.getLeaderReplicationThrottledReplicas()
                    ),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getMaxCompactionLagMs(), this.newRecord.getMaxCompactionLagMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("max.compaction.lag.ms", this.newRecord.getMaxCompactionLagMs().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (this.oldRecord.getMessageDownconversionEnable() != this.newRecord.getMessageDownconversionEnable()) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry(
                        "message.downconversion.enable",
                        this.newRecord.getMessageDownconversionEnable().toString()
                    ),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (
            !Objects.equals(
                this.oldRecord.getMessageTimestampDifferenceMaxMs(),
                this.newRecord.getMessageTimestampDifferenceMaxMs()
            )
        ) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry(
                        "message.timestamp.difference.max.ms",
                        this.newRecord.getMessageTimestampDifferenceMaxMs().toString()
                    ),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!this.oldRecord.getMessageTimestampType().equalsIgnoreCase(this.newRecord.getMessageTimestampType())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("message.timestamp.type", this.newRecord.getMessageTimestampType()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getMinCleanableDirtyRatio(), this.newRecord.getMinCleanableDirtyRatio())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("min.cleanable.dirty.ratio", this.newRecord.getMinCleanableDirtyRatio().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getMinCompactionLagMs(), this.newRecord.getMinCompactionLagMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("min.compaction.lag.ms", this.newRecord.getMinCompactionLagMs().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getMinInsyncReplicas(), this.newRecord.getMinInsyncReplicas())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("min.insync.replicas", this.newRecord.getMinInsyncReplicas().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (this.oldRecord.getPreallocate() != this.newRecord.getPreallocate()) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("preallocate", this.newRecord.getPreallocate().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getSegmentBytes(), this.newRecord.getSegmentBytes())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("segment.bytes", this.newRecord.getSegmentBytes().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getSegmentIndexBytes(), this.newRecord.getSegmentIndexBytes())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("segment.index.bytes", this.newRecord.getSegmentIndexBytes().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getSegmentJitterMs(), this.newRecord.getSegmentJitterMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("segment.jitter.ms", this.newRecord.getSegmentJitterMs().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (!Objects.equals(this.oldRecord.getSegmentMs(), this.newRecord.getSegmentMs())) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry("segment.ms", this.newRecord.getSegmentMs().toString()),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        if (this.oldRecord.getUncleanLeaderElectionEnable() != this.newRecord.getUncleanLeaderElectionEnable()) {
            alterations.add(
                new AlterConfigOp(
                    new ConfigEntry(
                        "unclean.leader.election.enable",
                        this.newRecord.getUncleanLeaderElectionEnable().toString()
                    ),
                    AlterConfigOp.OpType.SET
                )
            );
        }

        return Map.of(new ConfigResource(ConfigResource.Type.TOPIC, this.newRecord.getName()), alterations);
    }
}
