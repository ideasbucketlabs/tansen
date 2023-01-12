/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util

import com.ideasbucket.tansen.entity.Definition

object TopicConfigurationDefinition {

    /* ktlint-disable max-line-length */
    // formatter:off
    private val definitions: Map<String, Definition> = mapOf(
        "cleanup.policy" to Definition(
            "cleanup.policy",
            "This config designates the retention policy to use on log segments. The 'delete' policy (which is the default) will discard old segments when their retention time or size limit has been reached. The \"compact\" policy will enable log compaction, which retains the latest value for each key. It is also possible to specify both policies in a comma-separated list (e.g. \"delete,compact\"). In this case, old segments will be discarded per the retention time and size configuration, while retained segments will be compacted.",
            "list"
        ),
        "compression.type" to Definition(
            "compression.type",
            "Specify the final compression type for a given topic. This configuration accepts the standard compression codecs ('gzip', 'snappy', 'lz4', 'zstd'). It additionally accepts 'uncompressed' which is equivalent to no compression; and 'producer' which means retain the original compression codec set by the producer.",
            "string"
        ),
        "confluent.key.schema.validation" to Definition(
            "confluent.key.schema.validation",
            "True if schema validation at record key is enabled for this topic.",
            "boolean"
        ),
        "confluent.key.subject.name.strategy" to Definition(
            "confluent.key.subject.name.strategy",
            "Determines how to construct the subject name under which the key schema is registered with the schema registry. By default, TopicNameStrategy is used",
            "string"
        ),
        "confluent.tier.enable" to Definition(
            "confluent.tier.enable",
            "Allow tiering for topic(s). This enables tiering and fetching of data to and from the configured remote storage.",
            "boolean"
        ),
        "confluent.tier.local.hotset.bytes" to Definition(
            "confluent.tier.local.hotset.bytes",
            "When tiering is enabled, this configuration controls the maximum size a partition (which consists of log segments) can grow to on broker-local storage before we will discard old log segments to free up space. Log segments retained on broker-local storage is referred as the \"hotset\". Segments discarded from local store could continue to exist in tiered storage and remain available for fetches depending on retention configurations. By default there is no size limit only a time limit. Since this limit is enforced at the partition level, multiply it by the number of partitions to compute the topic hotset in bytes.",
            "long"
        ),
        "confluent.tier.local.hotset.ms" to Definition(
            "confluent.tier.local.hotset.ms",
            "When tiering is enabled, this configuration controls the maximum time we will retain a log segment on broker-local storage before we will discard it to free up space. Segments discarded from local store could continue to exist in tiered storage and remain available for fetches depending on retention configurations. If set to -1, no time limit is applied.",
            "long"
        ),
        "confluent.value.schema.validation" to Definition(
            "confluent.value.schema.validation",
            "True if schema validation at record value is enabled for this topic.",
            "boolean"
        ),
        "confluent.value.subject.name.strategy" to Definition(
            "confluent.value.subject.name.strategy",
            "Determines how to construct the subject name under which the value schema is registered with the schema registry. By default, TopicNameStrategy is used",
            "string"
        ),
        "delete.retention.ms" to Definition(
            "delete.retention.ms",
            "The amount of time to retain delete tombstone markers for log compacted topics. This setting also gives a bound on the time in which a consumer must complete a read if they begin from offset 0 to ensure that they get a valid snapshot of the final stage (otherwise delete tombstones may be collected before they complete their scan).",
            "long"
        ),
        "file.delete.delay.ms" to Definition(
            "file.delete.delay.ms",
            "The time to wait before deleting a file from the filesystem",
            "long"
        ),
        "flush.messages" to Definition(
            "flush.messages",
            "This setting allows specifying an interval at which we will force an fsync of data written to the log. For example if this was set to 1 we would fsync after every message; if it were 5 we would fsync after every five messages. In general we recommend you not set this and use replication for durability and allow the operating system’s background flush capabilities as it is more efficient. This setting can be overridden on a per-topic basis (see the per-topic configuration section).",
            "long"
        ),
        "flush.ms" to Definition(
            "flush.ms",
            "This setting allows specifying a time interval at which we will force an fsync of data written to the log. For example if this was set to 1000 we would fsync after 1000 ms had passed. In general we recommend you not set this and use replication for durability and allow the operating system’s background flush capabilities as it is more efficient.",
            "long"
        ),
        "follower.replication.throttled.replicas" to Definition(
            "follower.replication.throttled.replicas",
            "A list of replicas for which log replication should be throttled on the follower side. The list should describe a set of replicas in the form [PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:… or alternatively the wildcard '*' can be used to throttle all replicas for this topic.",
            "list"
        ),
        "index.interval.bytes" to Definition(
            "index.interval.bytes",
            "This setting controls how frequently Kafka adds an index entry to its offset index. The default setting ensures that we index a message roughly every 4096 bytes. More indexing allows reads to jump closer to the exact position in the log but makes the index larger. You probably don’t need to change this.",
            "int"
        ),
        "leader.replication.throttled.replicas" to Definition(
            "leader.replication.throttled.replicas",
            "A list of replicas for which log replication should be throttled on the leader side. The list should describe a set of replicas in the form [PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:… or alternatively the wildcard '*' can be used to throttle all replicas for this topic.",
            "list"
        ),
        "max.compaction.lag.ms" to Definition(
            "max.compaction.lag.ms",
            "The maximum time a message will remain ineligible for compaction in the log. Only applicable for logs that are being compacted.",
            "long"
        ),
        "max.message.bytes" to Definition(
            "max.message.bytes",
            "The largest record batch size allowed by Kafka (after compression if compression is enabled). If this is increased and there are consumers older than 0.10.2, the consumers’ fetch size must also be increased so that they can fetch record batches this large. In the latest message format version, records are always grouped into batches for efficiency. In previous message format versions, uncompressed records are not grouped into batches and this limit only applies to a single record in that case.",
            "int"
        ),
        "message.format.version" to Definition(
            "message.format.version",
            "[DEPRECATED] Specify the message format version the broker will use to append messages to the logs. The value of this config is always assumed to be 3.0 if inter.broker.protocol.version is 3.0 or higher (the actual config value is ignored). Otherwise, the value should be a valid ApiVersion. Some examples are: 0.10.0, 1.1, 2.8, 3.0. By setting a particular message format version, the user is certifying that all the existing messages on disk are smaller or equal than the specified version. Setting this value incorrectly will cause consumers with older versions to break as they will receive messages with a format that they don’t understand.",
            "string"
        ),
        "message.timestamp.difference.max.ms" to Definition(
            "message.timestamp.difference.max.ms",
            "The maximum difference allowed between the timestamp when a broker receives a message and the timestamp specified in the message. If message.timestamp.type=CreateTime, a message will be rejected if the difference in timestamp exceeds this threshold. This configuration is ignored if message.timestamp.type=LogAppendTime.",
            "long"
        ),
        "message.timestamp.type" to Definition(
            "message.timestamp.type",
            "Define whether the timestamp in the message is message create time or log append time. The value should be either CreateTime or LogAppendTime",
            "string"
        ),
        "min.cleanable.dirty.ratio" to Definition(
            "min.cleanable.dirty.ratio",
            "This configuration controls how frequently the log compactor will attempt to clean the log (assuming log compaction is enabled). By default we will avoid cleaning a log where more than 50% of the log has been compacted. This ratio bounds the maximum space wasted in the log by duplicates (at 50% at most 50% of the log could be duplicates). A higher ratio will mean fewer, more efficient cleanings but will mean more wasted space in the log. If the max.compaction.lag.ms or the min.compaction.lag.ms configurations are also specified, then the log compactor considers the log to be eligible for compaction as soon as either: (i) the dirty ratio threshold has been met and the log has had dirty (uncompacted) records for at least the min.compaction.lag.ms duration, or (ii) if the log has had dirty (uncompacted) records for at most the max.compaction.lag.ms period.",
            "double"
        ),
        "min.compaction.lag.ms" to Definition(
            "min.compaction.lag.ms",
            "The minimum time a message will remain uncompacted in the log. Only applicable for logs that are being compacted.",
            "long"
        ),
        "min.insync.replicas" to Definition(
            "min.insync.replicas",
            "When a producer sets acks to \"all\" (or \"-1\"), this configuration specifies the minimum number of replicas that must acknowledge a write for the write to be considered successful. If this minimum cannot be met, then the producer will raise an exception (either NotEnoughReplicas or NotEnoughReplicasAfterAppend).When used together, min.insync.replicas and acks allow you to enforce greater durability guarantees. A typical scenario would be to create a topic with a replication factor of 3, set min.insync.replicas to 2, and produce with acks of \"all\". This will ensure that the producer raises an exception if a majority of replicas do not receive a write.",
            "int"
        ),
        "preallocate" to Definition(
            "preallocate",
            "True if we should preallocate the file on disk when creating a new log segment.",
            "boolean"
        ),
        "retention.bytes" to Definition(
            "retention.bytes",
            "This configuration controls the maximum size a partition (which consists of log segments) can grow to before we will discard old log segments to free up space if we are using the “delete” retention policy. By default there is no size limit only a time limit. Since this limit is enforced at the partition level, multiply it by the number of partitions to compute the topic retention in bytes.",
            "long"
        ),
        "retention.ms" to Definition(
            "retention.ms",
            "This configuration controls the maximum time we will retain a log before we will discard old log segments to free up space if we are using the \"delete\" retention policy. This represents an SLA on how soon consumers must read their data. If set to -1, no time limit is applied.",
            "long"
        ),
        "segment.bytes" to Definition(
            "segment.bytes",
            "This configuration controls the segment file size for the log. Retention and cleaning is always done a file at a time so a larger segment size means fewer files but less granular control over retention.",
            "int"
        ),
        "segment.index.bytes" to Definition(
            "segment.index.bytes",
            "This configuration controls the size of the index that maps offsets to file positions. We preallocate this index file and shrink it only after log rolls. You generally should not need to change this setting.",
            "int"
        ),
        "segment.jitter.ms" to Definition(
            "segment.jitter.ms",
            "The maximum random jitter subtracted from the scheduled segment roll time to avoid thundering herds of segment rolling.",
            "long"
        ),
        "segment.ms" to Definition(
            "segment.ms",
            "This configuration controls the period of time after which Kafka will force the log to roll even if the segment file isn’t full to ensure that retention can delete or compact old data.",
            "long"
        ),
        "unclean.leader.election.enable" to Definition(
            "unclean.leader.election.enable",
            "Indicates whether to enable replicas not in the ISR set to be elected as leader as a last resort, even though doing so may result in data loss.",
            "boolean"
        ),
        "confluent.placement.constraints" to Definition(
            "confluent.placement.constraints",
            "This configuration is a JSON object that controls the set of brokers (replicas) which will always be allowed to join the ISR. And the set of brokers (observers) which are not allowed to join the ISR. The format of JSON is:{ \"version\": 1, \"replicas\": [ { \"count\": 2, \"constraints\": {\"rack\": \"east-1\"} }, { \"count\": 1, \"constraints\": {\"rack\": \"east-2\"} } ], \"observers\":[ { \"count\": 1, \"constraints\": {\"rack\": \"west-1\"} } ]}",
            "string"
        ),
        "message.downconversion.enable" to Definition(
            "message.downconversion.enable",
            "This configuration controls whether down-conversion of message formats is enabled to satisfy consume requests. When set to false, broker will not perform down-conversion for consumers expecting an older message format. The broker responds with UNSUPPORTED_VERSION error for consume requests from such older clients. This configuration does not apply to any message format conversion that might be required for replication to followers.",
            "boolean"
        ),
    )
    // formatter:on

    @JvmStatic
    fun getDefinition(definition: String): Definition {
        return definitions.getOrDefault(
            definition,
            Definition(definition, "", "unknown")
        )
    }
}
