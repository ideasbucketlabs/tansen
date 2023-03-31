/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.TopicConfig;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    {
        "cleanup.policy",
        "compression.type",
        "delete.retention.ms",
        "file.delete.delay.ms",
        "flush.messages",
        "flush.ms",
        "follower.replication.throttled.replicas",
        "index.interval.bytes",
        "leader.replication.throttled.replicas",
        "max.compaction.lag.ms",
        "max.message.bytes",
        "message.downconversion.enable",
        "message.format.version",
        "message.timestamp.difference.max.ms",
        "message.timestamp.type",
        "min.cleanable.dirty.ratio",
        "min.compaction.lag.ms",
        "min.insync.replicas",
        "replication.factor",
        "retention.bytes",
        "retention.ms",
        "segment.bytes",
        "segment.index.bytes",
        "segment.jitter.ms",
        "segment.ms",
        "unclean.leader.election.enable",
        "name",
        "partition",
        "preallocate",
    }
)
public final class TopicCreateRequest {

    @JsonProperty("cleanup.policy")
    @NotNull(message = "cleanup.policy must be either delete or compact.")
    @Pattern(
        regexp = "^(?:compact|delete|compact,delete)$",
        message = "cleanup.policy must be either delete or compact or compact, delete."
    )
    private final String cleanupPolicy;

    @JsonProperty("compression.type")
    @NotNull(message = "compression.type cannot be null.")
    @Pattern(
        regexp = "^(?:producer|uncompressed|zstd|lz4|snappy|gzip)$",
        message = "Compression type must be producer or uncompressed or zstd or lz4 or snappy or gzip."
    )
    private final String compressionType;

    @JsonProperty("delete.retention.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "Delete retention time cannot be null and must be greater than -1.")
    @Min(value = 0, message = "Delete retention time cannot be null and must be greater than -1.")
    private final Long deleteRetentionMs;

    @JsonProperty("file.delete.delay.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Min(value = 0, message = "File delete delay time cannot be null, must be greater than -1.")
    private final Long fileDeleteDelayMs;

    @JsonProperty("flush.messages")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Min(value = 1, message = "Flush messages interval cannot be null, must be integer and greater than 0.")
    private final Long flushMessages;

    @JsonProperty("flush.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Min(value = 0, message = "Flush messages time interval cannot be null, must be greater than -1.")
    private final Long flushMs;

    @JsonProperty("follower.replication.throttled.replicas")
    private final String followerReplicationThrottledReplicas;

    @JsonProperty("index.interval.bytes")
    @Min(value = 0, message = "Index interval cannot be null, must be greater than -1.")
    private final Integer indexIntervalBytes;

    @JsonProperty("leader.replication.throttled.replicas")
    private final String leaderReplicationThrottledReplicas;

    @JsonProperty("max.compaction.lag.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Min(value = 1, message = "Maximum compaction lag cannot be null, must be greater than 0.")
    private final Long maxCompactionLagMs;

    @JsonProperty("max.message.bytes")
    @Min(value = 1, message = "Maximum message bytes cannot be null, must be numeric and greater than -1.")
    private final Integer maxMessageBytes;

    @JsonProperty("message.downconversion.enable")
    @NotNull(message = "Message down conversion enable cannot be null.")
    private final Boolean messageDownconversionEnable;

    @JsonProperty("message.timestamp.difference.max.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Min(value = 0, message = "Maximum timestamp difference cannot be null, must be greater than -1.")
    private final Long messageTimestampDifferenceMaxMs;

    @JsonProperty("message.timestamp.type")
    @NotNull(message = "Message timestamp type cannot be null.")
    @Pattern(
        regexp = "^(?:CreateTime|LogAppendTime)$",
        message = "Message timestamp type can be either CreateTime or LogAppendTime."
    )
    private final String messageTimestampType;

    @JsonProperty("min.cleanable.dirty.ratio")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "Minimum cleanable ratio cannot be null must be numeric and between 0 and 1. Only two digits allowed after decimal."
    )
    @DecimalMax(
        value = "1.0",
        inclusive = true,
        message = "Minimum cleanable ratio cannot be null must be numeric and between 0 and 1. Only two digits allowed after decimal."
    )
    @Digits(
        integer = 2,
        fraction = 2,
        message = "Minimum cleanable ratio cannot be null must be numeric and between 0 and 1. Only two digits allowed after decimal."
    )
    private final Double minCleanableDirtyRatio;

    @JsonProperty("min.compaction.lag.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Min(value = 0, message = "Minimum compaction lag cannot be null, must be greater than -1.")
    private final Long minCompactionLagMs;

    @JsonProperty("min.insync.replicas")
    @Min(value = 1, message = "Minimum In-Sync replica cannot be less than 1.")
    @Max(value = 32766, message = "Replication factor cannot be more than 32766.")
    @JsonDeserialize(as = Short.class)
    private final Short minInsyncReplicas;

    @JsonProperty("replication.factor")
    @Min(value = 1, message = "Replication factor cannot be null and must be at least 1.")
    @Max(value = 32766, message = "Replication factor cannot be more than 32766.")
    @JsonDeserialize(as = Short.class)
    private final Short replicationFactor;

    @JsonProperty("retention.bytes")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private final Long retentionBytes;

    @JsonProperty("retention.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private final Long retentionMs;

    @JsonProperty("segment.bytes")
    @Min(value = 1, message = "Segment bytes cannot be null, must be greater than 0.")
    private final Integer segmentBytes;

    @JsonProperty("segment.index.bytes")
    @Min(value = 1, message = "Segment index bytes cannot be null, must be greater than 0.")
    private final Integer segmentIndexBytes;

    @JsonProperty("segment.jitter.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Min(value = 0, message = "Segment jitter cannot be null, must be greater than -1.")
    private final Long segmentJitterMs;

    @JsonProperty("segment.ms")
    @JsonDeserialize(as = Long.class)
    @JsonSerialize(using = ToStringSerializer.class)
    @Min(value = 1, message = "Segment cannot be null, must be greater than 0.")
    private final Long segmentMs;

    @JsonProperty("unclean.leader.election.enable")
    @NotNull(message = "Unclean leader election enable cannot be null.")
    private final Boolean uncleanLeaderElectionEnable;

    @JsonProperty("name")
    @NotNull(message = "Topic name cannot be null.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Invalid topic name.")
    private final String name;

    @JsonProperty("partition")
    @Min(value = 1, message = "Partition cannot be less than 1.")
    private final Integer partition;

    @JsonProperty("preallocate")
    @NotNull(message = "Pre allocate cannot be null.")
    private final Boolean preallocate;

    @JsonCreator
    public TopicCreateRequest(
        @JsonProperty("cleanup.policy") String cleanupPolicy,
        @JsonProperty("compression.type") String compressionType,
        @JsonProperty("delete.retention.ms") Long deleteRetentionMs,
        @JsonProperty("file.delete.delay.ms") Long fileDeleteDelayMs,
        @JsonProperty("flush.messages") Long flushMessages,
        @JsonProperty("flush.ms") Long flushMs,
        @JsonProperty("follower.replication.throttled.replicas") String followerReplicationThrottledReplicas,
        @JsonProperty("index.interval.bytes") Integer indexIntervalBytes,
        @JsonProperty("leader.replication.throttled.replicas") String leaderReplicationThrottledReplicas,
        @JsonProperty("max.compaction.lag.ms") Long maxCompactionLagMs,
        @JsonProperty("max.message.bytes") Integer maxMessageBytes,
        @JsonProperty("message.downconversion.enable") Boolean messageDownconversionEnable,
        @JsonProperty("message.timestamp.difference.max.ms") Long messageTimestampDifferenceMaxMs,
        @JsonProperty("message.timestamp.type") String messageTimestampType,
        @JsonProperty("min.cleanable.dirty.ratio") Double minCleanableDirtyRatio,
        @JsonProperty("min.compaction.lag.ms") Long minCompactionLagMs,
        @JsonProperty("min.insync.replicas") Short minInsyncReplicas,
        @JsonProperty("replication.factor") Short replicationFactor,
        @JsonProperty("retention.bytes") Long retentionBytes,
        @JsonProperty("retention.ms") Long retentionMs,
        @JsonProperty("segment.bytes") Integer segmentBytes,
        @JsonProperty("segment.index.bytes") Integer segmentIndexBytes,
        @JsonProperty("segment.jitter.ms") Long segmentJitterMs,
        @JsonProperty("segment.ms") Long segmentMs,
        @JsonProperty("unclean.leader.election.enable") Boolean uncleanLeaderElectionEnable,
        @JsonProperty("name") String name,
        @JsonProperty("partition") Integer partition,
        @JsonProperty("preallocate") Boolean preallocate
    ) {
        this.cleanupPolicy = cleanupPolicy;
        this.compressionType = compressionType;
        this.deleteRetentionMs = deleteRetentionMs;
        this.fileDeleteDelayMs = fileDeleteDelayMs;
        this.flushMessages = flushMessages;
        this.flushMs = flushMs;
        this.followerReplicationThrottledReplicas = followerReplicationThrottledReplicas.trim();
        this.indexIntervalBytes = indexIntervalBytes;
        this.leaderReplicationThrottledReplicas = leaderReplicationThrottledReplicas.trim();
        this.maxCompactionLagMs = maxCompactionLagMs;
        this.maxMessageBytes = maxMessageBytes;
        this.messageDownconversionEnable = messageDownconversionEnable;
        this.messageTimestampDifferenceMaxMs = messageTimestampDifferenceMaxMs;
        this.messageTimestampType = messageTimestampType;
        this.minCleanableDirtyRatio = minCleanableDirtyRatio;
        this.minCompactionLagMs = minCompactionLagMs;
        this.minInsyncReplicas = minInsyncReplicas;
        this.replicationFactor = replicationFactor;
        this.retentionBytes = retentionBytes;
        this.retentionMs = retentionMs;
        this.segmentBytes = segmentBytes;
        this.segmentIndexBytes = segmentIndexBytes;
        this.segmentJitterMs = segmentJitterMs;
        this.segmentMs = segmentMs;
        this.uncleanLeaderElectionEnable = uncleanLeaderElectionEnable;
        this.name = name;
        this.partition = partition;
        this.preallocate = preallocate;
    }

    public TopicCreateRequest(String name, Integer partition) {
        this.name = name;
        this.partition = partition;
        this.cleanupPolicy = "delete";
        this.compressionType = "producer";
        this.deleteRetentionMs = 86400000L;
        this.fileDeleteDelayMs = 60000L;
        this.flushMessages = 9223372036854775807L;
        this.flushMs = 9223372036854775807L;
        this.followerReplicationThrottledReplicas = "";
        this.indexIntervalBytes = 4096;
        this.leaderReplicationThrottledReplicas = "";
        this.maxCompactionLagMs = 9223372036854775807L;
        this.maxMessageBytes = 1048588;
        this.messageDownconversionEnable = true;
        this.messageTimestampDifferenceMaxMs = 9223372036854775807L;
        this.messageTimestampType = "CreateTime";
        this.minCleanableDirtyRatio = 0.5;
        this.minCompactionLagMs = 0L;
        this.minInsyncReplicas = 1;
        this.replicationFactor = 1;
        this.retentionBytes = -1L;
        this.retentionMs = 604800000L;
        this.segmentBytes = 1073741824;
        this.segmentIndexBytes = 10485760;
        this.segmentJitterMs = 0L;
        this.segmentMs = 604800000L;
        this.uncleanLeaderElectionEnable = false;
        this.preallocate = false;
    }

    @AssertTrue(message = "Retention bytes cannot be null, must be numeric either exactly -1 or greater than 0.")
    @JsonIgnore
    private boolean hasValidRetentionBytes() {
        if (this.retentionBytes == null) {
            return false;
        }

        return this.retentionBytes == -1L || this.retentionBytes > 0L;
    }

    @AssertFalse(message = "Minimum in-sync replica must be equal or less than replication factor.")
    @JsonIgnore
    private boolean hasMinInsyncReplicas() {

        return (this.replicationFactor < this.minInsyncReplicas);
    }

    @AssertTrue(message = "Retention time cannot be null, must be numeric either exactly -1 or greater than 0.")
    @JsonIgnore
    private boolean hasValidRetentionMs() {
        if (this.retentionMs == null) {
            return false;
        }

        return this.retentionMs == -1L || this.retentionMs > 0L;
    }

    @JsonIgnore
    public Map<String, String> getTopicOptions() {
        Map<String, String> topicOptions = new HashMap<>();
        topicOptions.put(TopicConfig.CLEANUP_POLICY_CONFIG, this.cleanupPolicy);
        topicOptions.put(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, this.minInsyncReplicas.toString());
        topicOptions.putAll(addIfNotDefault("producer", TopicConfig.COMPRESSION_TYPE_CONFIG, this.compressionType));
        topicOptions.putAll(addIfNotDefault(86400000L, TopicConfig.DELETE_RETENTION_MS_CONFIG, this.deleteRetentionMs));
        topicOptions.putAll(addIfNotDefault(60000L, TopicConfig.FILE_DELETE_DELAY_MS_CONFIG, this.fileDeleteDelayMs));
        topicOptions.putAll(
            addIfNotDefault(9223372036854775807L, TopicConfig.FLUSH_MESSAGES_INTERVAL_CONFIG, this.flushMessages)
        );
        topicOptions.putAll(addIfNotDefault(9223372036854775807L, TopicConfig.FLUSH_MS_CONFIG, this.flushMs));
        topicOptions.putAll(
            addIfNotDefault("", "follower.replication.throttled.replicas", this.followerReplicationThrottledReplicas)
        );
        topicOptions.putAll(addIfNotDefault(4096, TopicConfig.INDEX_INTERVAL_BYTES_CONFIG, this.indexIntervalBytes));
        topicOptions.putAll(
            addIfNotDefault("", "leader.replication.throttled.replicas", this.leaderReplicationThrottledReplicas)
        );
        topicOptions.putAll(
            addIfNotDefault(9223372036854775807L, TopicConfig.MAX_COMPACTION_LAG_MS_CONFIG, this.maxCompactionLagMs)
        );
        topicOptions.putAll(addIfNotDefault(1048588, TopicConfig.MAX_MESSAGE_BYTES_CONFIG, this.maxMessageBytes));
        topicOptions.putAll(
            addIfNotDefault(true, TopicConfig.MESSAGE_DOWNCONVERSION_ENABLE_CONFIG, this.messageDownconversionEnable)
        );
        topicOptions.putAll(
            addIfNotDefault(
                9223372036854775807L,
                TopicConfig.MESSAGE_TIMESTAMP_DIFFERENCE_MAX_MS_CONFIG,
                this.messageTimestampDifferenceMaxMs
            )
        );
        topicOptions.putAll(
            addIfNotDefault("CreateTime", TopicConfig.MESSAGE_TIMESTAMP_TYPE_CONFIG, this.messageTimestampType)
        );

        if (!(Math.abs(this.minCleanableDirtyRatio - 0.5) < 0.000001d)) {
            topicOptions.put(TopicConfig.MIN_CLEANABLE_DIRTY_RATIO_CONFIG, this.minCleanableDirtyRatio.toString());
        }

        topicOptions.putAll(addIfNotDefault(0L, TopicConfig.MIN_COMPACTION_LAG_MS_CONFIG, this.minCompactionLagMs));
        topicOptions.putAll(addIfNotDefault(1, TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, this.minInsyncReplicas));
        topicOptions.putAll(addIfNotDefault(-1L, TopicConfig.RETENTION_BYTES_CONFIG, this.retentionBytes));
        topicOptions.putAll(addIfNotDefault(604800000L, TopicConfig.RETENTION_MS_CONFIG, this.retentionMs));

        topicOptions.putAll(addIfNotDefault(1073741824, TopicConfig.SEGMENT_BYTES_CONFIG, this.segmentBytes));
        topicOptions.putAll(addIfNotDefault(10485760, TopicConfig.SEGMENT_INDEX_BYTES_CONFIG, this.segmentIndexBytes));

        topicOptions.putAll(addIfNotDefault(0L, TopicConfig.SEGMENT_JITTER_MS_CONFIG, this.segmentJitterMs));
        topicOptions.putAll(addIfNotDefault(604800000L, TopicConfig.SEGMENT_MS_CONFIG, this.segmentMs));
        topicOptions.putAll(
            addIfNotDefault(false, TopicConfig.UNCLEAN_LEADER_ELECTION_ENABLE_CONFIG, this.uncleanLeaderElectionEnable)
        );
        topicOptions.putAll(addIfNotDefault(false, TopicConfig.PREALLOCATE_CONFIG, this.preallocate));

        return Map.copyOf(topicOptions);
    }

    @JsonIgnore
    private Map<String, String> addIfNotDefault(Object defaultValue, String configuration, Object givenValue) {
        if (defaultValue instanceof Boolean) {
            return (defaultValue != givenValue) ? Map.of(configuration, givenValue.toString()) : Map.of();
        }
        return (!defaultValue.equals(givenValue)) ? Map.of(configuration, givenValue.toString()) : Map.of();
    }

    public String getCleanupPolicy() {
        return cleanupPolicy;
    }

    public String getCompressionType() {
        return compressionType;
    }

    public Long getDeleteRetentionMs() {
        return deleteRetentionMs;
    }

    public Long getFileDeleteDelayMs() {
        return fileDeleteDelayMs;
    }

    public Long getFlushMessages() {
        return flushMessages;
    }

    public Long getFlushMs() {
        return flushMs;
    }

    public String getFollowerReplicationThrottledReplicas() {
        return followerReplicationThrottledReplicas;
    }

    public Integer getIndexIntervalBytes() {
        return indexIntervalBytes;
    }

    public String getLeaderReplicationThrottledReplicas() {
        return leaderReplicationThrottledReplicas;
    }

    public Long getMaxCompactionLagMs() {
        return maxCompactionLagMs;
    }

    public Integer getMaxMessageBytes() {
        return maxMessageBytes;
    }

    public Boolean getMessageDownconversionEnable() {
        return messageDownconversionEnable;
    }

    public Long getMessageTimestampDifferenceMaxMs() {
        return messageTimestampDifferenceMaxMs;
    }

    public String getMessageTimestampType() {
        return messageTimestampType;
    }

    public Double getMinCleanableDirtyRatio() {
        return minCleanableDirtyRatio;
    }

    public Long getMinCompactionLagMs() {
        return minCompactionLagMs;
    }

    public Short getMinInsyncReplicas() {
        return minInsyncReplicas;
    }

    public Short getReplicationFactor() {
        return replicationFactor;
    }

    public Long getRetentionBytes() {
        return retentionBytes;
    }

    public Long getRetentionMs() {
        return retentionMs;
    }

    public Integer getSegmentBytes() {
        return segmentBytes;
    }

    public Integer getSegmentIndexBytes() {
        return segmentIndexBytes;
    }

    public Long getSegmentJitterMs() {
        return segmentJitterMs;
    }

    public Long getSegmentMs() {
        return segmentMs;
    }

    public Boolean getUncleanLeaderElectionEnable() {
        return uncleanLeaderElectionEnable;
    }

    public String getName() {
        return name;
    }

    public Integer getPartition() {
        return partition;
    }

    public Boolean getPreallocate() {
        return preallocate;
    }
}
