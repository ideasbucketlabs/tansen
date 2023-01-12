/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util

import com.ideasbucket.tansen.entity.Definition

object BrokerConfigurationDefinition {

    /* ktlint-disable max-line-length */
    // formatter:off
    private val definitions: Map<String, Definition> = mapOf(
        "advertised.listeners" to Definition(
            "advertised.listeners",
            "Listeners to publish to ZooKeeper for clients to use, if different than the <code>listeners</code> config property. In IaaS environments, this may need to be different from the interface to which the broker binds. If this is not set, the value for <code>listeners</code> will be used. Unlike <code>listeners</code>, it is not valid to advertise the 0.0.0.0 meta-address.<br> Also unlike <code>listeners</code>, there can be duplicated ports in this property, so that one listener can be configured to advertise another listener's address. This can be useful in some cases where external load balancers are used.",
            "string"
        ),
        "auto.create.topics.enable" to Definition(
            "auto.create.topics.enable",
            "Enable auto creation of topic on the server",
            "boolean"
        ),
        "auto.leader.rebalance.enable" to Definition(
            "auto.leader.rebalance.enable",
            "Enables auto leader balancing. A background thread checks the distribution of partition leaders at regular intervals, configurable by `leader.imbalance.check.interval.seconds`. If the leader imbalance exceeds `leader.imbalance.per.broker.percentage`, leader rebalance to the preferred leader for partitions is triggered.",
            "boolean"
        ),
        "background.threads" to Definition(
            "background.threads",
            "The number of threads to use for various background processing tasks",
            "int"
        ),
        "broker.id" to Definition(
            "broker.id",
            "The broker id for this server. If unset, a unique broker id will be generated.To avoid conflicts between zookeeper generated broker id's and user configured broker id's, generated broker ids start from reserved.broker.max.id + 1.",
            "int"
        ),
        "compression.type" to Definition(
            "compression.type",
            "Specify the final compression type for a given topic. This configuration accepts the standard compression codecs (‘gzip’, ‘snappy’, ‘lz4’, ‘zstd’). It additionally accepts ‘uncompressed’ which is equivalent to no compression; and ‘producer’ which means retain the original compression codec set by the producer. Default value is ‘producer’.",
            "string"
        ),
        "control.plane.listener.name" to Definition(
            "control.plane.listener.name",
            "Name of listener used for communication between controller and brokers. Broker will use the control.plane.listener.name to locate the endpoint in listeners list, to listen for connections from the controller. For example, if a broker's config is :<br>listeners = INTERNAL://192.1.1.8:9092, EXTERNAL://10.1.1.5:9093, CONTROLLER://192.1.1.8:9094<br>listener.security.protocol.map = INTERNAL:PLAINTEXT, EXTERNAL:SSL, CONTROLLER:SSL<br>control.plane.listener.name = CONTROLLER<br>On startup, the broker will start listening on \"192.1.1.8:9094\" with security protocol \"SSL\".<br>On controller side, when it discovers a broker's published endpoints through zookeeper, it will use the control.plane.listener.name to find the endpoint, which it will use to establish connection to the broker.<br>For example, if the broker's published endpoints on zookeeper are :<br>\"endpoints\" : [\"INTERNAL://broker1.example.com:9092\",\"EXTERNAL://broker1.example.com:9093\",\"CONTROLLER://broker1.example.com:9094\"]<br> and the controller's config is :<br>listener.security.protocol.map = INTERNAL:PLAINTEXT, EXTERNAL:SSL, CONTROLLER:SSL<br>control.plane.listener.name = CONTROLLER<br>then controller will use \"broker1.example.com:9094\" with security protocol \"SSL\" to connect to the broker.<br>If not explicitly configured, the default value will be null and there will be no dedicated endpoints for controller connections.<br>If explicitly configured, the value cannot be the same as the value of <code>inter.broker.listener.name</code>.",
            "string"
        ),
        "controller.listener.names" to Definition(
            "controller.listener.names",
            "A comma-separated list of the names of the listeners used by the controller. This is required if running in KRaft mode. When communicating with the controller quorum, the broker will always use the first listener in this list.<br> Note: The ZK-based controller should not set this configuration.",
            "string"
        ),
        "controller.quorum.election.backoff.max.ms" to Definition(
            "controller.quorum.election.backoff.max.ms",
            "Maximum time in milliseconds before starting new elections. This is used in the binary exponential backoff mechanism that helps prevent gridlocked elections",
            "int"
        ),
        "controller.quorum.election.timeout.ms" to Definition(
            "controller.quorum.election.timeout.ms",
            "Maximum time in milliseconds to wait without being able to fetch from the leader before triggering a new election",
            "int"
        ),
        "controller.quorum.fetch.timeout.ms" to Definition(
            "controller.quorum.fetch.timeout.ms",
            "Maximum time without a successful fetch from the current leader before becoming a candidate and triggering an election for voters; Maximum time without receiving fetch from a majority of the quorum before asking around to see if there's a new epoch for leader",
            "int"
        ),
        "controller.quorum.voters" to Definition(
            "controller.quorum.voters",
            "Map of id/endpoint information for the set of voters in a comma-separated list of `{id}@{host}:{port}` entries. For example: `1@localhost:9092,2@localhost:9093,3@localhost:9094`",
            "list"
        ),
        "delete.topic.enable" to Definition(
            "delete.topic.enable",
            "Enables delete topic. Delete topic through the admin tool will have no effect if this config is turned off",
            "boolean"
        ),
        "early.start.listeners" to Definition(
            "early.start.listeners",
            "A comma-separated list of listener names which may be started before the authorizer has finished initialization. This is useful when the authorizer is dependent on the cluster itself for bootstrapping, as is the case for the StandardAuthorizer (which stores ACLs in the metadata log.) By default, all listeners included in controller.listener.names will also be early start listeners. A listener should not appear in this list if it accepts external traffic.",
            "string"
        ),
        "leader.imbalance.check.interval.seconds" to Definition(
            "leader.imbalance.check.interval.seconds",
            "The frequency with which the partition rebalance check is triggered by the controller",
            "long"
        ),
        "leader.imbalance.per.broker.percentage" to Definition(
            "leader.imbalance.per.broker.percentage",
            "The ratio of leader imbalance allowed per broker. The controller would trigger a leader balance if it goes above this value per broker. The value is specified in percentage.",
            "int"
        ),
        "listeners" to Definition(
            "listeners",
            "List of comma-separated URIs the REST API will listen on. The supported protocols are HTTP and HTTPS.<br> Specify hostname as 0.0.0.0 to bind to all interfaces.<br> Leave hostname empty to bind to default interface.<br> Examples of legal listener lists: HTTP://myhost:8083,HTTPS://myhost:8084",
            "list"
        ),
        "log.dir" to Definition(
            "log.dir",
            "The directory in which the log data is kept (supplemental for log.dirs property)",
            "string"
        ),
        "log.dirs" to Definition(
            "log.dirs",
            "A comma-separated list of the directories where the log data is stored. If not set, the value in log.dir is used.",
            "string"
        ),
        "log.flush.interval.messages" to Definition(
            "log.flush.interval.messages",
            "The number of messages accumulated on a log partition before messages are flushed to disk ",
            "long"
        ),
        "log.flush.interval.ms" to Definition(
            "log.flush.interval.ms",
            "The maximum time in ms that a message in any topic is kept in memory before flushed to disk. If not set, the value in log.flush.scheduler.interval.ms is used",
            "long"
        ),
        "log.flush.offset.checkpoint.interval.ms" to Definition(
            "log.flush.offset.checkpoint.interval.ms",
            "The frequency with which we update the persistent record of the last flush which acts as the log recovery point",
            "int"
        ),
        "log.flush.scheduler.interval.ms" to Definition(
            "log.flush.scheduler.interval.ms",
            "The frequency in ms that the log flusher checks whether any log needs to be flushed to disk",
            "long"
        ),
        "log.flush.start.offset.checkpoint.interval.ms" to Definition(
            "log.flush.start.offset.checkpoint.interval.ms",
            "The frequency with which we update the persistent record of log start offset",
            "int"
        ),
        "log.retention.bytes" to Definition(
            "log.retention.bytes",
            "The maximum size of the log before deleting it",
            "long"
        ),
        "log.retention.hours" to Definition(
            "log.retention.hours",
            "The number of hours to keep a log file before deleting it (in hours), tertiary to log.retention.ms property",
            "int"
        ),
        "log.retention.minutes" to Definition(
            "log.retention.minutes",
            "The number of minutes to keep a log file before deleting it (in minutes), secondary to log.retention.ms property. If not set, the value in log.retention.hours is used",
            "int"
        ),
        "log.retention.ms" to Definition(
            "log.retention.ms",
            "The number of milliseconds to keep a log file before deleting it (in milliseconds), If not set, the value in log.retention.minutes is used. If set to -1, no time limit is applied.",
            "long"
        ),
        "log.roll.hours" to Definition(
            "log.roll.hours",
            "The maximum time before a new log segment is rolled out (in hours), secondary to log.roll.ms property",
            "int"
        ),
        "log.roll.jitter.hours" to Definition(
            "log.roll.jitter.hours",
            "The maximum jitter to subtract from logRollTimeMillis (in hours), secondary to log.roll.jitter.ms property",
            "int"
        ),
        "log.roll.jitter.ms" to Definition(
            "log.roll.jitter.ms",
            "The maximum jitter to subtract from logRollTimeMillis (in milliseconds). If not set, the value in log.roll.jitter.hours is used",
            "long"
        ),
        "log.roll.ms" to Definition(
            "log.roll.ms",
            "The maximum time before a new log segment is rolled out (in milliseconds). If not set, the value in log.roll.hours is used",
            "long"
        ),
        "log.segment.bytes" to Definition(
            "log.segment.bytes",
            "The maximum size of a single log file",
            "int"
        ),
        "log.segment.delete.delay.ms" to Definition(
            "log.segment.delete.delay.ms",
            "The amount of time to wait before deleting a file from the filesystem",
            "long"
        ),
        "message.max.bytes" to Definition(
            "message.max.bytes",
            "The largest record batch size allowed by Kafka (after compression if compression is enabled). If this is increased and there are consumers older than 0.10.2, the consumers' fetch size must also be increased so that they can fetch record batches this large. In the latest message format version, records are always grouped into batches for efficiency. In previous message format versions, uncompressed records are not grouped into batches and this limit only applies to a single record in that case.This can be set per topic with the topic level <code>max.message.bytes</code> config.",
            "int"
        ),
        "metadata.log.dir" to Definition(
            "metadata.log.dir",
            "This configuration determines where we put the metadata log for clusters in KRaft mode. If it is not set, the metadata log is placed in the first log directory from log.dirs.",
            "string"
        ),
        "metadata.log.max.record.bytes.between.snapshots" to Definition(
            "metadata.log.max.record.bytes.between.snapshots",
            "This is the maximum number of bytes in the log between the latest snapshot and the high-watermark needed before generating a new snapshot.",
            "long"
        ),
        "metadata.log.segment.bytes" to Definition(
            "metadata.log.segment.bytes",
            "The maximum size of a single metadata log file.",
            "int"
        ),
        "metadata.log.segment.ms" to Definition(
            "metadata.log.segment.ms",
            "The maximum time before a new metadata log file is rolled out (in milliseconds).",
            "long"
        ),
        "metadata.max.retention.bytes" to Definition(
            "metadata.max.retention.bytes",
            "The maximum combined size of the metadata log and snapshots before deleting old snapshots and log files. Since at least one snapshot must exist before any logs can be deleted, this is a soft limit.",
            "long"
        ),
        "metadata.max.retention.ms" to Definition(
            "metadata.max.retention.ms",
            "The number of milliseconds to keep a metadata log file or snapshot before deleting it. Since at least one snapshot must exist before any logs can be deleted, this is a soft limit.",
            "long"
        ),
        "min.insync.replicas" to Definition(
            "min.insync.replicas",
            "When a producer sets acks to \"all\" (or \"-1\"), this configuration specifies the minimum number of replicas that must acknowledge a write for the write to be considered successful. If this minimum cannot be met, then the producer will raise an exception (either NotEnoughReplicas or NotEnoughReplicasAfterAppend).<br>When used together, <code>min.insync.replicas</code> and <code>acks</code> allow you to enforce greater durability guarantees. A typical scenario would be to create a topic with a replication factor of 3, set <code>min.insync.replicas</code> to 2, and produce with <code>acks</code> of \"all\". This will ensure that the producer raises an exception if a majority of replicas do not receive a write.",
            "int"
        ),
        "node.id" to Definition(
            "node.id",
            "The node ID associated with the roles this process is playing when `process.roles` is non-empty. Every node in a KRaft cluster must have a unique `node.id`, this includes broker and controller nodes. This is required configuration when running in KRaft mode.",
            "int"
        ),
        "num.io.threads" to Definition(
            "num.io.threads",
            "The number of threads that the server uses for processing requests, which may include disk I/O",
            "int"
        ),
        "num.network.threads" to Definition(
            "num.network.threads",
            "The number of threads that the server uses for receiving requests from the network and sending responses to the network",
            "int"
        ),
        "num.recovery.threads.per.data.dir" to Definition(
            "num.recovery.threads.per.data.dir",
            "The number of threads per data directory to be used for log recovery at startup and flushing at shutdown",
            "int"
        ),
        "num.replica.alter.log.dirs.threads" to Definition(
            "num.replica.alter.log.dirs.threads",
            "The number of threads that can move replicas between log directories, which may include disk I/O",
            "int"
        ),
        "num.replica.fetchers" to Definition(
            "num.replica.fetchers",
            "Number of fetcher threads used to replicate records from each source broker. The total number of fetchers on each broker is bound by <code>num.replica.fetchers</code> multiplied by the number of brokers in the cluster.Increasing this value can increase the degree of I/O parallelism in the follower and leader broker at the cost of higher CPU and memory utilization.",
            "int"
        ),
        "offset.metadata.max.bytes" to Definition(
            "offset.metadata.max.bytes",
            "The maximum size for a metadata entry associated with an offset commit",
            "int"
        ),
        "offsets.commit.required.acks" to Definition(
            "offsets.commit.required.acks",
            "The required acks before the commit can be accepted. In general, the default (-1) should not be overridden",
            "short"
        ),
        "offsets.commit.timeout.ms" to Definition(
            "offsets.commit.timeout.ms",
            "Offset commit will be delayed until all replicas for the offsets topic receive the commit or this timeout is reached. This is similar to the producer request timeout.",
            "int"
        ),
        "offsets.load.buffer.size" to Definition(
            "offsets.load.buffer.size",
            "Batch size for reading from the offsets segments when loading offsets into the cache (soft-limit, overridden if records are too large).",
            "int"
        ),
        "offsets.retention.check.interval.ms" to Definition(
            "offsets.retention.check.interval.ms",
            "Frequency at which to check for stale offsets",
            "long"
        ),
        "offsets.retention.minutes" to Definition(
            "offsets.retention.minutes",
            "After a consumer group loses all its consumers (i.e. becomes empty) its offsets will be kept for this retention period before getting discarded. For standalone consumers (using manual assignment), offsets will be expired after the time of last commit plus this retention period.",
            "int"
        ),
        "offsets.topic.compression.codec" to Definition(
            "offsets.topic.compression.codec",
            "Compression codec for the offsets topic - compression may be used to achieve \"atomic\" commits",
            "int"
        ),
        "offsets.topic.num.partitions" to Definition(
            "offsets.topic.num.partitions",
            "The number of partitions for the offset commit topic (should not change after deployment)",
            "int"
        ),
        "offsets.topic.replication.factor" to Definition(
            "offsets.topic.replication.factor",
            "The replication factor for the offsets topic (set higher to ensure availability). Internal topic creation will fail until the cluster size meets this replication factor requirement.",
            "short"
        ),
        "offsets.topic.segment.bytes" to Definition(
            "offsets.topic.segment.bytes",
            "The offsets topic segment bytes should be kept relatively small in order to facilitate faster log compaction and cache loads",
            "int"
        ),
        "process.roles" to Definition(
            "process.roles",
            "The roles that this process plays: 'broker', 'controller', or 'broker,controller' if it is both. This configuration is only applicable for clusters in KRaft (Kafka Raft) mode (instead of ZooKeeper). Leave this config undefined or empty for Zookeeper clusters.",
            "list"
        ),
        "queued.max.requests" to Definition(
            "queued.max.requests",
            "The number of queued requests allowed for data-plane, before blocking the network threads",
            "int"
        ),
        "replica.fetch.min.bytes" to Definition(
            "replica.fetch.min.bytes",
            "Minimum bytes expected for each fetch response. If not enough bytes, wait up to <code>replica.fetch.wait.max.ms</code> (broker config).",
            "int"
        ),
        "replica.fetch.wait.max.ms" to Definition(
            "replica.fetch.wait.max.ms",
            "The maximum wait time for each fetcher request issued by follower replicas. This value should always be less than the replica.lag.time.max.ms at all times to prevent frequent shrinking of ISR for low throughput topics",
            "int"
        ),
        "replica.high.watermark.checkpoint.interval.ms" to Definition(
            "replica.high.watermark.checkpoint.interval.ms",
            "The frequency with which the high watermark is saved out to disk",
            "long"
        ),
        "replica.lag.time.max.ms" to Definition(
            "replica.lag.time.max.ms",
            "If a follower hasn't sent any fetch requests or hasn't consumed up to the leaders log end offset for at least this time, the leader will remove the follower from isr",
            "long"
        ),
        "replica.socket.receive.buffer.bytes" to Definition(
            "replica.socket.receive.buffer.bytes",
            "The socket receive buffer for network requests",
            "int"
        ),
        "replica.socket.timeout.ms" to Definition(
            "replica.socket.timeout.ms",
            "The socket timeout for network requests. Its value should be at least replica.fetch.wait.max.ms",
            "int"
        ),
        "request.timeout.ms" to Definition(
            "request.timeout.ms",
            "The configuration controls the maximum amount of time the client will wait for the response of a request. If the response is not received before the timeout elapses the client will resend the request if necessary or fail the request if retries are exhausted.",
            "int"
        ),
        "sasl.mechanism.controller.protocol" to Definition(
            "sasl.mechanism.controller.protocol",
            "SASL mechanism used for communication with controllers. Default is GSSAPI.",
            "string"
        ),
        "socket.receive.buffer.bytes" to Definition(
            "socket.receive.buffer.bytes",
            "The SO_RCVBUF buffer of the socket server sockets. If the value is -1, the OS default will be used.",
            "int"
        ),
        "socket.request.max.bytes" to Definition(
            "socket.request.max.bytes",
            "The maximum number of bytes in a socket request",
            "int"
        ),
        "socket.send.buffer.bytes" to Definition(
            "socket.send.buffer.bytes",
            "The SO_SNDBUF buffer of the socket server sockets. If the value is -1, the OS default will be used.",
            "int"
        ),
        "transaction.max.timeout.ms" to Definition(
            "transaction.max.timeout.ms",
            "The maximum allowed timeout for transactions. If a client's requested transaction time exceed this, then the broker will return an error in InitProducerIdRequest. This prevents a client from too large of a timeout, which can stall consumers reading from topics included in the transaction.",
            "int"
        ),
        "transaction.state.log.load.buffer.size" to Definition(
            "transaction.state.log.load.buffer.size",
            "Batch size for reading from the transaction log segments when loading producer ids and transactions into the cache (soft-limit, overridden if records are too large).",
            "int"
        ),
        "transaction.state.log.min.isr" to Definition(
            "transaction.state.log.min.isr",
            "Overridden min.insync.replicas config for the transaction topic.",
            "int"
        ),
        "transaction.state.log.num.partitions" to Definition(
            "transaction.state.log.num.partitions",
            "The number of partitions for the transaction topic (should not change after deployment).",
            "int"
        ),
        "transaction.state.log.replication.factor" to Definition(
            "transaction.state.log.replication.factor",
            "The replication factor for the transaction topic (set higher to ensure availability). Internal topic creation will fail until the cluster size meets this replication factor requirement.",
            "short"
        ),
        "transaction.state.log.segment.bytes" to Definition(
            "transaction.state.log.segment.bytes",
            "The transaction topic segment bytes should be kept relatively small in order to facilitate faster log compaction and cache loads",
            "int"
        ),
        "transactional.id.expiration.ms" to Definition(
            "transactional.id.expiration.ms",
            "The time in ms that the transaction coordinator will wait without receiving any transaction status updates for the current transaction before expiring its transactional id. This setting also influences producer id expiration - producer ids are expired once this time has elapsed after the last write with the given producer id. Note that producer ids may expire sooner if the last write from the producer id is deleted due to the topic's retention settings.",
            "int"
        ),
        "unclean.leader.election.enable" to Definition(
            "unclean.leader.election.enable",
            "Indicates whether to enable replicas not in the ISR set to be elected as leader as a last resort, even though doing so may result in data loss.",
            "boolean"
        ),
        "zookeeper.connect" to Definition(
            "zookeeper.connect",
            "Specifies the ZooKeeper connection string in the form <code>hostname:port</code> where host and port are the host and port of a ZooKeeper server. To allow connecting through other ZooKeeper nodes when that ZooKeeper machine is down you can also specify multiple hosts in the form <code>hostname1:port1,hostname2:port2,hostname3:port3</code>.<br>The server can also have a ZooKeeper chroot path as part of its ZooKeeper connection string which puts its data under some path in the global ZooKeeper namespace. For example to give a chroot path of <code>/chroot/path</code> you would give the connection string as <code>hostname1:port1,hostname2:port2,hostname3:port3/chroot/path</code>.",
            "string"
        ),
        "zookeeper.connection.timeout.ms" to Definition(
            "zookeeper.connection.timeout.ms",
            "The max time that the client waits to establish a connection to zookeeper. If not set, the value in zookeeper.session.timeout.ms is used",
            "int"
        ),
        "zookeeper.max.in.flight.requests" to Definition(
            "zookeeper.max.in.flight.requests",
            "The maximum number of unacknowledged requests the client will send to Zookeeper before blocking.",
            "int"
        ),
        "zookeeper.session.timeout.ms" to Definition(
            "zookeeper.session.timeout.ms",
            "Zookeeper session timeout",
            "int"
        ),
        "zookeeper.set.acl" to Definition(
            "zookeeper.set.acl",
            "Set client to use secure ACLs",
            "boolean"
        ),
        "broker.heartbeat.interval.ms" to Definition(
            "broker.heartbeat.interval.ms",
            "The length of time in milliseconds between broker heartbeats. Used when running in KRaft mode.",
            "int"
        ),
        "broker.id.generation.enable" to Definition(
            "broker.id.generation.enable",
            "Enable automatic broker id generation on the server. When enabled the value configured for reserved.broker.max.id should be reviewed.",
            "boolean"
        ),
        "broker.rack" to Definition(
            "broker.rack",
            "Rack of the broker. This will be used in rack aware replication assignment for fault tolerance. Examples: `RACK1`, `us-east-1d`",
            "string"
        ),
        "broker.session.timeout.ms" to Definition(
            "broker.session.timeout.ms",
            "The length of time in milliseconds that a broker lease lasts if no heartbeats are made. Used when running in KRaft mode.",
            "int"
        ),
        "connections.max.idle.ms" to Definition(
            "connections.max.idle.ms",
            "Close idle connections after the number of milliseconds specified by this config.",
            "long"
        ),
        "connections.max.reauth.ms" to Definition(
            "connections.max.reauth.ms",
            "When explicitly set to a positive number (the default is 0, not a positive number), a session lifetime that will not exceed the configured value will be communicated to v2.2.0 or later clients when they authenticate. The broker will disconnect any such connection that is not re-authenticated within the session lifetime and that is then subsequently used for any purpose other than re-authentication. Configuration names can optionally be prefixed with listener prefix and SASL mechanism name in lower-case. For example, listener.name.sasl_ssl.oauthbearer.connections.max.reauth.ms=3600000",
            "long"
        ),
        "controlled.shutdown.enable" to Definition(
            "controlled.shutdown.enable",
            "Enable controlled shutdown of the server",
            "boolean"
        ),
        "controlled.shutdown.max.retries" to Definition(
            "controlled.shutdown.max.retries",
            "Controlled shutdown can fail for multiple reasons. This determines the number of retries when such failure happens",
            "int"
        ),
        "controlled.shutdown.retry.backoff.ms" to Definition(
            "controlled.shutdown.retry.backoff.ms",
            "Before each retry, the system needs time to recover from the state that caused the previous failure (Controller fail over, replica lag etc). This config determines the amount of time to wait before retrying.",
            "long"
        ),
        "controller.quorum.append.linger.ms" to Definition(
            "controller.quorum.append.linger.ms",
            "The duration in milliseconds that the leader will wait for writes to accumulate before flushing them to disk.",
            "int"
        ),
        "controller.quorum.request.timeout.ms" to Definition(
            "controller.quorum.request.timeout.ms",
            "The configuration controls the maximum amount of time the client will wait for the response of a request. If the response is not received before the timeout elapses the client will resend the request if necessary or fail the request if retries are exhausted.",
            "int"
        ),
        "controller.socket.timeout.ms" to Definition(
            "controller.socket.timeout.ms",
            "The socket timeout for controller-to-broker channels",
            "int"
        ),
        "default.replication.factor" to Definition(
            "default.replication.factor",
            "The default replication factors for automatically created topics",
            "int"
        ),
        "delegation.token.expiry.time.ms" to Definition(
            "delegation.token.expiry.time.ms",
            "The token validity time in miliseconds before the token needs to be renewed. Default value 1 day.",
            "long"
        ),
        "delegation.token.master.key" to Definition(
            "delegation.token.master.key",
            "DEPRECATED: An alias for delegation.token.secret.key, which should be used instead of this config.",
            "password"
        ),
        "delegation.token.max.lifetime.ms" to Definition(
            "delegation.token.max.lifetime.ms",
            "The token has a maximum lifetime beyond which it cannot be renewed anymore. Default value 7 days.",
            "long"
        ),
        "delegation.token.secret.key" to Definition(
            "delegation.token.secret.key",
            "Secret key to generate and verify delegation tokens. The same key must be configured across all the brokers.  If the key is not set or set to empty string, brokers will disable the delegation token support.",
            "password"
        ),
        "delete.records.purgatory.purge.interval.requests" to Definition(
            "delete.records.purgatory.purge.interval.requests",
            "The purge interval (in number of requests) of the delete records request purgatory",
            "int"
        ),
        "fetch.max.bytes" to Definition(
            "fetch.max.bytes",
            "The maximum amount of data the server should return for a fetch request. Records are fetched in batches by the consumer, and if the first record batch in the first non-empty partition of the fetch is larger than this value, the record batch will still be returned to ensure that the consumer can make progress. As such, this is not a absolute maximum. The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or <code>max.message.bytes</code> (topic config). Note that the consumer performs multiple fetches in parallel.",
            "int"
        ),
        "fetch.purgatory.purge.interval.requests" to Definition(
            "fetch.purgatory.purge.interval.requests",
            "The purge interval (in number of requests) of the fetch request purgatory",
            "int"
        ),
        "group.initial.rebalance.delay.ms" to Definition(
            "group.initial.rebalance.delay.ms",
            "The amount of time the group coordinator will wait for more consumers to join a new group before performing the first rebalance. A longer delay means potentially fewer rebalances, but increases the time until processing begins.",
            "int"
        ),
        "group.max.session.timeout.ms" to Definition(
            "group.max.session.timeout.ms",
            "The maximum allowed session timeout for registered consumers. Longer timeouts give consumers more time to process messages in between heartbeats at the cost of a longer time to detect failures.",
            "int"
        ),
        "group.max.size" to Definition(
            "group.max.size",
            "The maximum number of consumers that a single consumer group can accommodate.",
            "int"
        ),
        "group.min.session.timeout.ms" to Definition(
            "group.min.session.timeout.ms",
            "The minimum allowed session timeout for registered consumers. Shorter timeouts result in quicker failure detection at the cost of more frequent consumer heartbeating, which can overwhelm broker resources.",
            "int"
        ),
        "initial.broker.registration.timeout.ms" to Definition(
            "initial.broker.registration.timeout.ms",
            "When initially registering with the controller quorum, the number of milliseconds to wait before declaring failure and exiting the broker process.",
            "int"
        ),
        "inter.broker.listener.name" to Definition(
            "inter.broker.listener.name",
            "Name of listener used for communication between brokers. If this is unset, the listener name is defined by security.inter.broker.protocol. It is an error to set this and security.inter.broker.protocol properties at the same time.",
            "string"
        ),
        "inter.broker.protocol.version" to Definition(
            "inter.broker.protocol.version",
            "Specify which version of the inter-broker protocol will be used.<br> This is typically bumped after all brokers were upgraded to a new version.<br> Example of some valid values are: 0.8.0, 0.8.1, 0.8.1.1, 0.8.2, 0.8.2.0, 0.8.2.1, 0.9.0.0, 0.9.0.1 Check MetadataVersion for the full list.",
            "string"
        ),
        "log.cleaner.backoff.ms" to Definition(
            "log.cleaner.backoff.ms",
            "The amount of time to sleep when there are no logs to clean",
            "long"
        ),
        "log.cleaner.dedupe.buffer.size" to Definition(
            "log.cleaner.dedupe.buffer.size",
            "The total memory used for log deduplication across all cleaner threads",
            "long"
        ),
        "log.cleaner.delete.retention.ms" to Definition(
            "log.cleaner.delete.retention.ms",
            "The amount of time to retain delete tombstone markers for log compacted topics. This setting also gives a bound on the time in which a consumer must complete a read if they begin from offset 0 to ensure that they get a valid snapshot of the final stage (otherwise delete tombstones may be collected before they complete their scan).",
            "long"
        ),
        "log.cleaner.enable" to Definition(
            "log.cleaner.enable",
            "Enable the log cleaner process to run on the server. Should be enabled if using any topics with a cleanup.policy=compact including the internal offsets topic. If disabled those topics will not be compacted and continually grow in size.",
            "boolean"
        ),
        "log.cleaner.io.buffer.load.factor" to Definition(
            "log.cleaner.io.buffer.load.factor",
            "Log cleaner dedupe buffer load factor. The percentage full the dedupe buffer can become. A higher value will allow more log to be cleaned at once but will lead to more hash collisions",
            "double"
        ),
        "log.cleaner.io.buffer.size" to Definition(
            "log.cleaner.io.buffer.size",
            "The total memory used for log cleaner I/O buffers across all cleaner threads",
            "int"
        ),
        "log.cleaner.io.max.bytes.per.second" to Definition(
            "log.cleaner.io.max.bytes.per.second",
            "The log cleaner will be throttled so that the sum of its read and write i/o will be less than this value on average",
            "double"
        ),
        "log.cleaner.max.compaction.lag.ms" to Definition(
            "log.cleaner.max.compaction.lag.ms",
            "The maximum time a message will remain ineligible for compaction in the log. Only applicable for logs that are being compacted.",
            "long"
        ),
        "log.cleaner.min.cleanable.ratio" to Definition(
            "log.cleaner.min.cleanable.ratio",
            "The minimum ratio of dirty log to total log for a log to eligible for cleaning. If the log.cleaner.max.compaction.lag.ms or the log.cleaner.min.compaction.lag.ms configurations are also specified, then the log compactor considers the log eligible for compaction as soon as either: (i) the dirty ratio threshold has been met and the log has had dirty (uncompacted) records for at least the log.cleaner.min.compaction.lag.ms duration, or (ii) if the log has had dirty (uncompacted) records for at most the log.cleaner.max.compaction.lag.ms period.",
            "double"
        ),
        "log.cleaner.min.compaction.lag.ms" to Definition(
            "log.cleaner.min.compaction.lag.ms",
            "The minimum time a message will remain uncompacted in the log. Only applicable for logs that are being compacted.",
            "long"
        ),
        "log.cleaner.threads" to Definition(
            "log.cleaner.threads",
            "The number of background threads to use for log cleaning",
            "int"
        ),
        "log.cleanup.policy" to Definition(
            "log.cleanup.policy",
            "The default cleanup policy for segments beyond the retention window. A comma separated list of valid policies. Valid policies are: \"delete\" and \"compact\"",
            "list"
        ),
        "log.index.interval.bytes" to Definition(
            "log.index.interval.bytes",
            "The interval with which we add an entry to the offset index",
            "int"
        ),
        "log.index.size.max.bytes" to Definition(
            "log.index.size.max.bytes",
            "The maximum size in bytes of the offset index",
            "int"
        ),
        "log.message.format.version" to Definition(
            "log.message.format.version",
            "Specify the message format version the broker will use to append messages to the logs. The value should be a valid MetadataVersion. Some examples are: 0.8.2, 0.9.0.0, 0.10.0, check MetadataVersion for more details. By setting a particular message format version, the user is certifying that all the existing messages on disk are smaller or equal than the specified version. Setting this value incorrectly will cause consumers with older versions to break as they will receive messages with a format that they don't understand.",
            "string"
        ),
        "log.message.timestamp.difference.max.ms" to Definition(
            "log.message.timestamp.difference.max.ms",
            "The maximum difference allowed between the timestamp when a broker receives a message and the timestamp specified in the message. If log.message.timestamp.type=CreateTime, a message will be rejected if the difference in timestamp exceeds this threshold. This configuration is ignored if log.message.timestamp.type=LogAppendTime.The maximum timestamp difference allowed should be no greater than log.retention.ms to avoid unnecessarily frequent log rolling.",
            "long"
        ),
        "log.message.timestamp.type" to Definition(
            "log.message.timestamp.type",
            "Define whether the timestamp in the message is message create time or log append time. The value should be either `CreateTime` or `LogAppendTime`",
            "string"
        ),
        "log.preallocate" to Definition(
            "log.preallocate",
            "Should pre allocate file when create new segment? If you are using Kafka on Windows, you probably need to set it to true.",
            "boolean"
        ),
        "log.retention.check.interval.ms" to Definition(
            "log.retention.check.interval.ms",
            "The frequency in milliseconds that the log cleaner checks whether any log is eligible for deletion",
            "long"
        ),
        "max.connection.creation.rate" to Definition(
            "max.connection.creation.rate",
            "The maximum connection creation rate we allow in the broker at any time. Listener-level limits may also be configured by prefixing the config name with the listener prefix, for example, <code>listener.name.internal.max.connection.creation.rate</code>.Broker-wide connection rate limit should be configured based on broker capacity while listener limits should be configured based on application requirements. New connections will be throttled if either the listener or the broker limit is reached, with the exception of inter-broker listener. Connections on the inter-broker listener will be throttled only when the listener-level rate limit is reached.",
            "int"
        ),
        "max.connections" to Definition(
            "max.connections",
            "The maximum number of connections we allow in the broker at any time. This limit is applied in addition to any per-ip limits configured using max.connections.per.ip. Listener-level limits may also be configured by prefixing the config name with the listener prefix, for example, <code>listener.name.internal.max.connections</code>. Broker-wide limit should be configured based on broker capacity while listener limits should be configured based on application requirements. New connections are blocked if either the listener or broker limit is reached. Connections on the inter-broker listener are permitted even if broker-wide limit is reached. The least recently used connection on another listener will be closed in this case.",
            "int"
        ),
        "max.connections.per.ip" to Definition(
            "max.connections.per.ip",
            "The maximum number of connections we allow from each ip address. This can be set to 0 if there are overrides configured using max.connections.per.ip.overrides property. New connections from the ip address are dropped if the limit is reached.",
            "int"
        ),
        "max.connections.per.ip.overrides" to Definition(
            "max.connections.per.ip.overrides",
            "A comma-separated list of per-ip or hostname overrides to the default maximum number of connections. An example value is \"hostName:100,127.0.0.1:200\"",
            "string"
        ),
        "max.incremental.fetch.session.cache.slots" to Definition(
            "max.incremental.fetch.session.cache.slots",
            "The maximum number of incremental fetch sessions that we will maintain.",
            "int"
        ),
        "num.partitions" to Definition(
            "num.partitions",
            "The default number of log partitions per topic",
            "int"
        ),
        "password.encoder.old.secret" to Definition(
            "password.encoder.old.secret",
            "The old secret that was used for encoding dynamically configured passwords. This is required only when the secret is updated. If specified, all dynamically encoded passwords are decoded using this old secret and re-encoded using password.encoder.secret when broker starts up.",
            "password"
        ),
        "password.encoder.secret" to Definition(
            "password.encoder.secret",
            "The secret used for encoding dynamically configured passwords for this broker.",
            "password"
        ),
        "principal.builder.class" to Definition(
            "principal.builder.class",
            "The fully qualified name of a class that implements the KafkaPrincipalBuilder interface, which is used to build the KafkaPrincipal object used during authorization. If no principal builder is defined, the default behavior depends on the security protocol in use. For SSL authentication,  the principal will be derived using the rules defined by <code>ssl.principal.mapping.rules</code> applied on the distinguished name from the client certificate if one is provided; otherwise, if client authentication is not required, the principal name will be ANONYMOUS. For SASL authentication, the principal will be derived using the rules defined by <code>sasl.kerberos.principal.to.local.rules</code> if GSSAPI is in use, and the SASL authentication ID for other mechanisms. For PLAINTEXT, the principal will be ANONYMOUS.",
            "class"
        ),
        "producer.purgatory.purge.interval.requests" to Definition(
            "producer.purgatory.purge.interval.requests",
            "The purge interval (in number of requests) of the producer request purgatory",
            "int"
        ),
        "queued.max.request.bytes" to Definition(
            "queued.max.request.bytes",
            "The number of queued bytes allowed before no more requests are read",
            "long"
        ),
        "replica.fetch.backoff.ms" to Definition(
            "replica.fetch.backoff.ms",
            "The amount of time to sleep when fetch partition error occurs.",
            "int"
        ),
        "replica.fetch.max.bytes" to Definition(
            "replica.fetch.max.bytes",
            "The number of bytes of messages to attempt to fetch for each partition. This is not an absolute maximum, if the first record batch in the first non-empty partition of the fetch is larger than this value, the record batch will still be returned to ensure that progress can be made. The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or <code>max.message.bytes</code> (topic config).",
            "int"
        ),
        "replica.fetch.response.max.bytes" to Definition(
            "replica.fetch.response.max.bytes",
            "Maximum bytes expected for the entire fetch response. Records are fetched in batches, and if the first record batch in the first non-empty partition of the fetch is larger than this value, the record batch will still be returned to ensure that progress can be made. As such, this is not an absolute maximum. The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or <code>max.message.bytes</code> (topic config).",
            "int"
        ),
        "replica.selector.class" to Definition(
            "replica.selector.class",
            "The fully qualified class name that implements ReplicaSelector. This is used by the broker to find the preferred read replica. By default, we use an implementation that returns the leader.",
            "string"
        ),
        "reserved.broker.max.id" to Definition(
            "reserved.broker.max.id",
            "Max number that can be used for a broker.id",
            "int"
        ),
        "sasl.client.callback.handler.class" to Definition(
            "sasl.client.callback.handler.class",
            "The fully qualified name of a SASL client callback handler class that implements the AuthenticateCallbackHandler interface.",
            "class"
        ),
        "sasl.enabled.mechanisms" to Definition(
            "sasl.enabled.mechanisms",
            "The list of SASL mechanisms enabled in the Kafka server. The list may contain any mechanism for which a security provider is available. Only GSSAPI is enabled by default.",
            "list"
        ),
        "sasl.jaas.config" to Definition(
            "sasl.jaas.config",
            "JAAS login context parameters for SASL connections in the format used by JAAS configuration files. The format for the value is: <code>loginModuleClass controlFlag (optionName=optionValue)*;</code>. For brokers, the config must be prefixed with listener prefix and SASL mechanism name in lower-case. For example, listener.name.sasl_ssl.scram-sha-256.sasl.jaas.config=com.example.ScramLoginModule required;",
            "password"
        ),
        "sasl.kerberos.kinit.cmd" to Definition(
            "sasl.kerberos.kinit.cmd",
            "Kerberos kinit command path.",
            "string"
        ),
        "sasl.kerberos.min.time.before.relogin" to Definition(
            "sasl.kerberos.min.time.before.relogin",
            "Login thread sleep time between refresh attempts.",
            "long"
        ),
        "sasl.kerberos.principal.to.local.rules" to Definition(
            "sasl.kerberos.principal.to.local.rules",
            "A list of rules for mapping from principal names to short names (typically operating system usernames). The rules are evaluated in order and the first rule that matches a principal name is used to map it to a short name. Any later rules in the list are ignored. By default, principal names of the form {username}/{hostname}@{REALM} are mapped to {username}. For more details on the format please see security authorization and acls. Note that this configuration is ignored if an extension of KafkaPrincipalBuilder is provided by the <code>principal.builder.class</code> configuration.",
            "list"
        ),
        "sasl.kerberos.service.name" to Definition(
            "sasl.kerberos.service.name",
            "The Kerberos principal name that Kafka runs as. This can be defined either in Kafka's JAAS config or in Kafka's config.",
            "string"
        ),
        "sasl.kerberos.ticket.renew.jitter" to Definition(
            "sasl.kerberos.ticket.renew.jitter",
            "Percentage of random jitter added to the renewal time.",
            "double"
        ),
        "sasl.kerberos.ticket.renew.window.factor" to Definition(
            "sasl.kerberos.ticket.renew.window.factor",
            "Login thread will sleep until the specified window factor of time from last refresh to ticket's expiry has been reached, at which time it will try to renew the ticket.",
            "double"
        ),
        "sasl.login.callback.handler.class" to Definition(
            "sasl.login.callback.handler.class",
            "The fully qualified name of a SASL login callback handler class that implements the AuthenticateCallbackHandler interface. For brokers, login callback handler config must be prefixed with listener prefix and SASL mechanism name in lower-case. For example, listener.name.sasl_ssl.scram-sha-256.sasl.login.callback.handler.class=com.example.CustomScramLoginCallbackHandler",
            "class"
        ),
        "sasl.login.class" to Definition(
            "sasl.login.class",
            "The fully qualified name of a class that implements the Login interface. For brokers, login config must be prefixed with listener prefix and SASL mechanism name in lower-case. For example, listener.name.sasl_ssl.scram-sha-256.sasl.login.class=com.example.CustomScramLogin",
            "class"
        ),
        "sasl.login.refresh.buffer.seconds" to Definition(
            "sasl.login.refresh.buffer.seconds",
            "The amount of buffer time before credential expiration to maintain when refreshing a credential, in seconds. If a refresh would otherwise occur closer to expiration than the number of buffer seconds then the refresh will be moved up to maintain as much of the buffer time as possible. Legal values are between 0 and 3600 (1 hour); a default value of  300 (5 minutes) is used if no value is specified. This value and sasl.login.refresh.min.period.seconds are both ignored if their sum exceeds the remaining lifetime of a credential. Currently applies only to OAUTHBEARER.",
            "short"
        ),
        "sasl.login.refresh.min.period.seconds" to Definition(
            "sasl.login.refresh.min.period.seconds",
            "The desired minimum time for the login refresh thread to wait before refreshing a credential, in seconds. Legal values are between 0 and 900 (15 minutes); a default value of 60 (1 minute) is used if no value is specified.  This value and  sasl.login.refresh.buffer.seconds are both ignored if their sum exceeds the remaining lifetime of a credential. Currently applies only to OAUTHBEARER.",
            "short"
        ),
        "sasl.login.refresh.window.factor" to Definition(
            "sasl.login.refresh.window.factor",
            "Login refresh thread will sleep until the specified window factor relative to the credential's lifetime has been reached, at which time it will try to refresh the credential. Legal values are between 0.5 (50%) and 1.0 (100%) inclusive; a default value of 0.8 (80%) is used if no value is specified. Currently applies only to OAUTHBEARER.",
            "double"
        ),
        "sasl.login.refresh.window.jitter" to Definition(
            "sasl.login.refresh.window.jitter",
            "The maximum amount of random jitter relative to the credential's lifetime that is added to the login refresh thread's sleep time. Legal values are between 0 and 0.25 (25%) inclusive; a default value of 0.05 (5%) is used if no value is specified. Currently applies only to OAUTHBEARER.",
            "double"
        ),
        "sasl.mechanism.inter.broker.protocol" to Definition(
            "sasl.mechanism.inter.broker.protocol",
            "SASL mechanism used for inter-broker communication. Default is GSSAPI.",
            "string"
        ),
        "sasl.oauthbearer.jwks.endpoint.url" to Definition(
            "sasl.oauthbearer.jwks.endpoint.url",
            "The OAuth/OIDC provider URL from which the provider's JWKS (JSON Web Key Set) can be retrieved. The URL can be HTTP(S)-based or file-based. If the URL is HTTP(S)-based, the JWKS data will be retrieved from the OAuth/OIDC provider via the configured URL on broker startup. All then-current keys will be cached on the broker for incoming requests. If an authentication request is received for a JWT that includes a \"kid\" header claim value that isn't yet in the cache, the JWKS endpoint will be queried again on demand. However, the broker polls the URL every sasl.oauthbearer.jwks.endpoint.refresh.ms milliseconds to refresh the cache with any forthcoming keys before any JWT requests that include them are received. If the URL is file-based, the broker will load the JWKS file from a configured location on startup. In the event that the JWT includes a \"kid\" header value that isn't in the JWKS file, the broker will reject the JWT and authentication will fail.",
            "string"
        ),
        "sasl.oauthbearer.token.endpoint.url" to Definition(
            "sasl.oauthbearer.token.endpoint.url",
            "The URL for the OAuth/OIDC identity provider. If the URL is HTTP(S)-based, it is the issuer's token endpoint URL to which requests will be made to login based on the configuration in sasl.jaas.config. If the URL is file-based, it specifies a file containing an access token (in JWT serialized form) issued by the OAuth/OIDC identity provider to use for authorization.",
            "string"
        ),
        "sasl.server.callback.handler.class" to Definition(
            "sasl.server.callback.handler.class",
            "The fully qualified name of a SASL server callback handler class that implements the AuthenticateCallbackHandler interface. Server callback handlers must be prefixed with listener prefix and SASL mechanism name in lower-case. For example, listener.name.sasl_ssl.plain.sasl.server.callback.handler.class=com.example.CustomPlainCallbackHandler.",
            "class"
        ),
        "sasl.server.max.receive.size" to Definition(
            "sasl.server.max.receive.size",
            "The maximum receive size allowed before and during initial SASL authentication. Default receive size is 512KB. GSSAPI limits requests to 64K, but we allow upto 512KB by default for custom SASL mechanisms. In practice, PLAIN, SCRAM and OAUTH mechanisms can use much smaller limits.",
            "int"
        ),
        "security.inter.broker.protocol" to Definition(
            "security.inter.broker.protocol",
            "Security protocol used to communicate between brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL. It is an error to set this and inter.broker.listener.name properties at the same time.",
            "string"
        ),
        "socket.connection.setup.timeout.max.ms" to Definition(
            "socket.connection.setup.timeout.max.ms",
            "The maximum amount of time the client will wait for the socket connection to be established. The connection setup timeout will increase exponentially for each consecutive connection failure up to this maximum. To avoid connection storms, a randomization factor of 0.2 will be applied to the timeout resulting in a random range between 20% below and 20% above the computed value.",
            "long"
        ),
        "socket.connection.setup.timeout.ms" to Definition(
            "socket.connection.setup.timeout.ms",
            "The amount of time the client will wait for the socket connection to be established. If the connection is not built before the timeout elapses, clients will close the socket channel.",
            "long"
        ),
        "socket.listen.backlog.size" to Definition(
            "socket.listen.backlog.size",
            "The maximum number of pending connections on the socket. In Linux, you may also need to configure `somaxconn` and `tcp_max_syn_backlog` kernel parameters accordingly to make the configuration takes effect.",
            "int"
        ),
        "ssl.cipher.suites" to Definition(
            "ssl.cipher.suites",
            "A list of cipher suites. This is a named combination of authentication, encryption, MAC and key exchange algorithm used to negotiate the security settings for a network connection using TLS or SSL network protocol. By default all the available cipher suites are supported.",
            "list"
        ),
        "ssl.client.auth" to Definition(
            "ssl.client.auth",
            "Configures kafka broker to request client authentication. The following settings are common:  ",
            "string"
        ),
        "ssl.enabled.protocols" to Definition(
            "ssl.enabled.protocols",
            "The list of protocols enabled for SSL connections. The default is 'TLSv1.2,TLSv1.3' when running with Java 11 or newer, 'TLSv1.2' otherwise. With the default value for Java 11, clients and servers will prefer TLSv1.3 if both support it and fallback to TLSv1.2 otherwise (assuming both support at least TLSv1.2). This default should be fine for most cases. Also see the config documentation for `ssl.protocol`.",
            "list"
        ),
        "ssl.key.password" to Definition(
            "ssl.key.password",
            "The password of the private key in the key store file or the PEM key specified in `ssl.keystore.key'.",
            "password"
        ),
        "ssl.keymanager.algorithm" to Definition(
            "ssl.keymanager.algorithm",
            "The algorithm used by key manager factory for SSL connections. Default value is the key manager factory algorithm configured for the Java Virtual Machine.",
            "string"
        ),
        "ssl.keystore.certificate.chain" to Definition(
            "ssl.keystore.certificate.chain",
            "Certificate chain in the format specified by 'ssl.keystore.type'. Default SSL engine factory supports only PEM format with a list of X.509 certificates",
            "password"
        ),
        "ssl.keystore.key" to Definition(
            "ssl.keystore.key",
            "Private key in the format specified by 'ssl.keystore.type'. Default SSL engine factory supports only PEM format with PKCS#8 keys. If the key is encrypted, key password must be specified using 'ssl.key.password'",
            "password"
        ),
        "ssl.keystore.location" to Definition(
            "ssl.keystore.location",
            "The location of the key store file. This is optional for client and can be used for two-way authentication for client.",
            "string"
        ),
        "ssl.keystore.password" to Definition(
            "ssl.keystore.password",
            "The store password for the key store file. This is optional for client and only needed if 'ssl.keystore.location' is configured. Key store password is not supported for PEM format.",
            "password"
        ),
        "ssl.keystore.type" to Definition(
            "ssl.keystore.type",
            "The file format of the key store file. This is optional for client. The values currently supported by the default `ssl.engine.factory.class` are [JKS, PKCS12, PEM].",
            "string"
        ),
        "ssl.protocol" to Definition(
            "ssl.protocol",
            "The SSL protocol used to generate the SSLContext. The default is 'TLSv1.3' when running with Java 11 or newer, 'TLSv1.2' otherwise. This value should be fine for most use cases. Allowed values in recent JVMs are 'TLSv1.2' and 'TLSv1.3'. 'TLS', 'TLSv1.1', 'SSL', 'SSLv2' and 'SSLv3' may be supported in older JVMs, but their usage is discouraged due to known security vulnerabilities. With the default value for this config and 'ssl.enabled.protocols', clients will downgrade to 'TLSv1.2' if the server does not support 'TLSv1.3'. If this config is set to 'TLSv1.2', clients will not use 'TLSv1.3' even if it is one of the values in ssl.enabled.protocols and the server only supports 'TLSv1.3'.",
            "string"
        ),
        "ssl.provider" to Definition(
            "ssl.provider",
            "The name of the security provider used for SSL connections. Default value is the default security provider of the JVM.",
            "string"
        ),
        "ssl.trustmanager.algorithm" to Definition(
            "ssl.trustmanager.algorithm",
            "The algorithm used by trust manager factory for SSL connections. Default value is the trust manager factory algorithm configured for the Java Virtual Machine.",
            "string"
        ),
        "ssl.truststore.certificates" to Definition(
            "ssl.truststore.certificates",
            "Trusted certificates in the format specified by 'ssl.truststore.type'. Default SSL engine factory supports only PEM format with X.509 certificates.",
            "password"
        ),
        "ssl.truststore.location" to Definition(
            "ssl.truststore.location",
            "The location of the trust store file.",
            "string"
        ),
        "ssl.truststore.password" to Definition(
            "ssl.truststore.password",
            "The password for the trust store file. If a password is not set, trust store file configured will still be used, but integrity checking is disabled. Trust store password is not supported for PEM format.",
            "password"
        ),
        "ssl.truststore.type" to Definition(
            "ssl.truststore.type",
            "The file format of the trust store file. The values currently supported by the default `ssl.engine.factory.class` are [JKS, PKCS12, PEM].",
            "string"
        ),
        "zookeeper.clientCnxnSocket" to Definition(
            "zookeeper.clientCnxnSocket",
            "Typically set to <code>org.apache.zookeeper.ClientCnxnSocketNetty</code> when using TLS connectivity to ZooKeeper. Overrides any explicit value set via the same-named <code>zookeeper.clientCnxnSocket</code> system property.",
            "string"
        ),
        "zookeeper.ssl.client.enable" to Definition(
            "zookeeper.ssl.client.enable",
            "Set client to use TLS when connecting to ZooKeeper. An explicit value overrides any value set via the <code>zookeeper.client.secure</code> system property (note the different name). Defaults to false if neither is set; when true, <code>zookeeper.clientCnxnSocket</code> must be set (typically to <code>org.apache.zookeeper.ClientCnxnSocketNetty</code>); other values to set may include <code>zookeeper.ssl.cipher.suites</code>, <code>zookeeper.ssl.crl.enable</code>, <code>zookeeper.ssl.enabled.protocols</code>, <code>zookeeper.ssl.endpoint.identification.algorithm</code>, <code>zookeeper.ssl.keystore.location</code>, <code>zookeeper.ssl.keystore.password</code>, <code>zookeeper.ssl.keystore.type</code>, <code>zookeeper.ssl.ocsp.enable</code>, <code>zookeeper.ssl.protocol</code>, <code>zookeeper.ssl.truststore.location</code>, <code>zookeeper.ssl.truststore.password</code>, <code>zookeeper.ssl.truststore.type</code>",
            "boolean"
        ),
        "zookeeper.ssl.keystore.location" to Definition(
            "zookeeper.ssl.keystore.location",
            "Keystore location when using a client-side certificate with TLS connectivity to ZooKeeper. Overrides any explicit value set via the <code>zookeeper.ssl.keyStore.location</code> system property (note the camelCase).",
            "string"
        ),
        "zookeeper.ssl.keystore.password" to Definition(
            "zookeeper.ssl.keystore.password",
            "Keystore password when using a client-side certificate with TLS connectivity to ZooKeeper. Overrides any explicit value set via the <code>zookeeper.ssl.keyStore.password</code> system property (note the camelCase). Note that ZooKeeper does not support a key password different from the keystore password, so be sure to set the key password in the keystore to be identical to the keystore password; otherwise the connection attempt to Zookeeper will fail.",
            "password"
        ),
        "zookeeper.ssl.keystore.type" to Definition(
            "zookeeper.ssl.keystore.type",
            "Keystore type when using a client-side certificate with TLS connectivity to ZooKeeper. Overrides any explicit value set via the <code>zookeeper.ssl.keyStore.type</code> system property (note the camelCase). The default value of <code>null</code> means the type will be auto-detected based on the filename extension of the keystore.",
            "string"
        ),
        "zookeeper.ssl.truststore.location" to Definition(
            "zookeeper.ssl.truststore.location",
            "Truststore location when using TLS connectivity to ZooKeeper. Overrides any explicit value set via the <code>zookeeper.ssl.trustStore.location</code> system property (note the camelCase).",
            "string"
        ),
        "zookeeper.ssl.truststore.password" to Definition(
            "zookeeper.ssl.truststore.password",
            "Truststore password when using TLS connectivity to ZooKeeper. Overrides any explicit value set via the <code>zookeeper.ssl.trustStore.password</code> system property (note the camelCase).",
            "password"
        ),
        "zookeeper.ssl.truststore.type" to Definition(
            "zookeeper.ssl.truststore.type",
            "Truststore type when using TLS connectivity to ZooKeeper. Overrides any explicit value set via the <code>zookeeper.ssl.trustStore.type</code> system property (note the camelCase). The default value of <code>null</code> means the type will be auto-detected based on the filename extension of the truststore.",
            "string"
        ),
        "alter.config.policy.class.name" to Definition(
            "alter.config.policy.class.name",
            "The alter configs policy class that should be used for validation. The class should implement the <code>org.apache.kafka.server.policy.AlterConfigPolicy</code> interface.",
            "class"
        ),
        "alter.log.dirs.replication.quota.window.num" to Definition(
            "alter.log.dirs.replication.quota.window.num",
            "The number of samples to retain in memory for alter log dirs replication quotas",
            "int"
        ),
        "alter.log.dirs.replication.quota.window.size.seconds" to Definition(
            "alter.log.dirs.replication.quota.window.size.seconds",
            "The time span of each sample for alter log dirs replication quotas",
            "int"
        ),
        "authorizer.class.name" to Definition(
            "authorizer.class.name",
            "The fully qualified name of a class that implements <code>org.apache.kafka.server.authorizer.Authorizer</code> interface, which is used by the broker for authorization.",
            "string"
        ),
        "client.quota.callback.class" to Definition(
            "client.quota.callback.class",
            "The fully qualified name of a class that implements the ClientQuotaCallback interface, which is used to determine quota limits applied to client requests. By default, the &lt;user&gt; and &lt;client-id&gt; quotas that are stored in ZooKeeper are applied. For any given request, the most specific quota that matches the user principal of the session and the client-id of the request is applied.",
            "class"
        ),
        "connection.failed.authentication.delay.ms" to Definition(
            "connection.failed.authentication.delay.ms",
            "Connection close delay on failed authentication: this is the time (in milliseconds) by which connection close will be delayed on authentication failure. This must be configured to be less than connections.max.idle.ms to prevent connection timeout.",
            "int"
        ),
        "controller.quorum.retry.backoff.ms" to Definition(
            "controller.quorum.retry.backoff.ms",
            "The amount of time to wait before attempting to retry a failed request to a given topic partition. This avoids repeatedly sending requests in a tight loop under some failure scenarios.",
            "int"
        ),
        "controller.quota.window.num" to Definition(
            "controller.quota.window.num",
            "The number of samples to retain in memory for controller mutation quotas",
            "int"
        ),
        "controller.quota.window.size.seconds" to Definition(
            "controller.quota.window.size.seconds",
            "The time span of each sample for controller mutations quotas",
            "int"
        ),
        "create.topic.policy.class.name" to Definition(
            "create.topic.policy.class.name",
            "The create topic policy class that should be used for validation. The class should implement the <code>org.apache.kafka.server.policy.CreateTopicPolicy</code> interface.",
            "class"
        ),
        "delegation.token.expiry.check.interval.ms" to Definition(
            "delegation.token.expiry.check.interval.ms",
            "Scan interval to remove expired delegation tokens.",
            "long"
        ),
        "kafka.metrics.polling.interval.secs" to Definition(
            "kafka.metrics.polling.interval.secs",
            "The metrics polling interval (in seconds) which can be used in kafka.metrics.reporters implementations.",
            "int"
        ),
        "kafka.metrics.reporters" to Definition(
            "kafka.metrics.reporters",
            "A list of classes to use as Yammer metrics custom reporters. The reporters should implement <code>kafka.metrics.KafkaMetricsReporter</code> trait. If a client wants to expose JMX operations on a custom reporter, the custom reporter needs to additionally implement an MBean trait that extends <code>kafka.metrics.KafkaMetricsReporterMBean</code> trait so that the registered MBean is compliant with the standard MBean convention.",
            "list"
        ),
        "listener.security.protocol.map" to Definition(
            "listener.security.protocol.map",
            "Map between listener names and security protocols. This must be defined for the same security protocol to be usable in more than one port or IP. For example, internal and external traffic can be separated even if SSL is required for both. Concretely, the user could define listeners with names INTERNAL and EXTERNAL and this property as: `INTERNAL:SSL,EXTERNAL:SSL`. As shown, key and value are separated by a colon and map entries are separated by commas. Each listener name should only appear once in the map. Different security (SSL and SASL) settings can be configured for each listener by adding a normalised prefix (the listener name is lowercased) to the config name. For example, to set a different keystore for the INTERNAL listener, a config with name <code>listener.name.internal.ssl.keystore.location</code> would be set. If the config for the listener name is not set, the config will fallback to the generic config (i.e. <code>ssl.keystore.location</code>). Note that in KRaft a default mapping from the listener names defined by <code>controller.listener.names</code> to PLAINTEXT is assumed if no explicit mapping is provided and no other security protocol is in use.",
            "string"
        ),
        "log.message.downconversion.enable" to Definition(
            "log.message.downconversion.enable",
            "This configuration controls whether down-conversion of message formats is enabled to satisfy consume requests. When set to <code>false</code>, broker will not perform down-conversion for consumers expecting an older message format. The broker responds with <code>UNSUPPORTED_VERSION</code> error for consume requests from such older clients. This configurationdoes not apply to any message format conversion that might be required for replication to followers.",
            "boolean"
        ),
        "metadata.max.idle.interval.ms" to Definition(
            "metadata.max.idle.interval.ms",
            "This configuration controls how often the active controller should write no-op records to the metadata partition. If the value is 0, no-op records are not appended to the metadata partition. The default value is 500",
            "int"
        ),
        "metric.reporters" to Definition(
            "metric.reporters",
            "A list of classes to use as metrics reporters. Implementing the <code>org.apache.kafka.common.metrics.MetricsReporter</code> interface allows plugging in classes that will be notified of new metric creation. The JmxReporter is always included to register JMX statistics.",
            "list"
        ),
        "metrics.num.samples" to Definition(
            "metrics.num.samples",
            "The number of samples maintained to compute metrics.",
            "int"
        ),
        "metrics.recording.level" to Definition(
            "metrics.recording.level",
            "The highest recording level for metrics.",
            "string"
        ),
        "metrics.sample.window.ms" to Definition(
            "metrics.sample.window.ms",
            "The window of time a metrics sample is computed over.",
            "long"
        ),
        "password.encoder.cipher.algorithm" to Definition(
            "password.encoder.cipher.algorithm",
            "The Cipher algorithm used for encoding dynamically configured passwords.",
            "string"
        ),
        "password.encoder.iterations" to Definition(
            "password.encoder.iterations",
            "The iteration count used for encoding dynamically configured passwords.",
            "int"
        ),
        "password.encoder.key.length" to Definition(
            "password.encoder.key.length",
            "The key length used for encoding dynamically configured passwords.",
            "int"
        ),
        "password.encoder.keyfactory.algorithm" to Definition(
            "password.encoder.keyfactory.algorithm",
            "The SecretKeyFactory algorithm used for encoding dynamically configured passwords. Default is PBKDF2WithHmacSHA512 if available and PBKDF2WithHmacSHA1 otherwise.",
            "string"
        ),
        "quota.window.num" to Definition(
            "quota.window.num",
            "The number of samples to retain in memory for client quotas",
            "int"
        ),
        "quota.window.size.seconds" to Definition(
            "quota.window.size.seconds",
            "The time span of each sample for client quotas",
            "int"
        ),
        "replication.quota.window.num" to Definition(
            "replication.quota.window.num",
            "The number of samples to retain in memory for replication quotas",
            "int"
        ),
        "replication.quota.window.size.seconds" to Definition(
            "replication.quota.window.size.seconds",
            "The time span of each sample for replication quotas",
            "int"
        ),
        "sasl.login.connect.timeout.ms" to Definition(
            "sasl.login.connect.timeout.ms",
            "The (optional) value in milliseconds for the external authentication provider connection timeout. Currently applies only to OAUTHBEARER.",
            "int"
        ),
        "sasl.login.read.timeout.ms" to Definition(
            "sasl.login.read.timeout.ms",
            "The (optional) value in milliseconds for the external authentication provider read timeout. Currently applies only to OAUTHBEARER.",
            "int"
        ),
        "sasl.login.retry.backoff.max.ms" to Definition(
            "sasl.login.retry.backoff.max.ms",
            "The (optional) value in milliseconds for the maximum wait between login attempts to the external authentication provider. Login uses an exponential backoff algorithm with an initial wait based on the sasl.login.retry.backoff.ms setting and will double in wait length between attempts up to a maximum wait length specified by the sasl.login.retry.backoff.max.ms setting. Currently applies only to OAUTHBEARER.",
            "long"
        ),
        "sasl.login.retry.backoff.ms" to Definition(
            "sasl.login.retry.backoff.ms",
            "The (optional) value in milliseconds for the initial wait between login attempts to the external authentication provider. Login uses an exponential backoff algorithm with an initial wait based on the sasl.login.retry.backoff.ms setting and will double in wait length between attempts up to a maximum wait length specified by the sasl.login.retry.backoff.max.ms setting. Currently applies only to OAUTHBEARER.",
            "long"
        ),
        "sasl.oauthbearer.clock.skew.seconds" to Definition(
            "sasl.oauthbearer.clock.skew.seconds",
            "The (optional) value in seconds to allow for differences between the time of the OAuth/OIDC identity provider and the broker.",
            "int"
        ),
        "sasl.oauthbearer.expected.audience" to Definition(
            "sasl.oauthbearer.expected.audience",
            "The (optional) comma-delimited setting for the broker to use to verify that the JWT was issued for one of the expected audiences. The JWT will be inspected for the standard OAuth \"aud\" claim and if this value is set, the broker will match the value from JWT's \"aud\" claim  to see if there is an exact match. If there is no match, the broker will reject the JWT and authentication will fail.",
            "list"
        ),
        "sasl.oauthbearer.expected.issuer" to Definition(
            "sasl.oauthbearer.expected.issuer",
            "The (optional) setting for the broker to use to verify that the JWT was created by the expected issuer. The JWT will be inspected for the standard OAuth \"iss\" claim and if this value is set, the broker will match it exactly against what is in the JWT's \"iss\" claim. If there is no match, the broker will reject the JWT and authentication will fail.",
            "string"
        ),
        "sasl.oauthbearer.jwks.endpoint.refresh.ms" to Definition(
            "sasl.oauthbearer.jwks.endpoint.refresh.ms",
            "The (optional) value in milliseconds for the broker to wait between refreshing its JWKS (JSON Web Key Set) cache that contains the keys to verify the signature of the JWT.",
            "long"
        ),
        "sasl.oauthbearer.jwks.endpoint.retry.backoff.max.ms" to Definition(
            "sasl.oauthbearer.jwks.endpoint.retry.backoff.max.ms",
            "The (optional) value in milliseconds for the maximum wait between attempts to retrieve the JWKS (JSON Web Key Set) from the external authentication provider. JWKS retrieval uses an exponential backoff algorithm with an initial wait based on the sasl.oauthbearer.jwks.endpoint.retry.backoff.ms setting and will double in wait length between attempts up to a maximum wait length specified by the sasl.oauthbearer.jwks.endpoint.retry.backoff.max.ms setting.",
            "long"
        ),
        "sasl.oauthbearer.jwks.endpoint.retry.backoff.ms" to Definition(
            "sasl.oauthbearer.jwks.endpoint.retry.backoff.ms",
            "The (optional) value in milliseconds for the initial wait between JWKS (JSON Web Key Set) retrieval attempts from the external authentication provider. JWKS retrieval uses an exponential backoff algorithm with an initial wait based on the sasl.oauthbearer.jwks.endpoint.retry.backoff.ms setting and will double in wait length between attempts up to a maximum wait length specified by the sasl.oauthbearer.jwks.endpoint.retry.backoff.max.ms setting.",
            "long"
        ),
        "sasl.oauthbearer.scope.claim.name" to Definition(
            "sasl.oauthbearer.scope.claim.name",
            "The OAuth claim for the scope is often named \"scope\", but this (optional) setting can provide a different name to use for the scope included in the JWT payload's claims if the OAuth/OIDC provider uses a different name for that claim.",
            "string"
        ),
        "sasl.oauthbearer.sub.claim.name" to Definition(
            "sasl.oauthbearer.sub.claim.name",
            "The OAuth claim for the subject is often named \"sub\", but this (optional) setting can provide a different name to use for the subject included in the JWT payload's claims if the OAuth/OIDC provider uses a different name for that claim.",
            "string"
        ),
        "security.providers" to Definition(
            "security.providers",
            "A list of configurable creator classes each returning a provider implementing security algorithms. These classes should implement the <code>org.apache.kafka.common.security.auth.SecurityProviderCreator</code> interface.",
            "string"
        ),
        "ssl.endpoint.identification.algorithm" to Definition(
            "ssl.endpoint.identification.algorithm",
            "The endpoint identification algorithm to validate server hostname using server certificate. ",
            "string"
        ),
        "ssl.engine.factory.class" to Definition(
            "ssl.engine.factory.class",
            "The class of type org.apache.kafka.common.security.auth.SslEngineFactory to provide SSLEngine objects. Default value is org.apache.kafka.common.security.ssl.DefaultSslEngineFactory",
            "class"
        ),
        "ssl.principal.mapping.rules" to Definition(
            "ssl.principal.mapping.rules",
            "A list of rules for mapping from distinguished name from the client certificate to short name. The rules are evaluated in order and the first rule that matches a principal name is used to map it to a short name. Any later rules in the list are ignored. By default, distinguished name of the X.500 certificate will be the principal. For more details on the format please see security authorization and acls. Note that this configuration is ignored if an extension of KafkaPrincipalBuilder is provided by the <code>principal.builder.class</code> configuration.",
            "string"
        ),
        "ssl.secure.random.implementation" to Definition(
            "ssl.secure.random.implementation",
            "The SecureRandom PRNG implementation to use for SSL cryptography operations. ",
            "string"
        ),
        "transaction.abort.timed.out.transaction.cleanup.interval.ms" to Definition(
            "transaction.abort.timed.out.transaction.cleanup.interval.ms",
            "The interval at which to rollback transactions that have timed out",
            "int"
        ),
        "transaction.remove.expired.transaction.cleanup.interval.ms" to Definition(
            "transaction.remove.expired.transaction.cleanup.interval.ms",
            "The interval at which to remove transactions that have expired due to <code>transactional.id.expiration.ms</code> passing",
            "int"
        ),
        "zookeeper.ssl.cipher.suites" to Definition(
            "zookeeper.ssl.cipher.suites",
            "Specifies the enabled cipher suites to be used in ZooKeeper TLS negotiation (csv). Overrides any explicit value set via the <code>zookeeper.ssl.ciphersuites</code> system property (note the single word \"ciphersuites\"). The default value of <code>null</code> means the list of enabled cipher suites is determined by the Java runtime being used.",
            "list"
        ),
        "zookeeper.ssl.crl.enable" to Definition(
            "zookeeper.ssl.crl.enable",
            "Specifies whether to enable Certificate Revocation List in the ZooKeeper TLS protocols. Overrides any explicit value set via the <code>zookeeper.ssl.crl</code> system property (note the shorter name).",
            "boolean"
        ),
        "zookeeper.ssl.enabled.protocols" to Definition(
            "zookeeper.ssl.enabled.protocols",
            "Specifies the enabled protocol(s) in ZooKeeper TLS negotiation (csv). Overrides any explicit value set via the <code>zookeeper.ssl.enabledProtocols</code> system property (note the camelCase). The default value of <code>null</code> means the enabled protocol will be the value of the <code>zookeeper.ssl.protocol</code> configuration property.",
            "list"
        ),
        "zookeeper.ssl.endpoint.identification.algorithm" to Definition(
            "zookeeper.ssl.endpoint.identification.algorithm",
            "Specifies whether to enable hostname verification in the ZooKeeper TLS negotiation process, with (case-insensitively) \"https\" meaning ZooKeeper hostname verification is enabled and an explicit blank value meaning it is disabled (disabling it is only recommended for testing purposes). An explicit value overrides any \"true\" or \"false\" value set via the <code>zookeeper.ssl.hostnameVerification</code> system property (note the different name and values; true implies https and false implies blank).",
            "string"
        ),
        "zookeeper.ssl.ocsp.enable" to Definition(
            "zookeeper.ssl.ocsp.enable",
            "Specifies whether to enable Online Certificate Status Protocol in the ZooKeeper TLS protocols. Overrides any explicit value set via the <code>zookeeper.ssl.ocsp</code> system property (note the shorter name).",
            "boolean"
        ),
        "zookeeper.ssl.protocol" to Definition(
            "zookeeper.ssl.protocol",
            "Specifies the protocol to be used in ZooKeeper TLS negotiation. An explicit value overrides any value set via the same-named <code>zookeeper.ssl.protocol</code> system property.",
            "string"
        ),
        "cleanup.policy" to Definition(
            "cleanup.policy",
            "This config designates the retention policy to use on log segments. The \"delete\" policy (which is the default) will discard old segments when their retention time or size limit has been reached. The \"compact\" policy will enable log compaction, which retains the latest value for each key. It is also possible to specify both policies in a comma-separated list (e.g. \"delete,compact\"). In this case, old segments will be discarded per the retention time and size configuration, while retained segments will be compacted.",
            "list"
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
            "This setting allows specifying an interval at which we will force an fsync of data written to the log. For example if this was set to 1 we would fsync after every message; if it were 5 we would fsync after every five messages. In general we recommend you not set this and use replication for durability and allow the operating system's background flush capabilities as it is more efficient. This setting can be overridden on a per-topic basis (see the per-topic configuration section).",
            "long"
        ),
        "flush.ms" to Definition(
            "flush.ms",
            "This setting allows specifying a time interval at which we will force an fsync of data written to the log. For example if this was set to 1000 we would fsync after 1000 ms had passed. In general we recommend you not set this and use replication for durability and allow the operating system's background flush capabilities as it is more efficient.",
            "long"
        ),
        "follower.replication.throttled.replicas" to Definition(
            "follower.replication.throttled.replicas",
            "A list of replicas for which log replication should be throttled on the follower side. The list should describe a set of replicas in the form [PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:... or alternatively the wildcard '*' can be used to throttle all replicas for this topic.",
            "list"
        ),
        "index.interval.bytes" to Definition(
            "index.interval.bytes",
            "This setting controls how frequently Kafka adds an index entry to its offset index. The default setting ensures that we index a message roughly every 4096 bytes. More indexing allows reads to jump closer to the exact position in the log but makes the index larger. You probably don't need to change this.",
            "int"
        ),
        "leader.replication.throttled.replicas" to Definition(
            "leader.replication.throttled.replicas",
            "A list of replicas for which log replication should be throttled on the leader side. The list should describe a set of replicas in the form [PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:... or alternatively the wildcard '*' can be used to throttle all replicas for this topic.",
            "list"
        ),
        "max.compaction.lag.ms" to Definition(
            "max.compaction.lag.ms",
            "The maximum time a message will remain ineligible for compaction in the log. Only applicable for logs that are being compacted.",
            "long"
        ),
        "max.message.bytes" to Definition(
            "max.message.bytes",
            "The largest record batch size allowed by Kafka (after compression if compression is enabled). If this is increased and there are consumers older than 0.10.2, the consumers' fetch size must also be increased so that they can fetch record batches this large. In the latest message format version, records are always grouped into batches for efficiency. In previous message format versions, uncompressed records are not grouped into batches and this limit only applies to a single record in that case.",
            "int"
        ),
        "message.format.version" to Definition(
            "message.format.version",
            "[DEPRECATED] Specify the message format version the broker will use to append messages to the logs. The value of this config is always assumed to be `3.0` if `inter.broker.protocol.version` is 3.0 or higher (the actual config value is ignored). Otherwise, the value should be a valid ApiVersion. Some examples are: 0.10.0, 1.1, 2.8, 3.0. By setting a particular message format version, the user is certifying that all the existing messages on disk are smaller or equal than the specified version. Setting this value incorrectly will cause consumers with older versions to break as they will receive messages with a format that they don't understand.",
            "string"
        ),
        "message.timestamp.difference.max.ms" to Definition(
            "message.timestamp.difference.max.ms",
            "The maximum difference allowed between the timestamp when a broker receives a message and the timestamp specified in the message. If message.timestamp.type=CreateTime, a message will be rejected if the difference in timestamp exceeds this threshold. This configuration is ignored if message.timestamp.type=LogAppendTime.",
            "long"
        ),
        "message.timestamp.type" to Definition(
            "message.timestamp.type",
            "Define whether the timestamp in the message is message create time or log append time. The value should be either `CreateTime` or `LogAppendTime`",
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
        "preallocate" to Definition(
            "preallocate",
            "True if we should preallocate the file on disk when creating a new log segment.",
            "boolean"
        ),
        "retention.bytes" to Definition(
            "retention.bytes",
            "This configuration controls the maximum size a partition (which consists of log segments) can grow to before we will discard old log segments to free up space if we are using the \"delete\" retention policy. By default there is no size limit only a time limit. Since this limit is enforced at the partition level, multiply it by the number of partitions to compute the topic retention in bytes.",
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
            "The maximum random jitter subtracted from the scheduled segment roll time to avoid thundering herds of segment rolling",
            "long"
        ),
        "segment.ms" to Definition(
            "segment.ms",
            "This configuration controls the period of time after which Kafka will force the log to roll even if the segment file isn't full to ensure that retention can delete or compact old data.",
            "long"
        ),
        "message.downconversion.enable" to Definition(
            "message.downconversion.enable",
            "This configuration controls whether down-conversion of message formats is enabled to satisfy consume requests. When set to <code>false</code>, broker will not perform down-conversion for consumers expecting an older message format. The broker responds with <code>UNSUPPORTED_VERSION</code> error for consume requests from such older clients. This configurationdoes not apply to any message format conversion that might be required for replication to followers.",
            "boolean"
        ),
        "key.serializer" to Definition(
            "key.serializer",
            "Serializer class for key that implements the <code>org.apache.kafka.common.serialization.Serializer</code> interface.",
            "class"
        ),
        "value.serializer" to Definition(
            "value.serializer",
            "Serializer class for value that implements the <code>org.apache.kafka.common.serialization.Serializer</code> interface.",
            "class"
        ),
        "bootstrap.servers" to Definition(
            "bootstrap.servers",
            "A list of host/port pairs to use for establishing the initial connection to the Kafka cluster. The client will make use of all servers irrespective of which servers are specified here for bootstrapping—this list only impacts the initial hosts used to discover the full set of servers. This list should be in the form <code>host1:port1,host2:port2,...</code>. Since these servers are just used for the initial connection to discover the full cluster membership (which may change dynamically), this list need not contain the full set of servers (you may want more than one, though, in case a server is down).",
            "list"
        ),
        "buffer.memory" to Definition(
            "buffer.memory",
            "The total bytes of memory the producer can use to buffer records waiting to be sent to the server. If records are sent faster than they can be delivered to the server the producer will block for <code>max.block.ms</code> after which it will throw an exception.",
            "long"
        ),
        "retries" to Definition(
            "retries",
            "Setting a value greater than zero will cause the client to resend any request that fails with a potentially transient error. It is recommended to set the value to either zero or `MAX_VALUE` and use corresponding timeout parameters to control how long a client should retry a request.",
            "int"
        ),
        "batch.size" to Definition(
            "batch.size",
            "The producer will attempt to batch records together into fewer requests whenever multiple records are being sent to the same partition. This helps performance on both the client and the server. This configuration controls the default batch size in bytes. ",
            "int"
        ),
        "client.dns.lookup" to Definition(
            "client.dns.lookup",
            "Controls how the client uses DNS lookups. If set to <code>use_all_dns_ips</code>, connect to each returned IP address in sequence until a successful connection is established. After a disconnection, the next IP is used. Once all IPs have been used once, the client resolves the IP(s) from the hostname again (both the JVM and the OS cache DNS name lookups, however). If set to <code>resolve_canonical_bootstrap_servers_only</code>, resolve each bootstrap address into a list of canonical names. After the bootstrap phase, this behaves the same as <code>use_all_dns_ips</code>.",
            "string"
        ),
        "client.id" to Definition(
            "client.id",
            "An id string to pass to the server when making requests. The purpose of this is to be able to track the source of requests beyond just ip/port by allowing a logical application name to be included in server-side request logging.",
            "string"
        ),
        "delivery.timeout.ms" to Definition(
            "delivery.timeout.ms",
            "An upper bound on the time to report success or failure after a call to <code>send()</code> returns. This limits the total time that a record will be delayed prior to sending, the time to await acknowledgement from the broker (if expected), and the time allowed for retriable send failures. The producer may report failure to send a record earlier than this config if either an unrecoverable error is encountered, the retries have been exhausted, or the record is added to a batch which reached an earlier delivery expiration deadline. The value of this config should be greater than or equal to the sum of <code>request.timeout.ms</code> and <code>linger.ms</code>.",
            "int"
        ),
        "linger.ms" to Definition(
            "linger.ms",
            "The producer groups together any records that arrive in between request transmissions into a single batched request. Normally this occurs only under load when records arrive faster than they can be sent out. However in some circumstances the client may want to reduce the number of requests even under moderate load. This setting accomplishes this by adding a small amount of artificial delay—that is, rather than immediately sending out a record, the producer will wait for up to the given delay to allow other records to be sent so that the sends can be batched together. This can be thought of as analogous to Nagle's algorithm in TCP. This setting gives the upper bound on the delay for batching: once we get <code>batch.size</code> worth of records for a partition it will be sent immediately regardless of this setting, however if we have fewer than this many bytes accumulated for this partition we will 'linger' for the specified time waiting for more records to show up. This setting defaults to 0 (i.e. no delay). Setting <code>linger.ms=5</code>, for example, would have the effect of reducing the number of requests sent but would add up to 5ms of latency to records sent in the absence of load.",
            "long"
        ),
        "max.block.ms" to Definition(
            "max.block.ms",
            "The configuration controls how long the <code>KafkaProducer</code>'s <code>send()</code>, <code>partitionsFor()</code>, <code>initTransactions()</code>, <code>sendOffsetsToTransaction()</code>, <code>commitTransaction()</code> and <code>abortTransaction()</code> methods will block. For <code>send()</code> this timeout bounds the total time waiting for both metadata fetch and buffer allocation (blocking in the user-supplied serializers or partitioner is not counted against this timeout). For <code>partitionsFor()</code> this timeout bounds the time spent waiting for metadata if it is unavailable. The transaction-related methods always block, but may timeout if the transaction coordinator could not be discovered or did not respond within the timeout.",
            "long"
        ),
        "max.request.size" to Definition(
            "max.request.size",
            "The maximum size of a request in bytes. This setting will limit the number of record batches the producer will send in a single request to avoid sending huge requests. This is also effectively a cap on the maximum uncompressed record batch size. Note that the server has its own cap on the record batch size (after compression if compression is enabled) which may be different from this.",
            "int"
        ),
        "partitioner.class" to Definition(
            "partitioner.class",
            "A class to use to determine which partition to be send to when produce the records. Available options are:",
            "class"
        ),
        "partitioner.ignore.keys" to Definition(
            "partitioner.ignore.keys",
            "When set to 'true' the producer won't use record keys to choose a partition. If 'false', producer would choose a partition based on a hash of the key when a key is present. Note: this setting has no effect if a custom partitioner is used.",
            "boolean"
        ),
        "receive.buffer.bytes" to Definition(
            "receive.buffer.bytes",
            "The size of the TCP receive buffer (SO_RCVBUF) to use when reading data. If the value is -1, the OS default will be used.",
            "int"
        ),
        "sasl.mechanism" to Definition(
            "sasl.mechanism",
            "SASL mechanism used for client connections. This may be any mechanism for which a security provider is available. GSSAPI is the default mechanism.",
            "string"
        ),
        "security.protocol" to Definition(
            "security.protocol",
            "Protocol used to communicate with brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL.",
            "string"
        ),
        "send.buffer.bytes" to Definition(
            "send.buffer.bytes",
            "The size of the TCP send buffer (SO_SNDBUF) to use when sending data. If the value is -1, the OS default will be used.",
            "int"
        ),
        "acks" to Definition(
            "acks",
            "The number of acknowledgments the producer requires the leader to have received before considering a request complete. This controls the  durability of records that are sent. The following settings are allowed:  ",
            "string"
        ),
        "enable.idempotence" to Definition(
            "enable.idempotence",
            "When set to 'true', the producer will ensure that exactly one copy of each message is written in the stream. If 'false', producer retries due to broker failures, etc., may write duplicates of the retried message in the stream. Note that enabling idempotence requires <code>max.in.flight.requests.per.connection</code> to be less than or equal to 5 (with message ordering preserved for any allowable value), <code>retries</code> to be greater than 0, and <code>acks</code> must be 'all'. ",
            "boolean"
        ),
        "interceptor.classes" to Definition(
            "interceptor.classes",
            "A list of classes to use as interceptors. Implementing the <code>org.apache.kafka.clients.consumer.ConsumerInterceptor</code> interface allows you to intercept (and possibly mutate) records received by the consumer. By default, there are no interceptors.",
            "list"
        ),
        "max.in.flight.requests.per.connection" to Definition(
            "max.in.flight.requests.per.connection",
            "The maximum number of unacknowledged requests the client will send on a single connection before blocking. Note that if this configuration is set to be greater than 1 and <code>enable.idempotence</code> is set to false, there is a risk of message reordering after a failed send due to retries (i.e., if retries are enabled);  if retries are disabled or if <code>enable.idempotence</code> is set to true, ordering will be preserved. Additionally, enabling idempotence requires the value of this configuration to be less than or equal to 5. If conflicting configurations are set and idempotence is not explicitly enabled, idempotence is disabled. ",
            "int"
        ),
        "metadata.max.age.ms" to Definition(
            "metadata.max.age.ms",
            "The period of time in milliseconds after which we force a refresh of metadata even if we haven't seen any partition leadership changes to proactively discover any new brokers or partitions.",
            "long"
        ),
        "metadata.max.idle.ms" to Definition(
            "metadata.max.idle.ms",
            "Controls how long the producer will cache metadata for a topic that's idle. If the elapsed time since a topic was last produced to exceeds the metadata idle duration, then the topic's metadata is forgotten and the next access to it will force a metadata fetch request.",
            "long"
        ),
        "partitioner.adaptive.partitioning.enable" to Definition(
            "partitioner.adaptive.partitioning.enable",
            "When set to 'true', the producer will try to adapt to broker performance and produce more messages to partitions hosted on faster brokers. If 'false', producer will try to distribute messages uniformly. Note: this setting has no effect if a custom partitioner is used",
            "boolean"
        ),
        "partitioner.availability.timeout.ms" to Definition(
            "partitioner.availability.timeout.ms",
            "If a broker cannot process produce requests from a partition for <code>partitioner.availability.timeout.ms</code> time, the partitioner treats that partition as not available.  If the value is 0, this logic is disabled. Note: this setting has no effect if a custom partitioner is used or <code>partitioner.adaptive.partitioning.enable</code> is set to 'false'",
            "long"
        ),
        "reconnect.backoff.max.ms" to Definition(
            "reconnect.backoff.max.ms",
            "The maximum amount of time in milliseconds to wait when reconnecting to a broker that has repeatedly failed to connect. If provided, the backoff per host will increase exponentially for each consecutive connection failure, up to this maximum. After calculating the backoff increase, 20% random jitter is added to avoid connection storms.",
            "long"
        ),
        "reconnect.backoff.ms" to Definition(
            "reconnect.backoff.ms",
            "The base amount of time to wait before attempting to reconnect to a given host. This avoids repeatedly connecting to a host in a tight loop. This backoff applies to all connection attempts by the client to a broker.",
            "long"
        ),
        "retry.backoff.ms" to Definition(
            "retry.backoff.ms",
            "The amount of time to wait before attempting to retry a failed request. This avoids repeatedly sending requests in a tight loop under some failure scenarios.",
            "long"
        ),
        "transaction.timeout.ms" to Definition(
            "transaction.timeout.ms",
            "The maximum amount of time in ms that the transaction coordinator will wait for a transaction status update from the producer before proactively aborting the ongoing transaction.If this value is larger than the transaction.max.timeout.ms setting in the broker, the request will fail with a <code>InvalidTxnTimeoutException</code> error.",
            "int"
        ),
        "transactional.id" to Definition(
            "transactional.id",
            "The TransactionalId to use for transactional delivery. This enables reliability semantics which span multiple producer sessions since it allows the client to guarantee that transactions using the same TransactionalId have been completed prior to starting any new transactions. If no TransactionalId is provided, then the producer is limited to idempotent delivery. If a TransactionalId is configured, <code>enable.idempotence</code> is implied. By default the TransactionId is not configured, which means transactions cannot be used. Note that, by default, transactions require a cluster of at least three brokers which is the recommended setting for production; for development you can change this, by adjusting broker setting <code>transaction.state.log.replication.factor</code>.",
            "string"
        ),
        "key.deserializer" to Definition(
            "key.deserializer",
            "Deserializer class for key that implements the <code>org.apache.kafka.common.serialization.Deserializer</code> interface.",
            "class"
        ),
        "value.deserializer" to Definition(
            "value.deserializer",
            "Deserializer class for value that implements the <code>org.apache.kafka.common.serialization.Deserializer</code> interface.",
            "class"
        ),
        "fetch.min.bytes" to Definition(
            "fetch.min.bytes",
            "The minimum amount of data the server should return for a fetch request. If insufficient data is available the request will wait for that much data to accumulate before answering the request. The default setting of 1 byte means that fetch requests are answered as soon as a single byte of data is available or the fetch request times out waiting for data to arrive. Setting this to something greater than 1 will cause the server to wait for larger amounts of data to accumulate which can improve server throughput a bit at the cost of some additional latency.",
            "int"
        ),
        "group.id" to Definition(
            "group.id",
            "A unique string that identifies the Connect cluster group this worker belongs to.",
            "string"
        ),
        "heartbeat.interval.ms" to Definition(
            "heartbeat.interval.ms",
            "The expected time between heartbeats to the group coordinator when using Kafka's group management facilities. Heartbeats are used to ensure that the worker's session stays active and to facilitate rebalancing when new members join or leave the group. The value must be set lower than <code>session.timeout.ms</code>, but typically should be set no higher than 1/3 of that value. It can be adjusted even lower to control the expected time for normal rebalances.",
            "int"
        ),
        "max.partition.fetch.bytes" to Definition(
            "max.partition.fetch.bytes",
            "The maximum amount of data per-partition the server will return. Records are fetched in batches by the consumer. If the first record batch in the first non-empty partition of the fetch is larger than this limit, the batch will still be returned to ensure that the consumer can make progress. The maximum record batch size accepted by the broker is defined via <code>message.max.bytes</code> (broker config) or <code>max.message.bytes</code> (topic config). See fetch.max.bytes for limiting the consumer request size.",
            "int"
        ),
        "session.timeout.ms" to Definition(
            "session.timeout.ms",
            "The timeout used to detect worker failures. The worker sends periodic heartbeats to indicate its liveness to the broker. If no heartbeats are received by the broker before the expiration of this session timeout, then the broker will remove the worker from the group and initiate a rebalance. Note that the value must be in the allowable range as configured in the broker configuration by <code>group.min.session.timeout.ms</code> and <code>group.max.session.timeout.ms</code>.",
            "int"
        ),
        "allow.auto.create.topics" to Definition(
            "allow.auto.create.topics",
            "Allow automatic topic creation on the broker when subscribing to or assigning a topic. A topic being subscribed to will be automatically created only if the broker allows for it using `auto.create.topics.enable` broker configuration. This configuration must be set to `false` when using brokers older than 0.11.0",
            "boolean"
        ),
        "auto.offset.reset" to Definition(
            "auto.offset.reset",
            "What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted): ",
            "string"
        ),
        "default.api.timeout.ms" to Definition(
            "default.api.timeout.ms",
            "Specifies the timeout (in milliseconds) for client APIs. This configuration is used as the default timeout for all client operations that do not specify a <code>timeout</code> parameter.",
            "int"
        ),
        "enable.auto.commit" to Definition(
            "enable.auto.commit",
            "If true the consumer's offset will be periodically committed in the background.",
            "boolean"
        ),
        "exclude.internal.topics" to Definition(
            "exclude.internal.topics",
            "Whether internal topics matching a subscribed pattern should be excluded from the subscription. It is always possible to explicitly subscribe to an internal topic.",
            "boolean"
        ),
        "group.instance.id" to Definition(
            "group.instance.id",
            "A unique identifier of the consumer instance provided by the end user. Only non-empty strings are permitted. If set, the consumer is treated as a static member, which means that only one instance with this ID is allowed in the consumer group at any time. This can be used in combination with a larger session timeout to avoid group rebalances caused by transient unavailability (e.g. process restarts). If not set, the consumer will join the group as a dynamic member, which is the traditional behavior.",
            "string"
        ),
        "isolation.level" to Definition(
            "isolation.level",
            "Controls how to read messages written transactionally. If set to <code>read_committed</code>, consumer.poll() will only return transactional messages which have been committed. If set to <code>read_uncommitted</code> (the default), consumer.poll() will return all messages, even transactional messages which have been aborted. Non-transactional messages will be returned unconditionally in either mode. ",
            "string"
        ),
        "max.poll.interval.ms" to Definition(
            "max.poll.interval.ms",
            "The maximum delay between invocations of poll() when using consumer group management. This places an upper bound on the amount of time that the consumer can be idle before fetching more records. If poll() is not called before expiration of this timeout, then the consumer is considered failed and the group will rebalance in order to reassign the partitions to another member. For consumers using a non-null <code>group.instance.id</code> which reach this timeout, partitions will not be immediately reassigned. Instead, the consumer will stop sending heartbeats and partitions will be reassigned after expiration of <code>session.timeout.ms</code>. This mirrors the behavior of a static consumer which has shutdown.",
            "int"
        ),
        "max.poll.records" to Definition(
            "max.poll.records",
            "The maximum number of records returned in a single call to poll(). Note, that <code>max.poll.records</code> does not impact the underlying fetching behavior. The consumer will cache the records from each fetch request and returns them incrementally from each poll.",
            "int"
        ),
        "partition.assignment.strategy" to Definition(
            "partition.assignment.strategy",
            "A list of class names or class types, ordered by preference, of supported partition assignment strategies that the client will use to distribute partition ownership amongst consumer instances when group management is used. Available options are:",
            "list"
        ),
        "auto.commit.interval.ms" to Definition(
            "auto.commit.interval.ms",
            "The frequency in milliseconds that the consumer offsets are auto-committed to Kafka if <code>enable.auto.commit</code> is set to <code>true</code>.",
            "int"
        ),
        "check.crcs" to Definition(
            "check.crcs",
            "Automatically check the CRC32 of the records consumed. This ensures no on-the-wire or on-disk corruption to the messages occurred. This check adds some overhead, so it may be disabled in cases seeking extreme performance.",
            "boolean"
        ),
        "client.rack" to Definition(
            "client.rack",
            "A rack identifier for this client. This can be any string value which indicates where this client is physically located. It corresponds with the broker config 'broker.rack'",
            "string"
        ),
        "fetch.max.wait.ms" to Definition(
            "fetch.max.wait.ms",
            "The maximum amount of time the server will block before answering the fetch request if there isn't sufficient data to immediately satisfy the requirement given by fetch.min.bytes.",
            "int"
        ),
        "config.storage.topic" to Definition(
            "config.storage.topic",
            "The name of the Kafka topic where connector configurations are stored",
            "string"
        ),
        "key.converter" to Definition(
            "key.converter",
            "Converter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. This controls the format of the keys in messages written to or read from Kafka, and since this is independent of connectors it allows any connector to work with any serialization format. Examples of common formats include JSON and Avro.",
            "class"
        ),
        "offset.storage.topic" to Definition(
            "offset.storage.topic",
            "The name of the Kafka topic where connector offsets are stored",
            "string"
        ),
        "status.storage.topic" to Definition(
            "status.storage.topic",
            "The name of the Kafka topic where connector and task status are stored",
            "string"
        ),
        "value.converter" to Definition(
            "value.converter",
            "Converter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. This controls the format of the values in messages written to or read from Kafka, and since this is independent of connectors it allows any connector to work with any serialization format. Examples of common formats include JSON and Avro.",
            "class"
        ),
        "exactly.once.source.support" to Definition(
            "exactly.once.source.support",
            "Whether to enable exactly-once support for source connectors in the cluster by using transactions to write source records and their source offsets, and by proactively fencing out old task generations before bringing up new ones. ",
            "string"
        ),
        "rebalance.timeout.ms" to Definition(
            "rebalance.timeout.ms",
            "The maximum allowed time for each worker to join the group once a rebalance has begun. This is basically a limit on the amount of time needed for all tasks to flush any pending data and commit offsets. If the timeout is exceeded, then the worker will be removed from the group, which will cause offset commit failures.",
            "int"
        ),
        "connector.client.config.override.policy" to Definition(
            "connector.client.config.override.policy",
            "Class name or alias of implementation of <code>ConnectorClientConfigOverridePolicy</code>. Defines what client configurations can be overriden by the connector. The default implementation is `All`, meaning connector configurations can override all client properties. The other possible policies in the framework include `None` to disallow connectors from overriding client properties, and `Principal` to allow connectors to override only client principals.",
            "string"
        ),
        "worker.sync.timeout.ms" to Definition(
            "worker.sync.timeout.ms",
            "When the worker is out of sync with other workers and needs to resynchronize configurations, wait up to this amount of time before giving up, leaving the group, and waiting a backoff period before rejoining.",
            "int"
        ),
        "worker.unsync.backoff.ms" to Definition(
            "worker.unsync.backoff.ms",
            "When the worker is out of sync with other workers and  fails to catch up within worker.sync.timeout.ms, leave the Connect cluster for this long before rejoining.",
            "int"
        ),
        "access.control.allow.methods" to Definition(
            "access.control.allow.methods",
            "Sets the methods supported for cross origin requests by setting the Access-Control-Allow-Methods header. The default value of the Access-Control-Allow-Methods header allows cross origin requests for GET, POST and HEAD.",
            "string"
        ),
        "access.control.allow.origin" to Definition(
            "access.control.allow.origin",
            "Value to set the Access-Control-Allow-Origin header to for REST API requests.To enable cross origin access, set this to the domain of the application that should be permitted to access the API, or '*' to allow access from any domain. The default value only allows access from the domain of the REST API.",
            "string"
        ),
        "admin.listeners" to Definition(
            "admin.listeners",
            "List of comma-separated URIs the Admin REST API will listen on. The supported protocols are HTTP and HTTPS. An empty or blank string will disable this feature. The default behavior is to use the regular listener (specified by the 'listeners' property).",
            "list"
        ),
        "config.providers" to Definition(
            "config.providers",
            "Comma-separated names of <code>ConfigProvider</code> classes, loaded and used in the order specified. Implementing the interface  <code>ConfigProvider</code> allows you to replace variable references in connector configurations, such as for externalized secrets. ",
            "list"
        ),
        "config.storage.replication.factor" to Definition(
            "config.storage.replication.factor",
            "Replication factor used when creating the configuration storage topic",
            "short"
        ),
        "connect.protocol" to Definition(
            "connect.protocol",
            "Compatibility mode for Kafka Connect Protocol",
            "string"
        ),
        "header.converter" to Definition(
            "header.converter",
            "HeaderConverter class used to convert between Kafka Connect format and the serialized form that is written to Kafka. This controls the format of the header values in messages written to or read from Kafka, and since this is independent of connectors it allows any connector to work with any serialization format. Examples of common formats include JSON and Avro. By default, the SimpleHeaderConverter is used to serialize header values to strings and deserialize them by inferring the schemas.",
            "class"
        ),
        "inter.worker.key.generation.algorithm" to Definition(
            "inter.worker.key.generation.algorithm",
            "The algorithm to use for generating internal request keys. The algorithm 'HmacSHA256' will be used as a default on JVMs that support it; on other JVMs, no default is used and a value for this property must be manually specified in the worker config.",
            "string"
        ),
        "inter.worker.key.size" to Definition(
            "inter.worker.key.size",
            "The size of the key to use for signing internal requests, in bits. If null, the default key size for the key generation algorithm will be used.",
            "int"
        ),
        "inter.worker.key.ttl.ms" to Definition(
            "inter.worker.key.ttl.ms",
            "The TTL of generated session keys used for internal request validation (in milliseconds)",
            "int"
        ),
        "inter.worker.signature.algorithm" to Definition(
            "inter.worker.signature.algorithm",
            "The algorithm used to sign internal requestsThe algorithm 'inter.worker.signature.algorithm' will be used as a default on JVMs that support it; on other JVMs, no default is used and a value for this property must be manually specified in the worker config.",
            "string"
        ),
        "inter.worker.verification.algorithms" to Definition(
            "inter.worker.verification.algorithms",
            "A list of permitted algorithms for verifying internal requests, which must include the algorithm used for the inter.worker.signature.algorithm property. The algorithm(s) '[HmacSHA256]' will be used as a default on JVMs that provide them; on other JVMs, no default is used and a value for this property must be manually specified in the worker config.",
            "list"
        ),
        "offset.flush.interval.ms" to Definition(
            "offset.flush.interval.ms",
            "Interval at which to try committing offsets for tasks.",
            "long"
        ),
        "offset.flush.timeout.ms" to Definition(
            "offset.flush.timeout.ms",
            "Maximum number of milliseconds to wait for records to flush and partition offset data to be committed to offset storage before cancelling the process and restoring the offset data to be committed in a future attempt. This property has no effect for source connectors running with exactly-once support.",
            "long"
        ),
        "offset.storage.partitions" to Definition(
            "offset.storage.partitions",
            "The number of partitions used when creating the offset storage topic",
            "int"
        ),
        "offset.storage.replication.factor" to Definition(
            "offset.storage.replication.factor",
            "Replication factor used when creating the offset storage topic",
            "short"
        ),
        "plugin.path" to Definition(
            "plugin.path",
            "List of paths separated by commas (,) that contain plugins (connectors, converters, transformations). The list should consist of top level directories that include any combination of: <br>a) directories immediately containing jars with plugins and their dependencies<br>b) uber-jars with plugins and their dependencies<br>c) directories immediately containing the package directory structure of classes of plugins and their dependencies<br>Note: symlinks will be followed to discover dependencies or plugins.<br>Examples: plugin.path=/usr/local/share/java,/usr/local/share/kafka/plugins,/opt/connectors<br>Do not use config provider variables in this property, since the raw path is used by the worker's scanner before config providers are initialized and used to replace variables.",
            "list"
        ),
        "response.http.headers.config" to Definition(
            "response.http.headers.config",
            "Rules for REST API HTTP response headers",
            "string"
        ),
        "rest.advertised.host.name" to Definition(
            "rest.advertised.host.name",
            "If this is set, this is the hostname that will be given out to other workers to connect to.",
            "string"
        ),
        "rest.advertised.listener" to Definition(
            "rest.advertised.listener",
            "Sets the advertised listener (HTTP or HTTPS) which will be given to other workers to use.",
            "string"
        ),
        "rest.advertised.port" to Definition(
            "rest.advertised.port",
            "If this is set, this is the port that will be given out to other workers to connect to.",
            "int"
        ),
        "rest.extension.classes" to Definition(
            "rest.extension.classes",
            "Comma-separated names of <code>ConnectRestExtension</code> classes, loaded and called in the order specified. Implementing the interface  <code>ConnectRestExtension</code> allows you to inject into Connect's REST API user defined resources like filters. Typically used to add custom capability like logging, security, etc. ",
            "list"
        ),
        "scheduled.rebalance.max.delay.ms" to Definition(
            "scheduled.rebalance.max.delay.ms",
            "The maximum delay that is scheduled in order to wait for the return of one or more departed workers before rebalancing and reassigning their connectors and tasks to the group. During this period the connectors and tasks of the departed workers remain unassigned",
            "int"
        ),
        "status.storage.partitions" to Definition(
            "status.storage.partitions",
            "The number of partitions used when creating the status storage topic",
            "int"
        ),
        "status.storage.replication.factor" to Definition(
            "status.storage.replication.factor",
            "Replication factor used when creating the status storage topic",
            "short"
        ),
        "task.shutdown.graceful.timeout.ms" to Definition(
            "task.shutdown.graceful.timeout.ms",
            "Amount of time to wait for tasks to shutdown gracefully. This is the total amount of time, not per task. All task have shutdown triggered, then they are waited on sequentially.",
            "long"
        ),
        "topic.creation.enable" to Definition(
            "topic.creation.enable",
            "Whether to allow automatic creation of topics used by source connectors, when source connectors are configured with `topic.creation.` properties. Each task will use an admin client to create its topics and will not depend on the Kafka brokers to create topics automatically.",
            "boolean"
        ),
        "topic.tracking.allow.reset" to Definition(
            "topic.tracking.allow.reset",
            "If set to true, it allows user requests to reset the set of active topics per connector.",
            "boolean"
        ),
        "topic.tracking.enable" to Definition(
            "topic.tracking.enable",
            "Enable tracking the set of active topics per connector during runtime.",
            "boolean"
        ),
        "name" to Definition(
            "name",
            "Globally unique name to use for this connector.",
            "string"
        ),
        "connector.class" to Definition(
            "connector.class",
            "Name or alias of the class for this connector. Must be a subclass of org.apache.kafka.connect.connector.Connector. If the connector is org.apache.kafka.connect.file.FileStreamSinkConnector, you can either specify this full name,  or use \"FileStreamSink\" or \"FileStreamSinkConnector\" to make the configuration a bit shorter",
            "string"
        ),
        "tasks.max" to Definition(
            "tasks.max",
            "Maximum number of tasks to use for this connector.",
            "int"
        ),
        "config.action.reload" to Definition(
            "config.action.reload",
            "The action that Connect should take on the connector when changes in external configuration providers result in a change in the connector's configuration properties. A value of 'none' indicates that Connect will do nothing. A value of 'restart' indicates that Connect should restart/reload the connector with the updated configuration properties.The restart may actually be scheduled in the future if the external configuration provider indicates that a configuration value will expire in the future.",
            "string"
        ),
        "transforms" to Definition(
            "transforms",
            "Aliases for the transformations to be applied to records.",
            "list"
        ),
        "predicates" to Definition(
            "predicates",
            "Aliases for the predicates used by transformations.",
            "list"
        ),
        "errors.retry.timeout" to Definition(
            "errors.retry.timeout",
            "The maximum duration in milliseconds that a failed operation will be reattempted. The default is 0, which means no retries will be attempted. Use -1 for infinite retries.",
            "long"
        ),
        "errors.retry.delay.max.ms" to Definition(
            "errors.retry.delay.max.ms",
            "The maximum duration in milliseconds between consecutive retry attempts. Jitter will be added to the delay once this limit is reached to prevent thundering herd issues.",
            "long"
        ),
        "errors.tolerance" to Definition(
            "errors.tolerance",
            "Behavior for tolerating errors during connector operation. 'none' is the default value and signals that any error will result in an immediate connector task failure; 'all' changes the behavior to skip over problematic records.",
            "string"
        ),
        "errors.log.enable" to Definition(
            "errors.log.enable",
            "If true, write each error and the details of the failed operation and problematic record to the Connect application log. This is 'false' by default, so that only errors that are not tolerated are reported.",
            "boolean"
        ),
        "errors.log.include.messages" to Definition(
            "errors.log.include.messages",
            "Whether to include in the log the Connect record that resulted in a failure.For sink records, the topic, partition, offset, and timestamp will be logged. For source records, the key and value (and their schemas), all headers, and the timestamp, Kafka topic, Kafka partition, source partition, and source offset will be logged. This is 'false' by default, which will prevent record keys, values, and headers from being written to log files.",
            "boolean"
        ),
        "topic.creation.groups" to Definition(
            "topic.creation.groups",
            "Groups of configurations for topics created by source connectors",
            "list"
        ),
        "exactly.once.support" to Definition(
            "exactly.once.support",
            "Permitted values are requested, required. If set to \"required\", forces a preflight check for the connector to ensure that it can provide exactly-once delivery with the given configuration. Some connectors may be capable of providing exactly-once delivery but not signal to Connect that they support this; in that case, documentation for the connector should be consulted carefully before creating it, and the value for this property should be set to \"requested\". Additionally, if the value is set to \"required\" but the worker that performs preflight validation does not have exactly-once support enabled for source connectors, requests to create or validate the connector will fail.",
            "string"
        ),
        "transaction.boundary" to Definition(
            "transaction.boundary",
            "Permitted values are: poll, interval, connector. If set to 'poll', a new producer transaction will be started and committed for every batch of records that each task from this connector provides to Connect. If set to 'connector', relies on connector-defined transaction boundaries; note that not all connectors are capable of defining their own transaction boundaries, and in that case, attempts to instantiate a connector with this value will fail. Finally, if set to 'interval', commits transactions only after a user-defined time interval has passed.",
            "string"
        ),
        "transaction.boundary.interval.ms" to Definition(
            "transaction.boundary.interval.ms",
            "If 'transaction.boundary' is set to 'interval', determines the interval for producer transaction commits by connector tasks. If unset, defaults to the value of the worker-level 'offset.flush.interval.ms' property. It has no effect if a different transaction.boundary is specified.",
            "long"
        ),
        "offsets.storage.topic" to Definition(
            "offsets.storage.topic",
            "The name of a separate offsets topic to use for this connector. If empty or not specified, the worker's global offsets topic name will be used. If specified, the offsets topic will be created if it does not already exist on the Kafka cluster targeted by this connector (which may be different from the one used for the worker's global offsets topic if the bootstrap.servers property of the connector's producer has been overridden from the worker's). Only applicable in distributed mode; in standalone mode, setting this property will have no effect.",
            "string"
        ),
        "topics" to Definition(
            "topics",
            "List of topics to consume, separated by commas",
            "list"
        ),
        "topics.regex" to Definition(
            "topics.regex",
            "Regular expression giving topics to consume. Under the hood, the regex is compiled to a <code>java.util.regex.Pattern</code>. Only one of topics or topics.regex should be specified.",
            "string"
        ),
        "errors.deadletterqueue.topic.name" to Definition(
            "errors.deadletterqueue.topic.name",
            "The name of the topic to be used as the dead letter queue (DLQ) for messages that result in an error when processed by this sink connector, or its transformations or converters. The topic name is blank by default, which means that no messages are to be recorded in the DLQ.",
            "string"
        ),
        "errors.deadletterqueue.topic.replication.factor" to Definition(
            "errors.deadletterqueue.topic.replication.factor",
            "Replication factor used to create the dead letter queue topic when it doesn't already exist.",
            "short"
        ),
        "errors.deadletterqueue.context.headers.enable" to Definition(
            "errors.deadletterqueue.context.headers.enable",
            "If true, add headers containing error context to the messages written to the dead letter queue. To avoid clashing with headers from the original record, all error context header keys, all error context header keys will start with <code>__connect.errors.</code>",
            "boolean"
        ),
        "application.id" to Definition(
            "application.id",
            "An identifier for the stream processing application. Must be unique within the Kafka cluster. It is used as 1) the default client-id prefix, 2) the group-id for membership management, 3) the changelog topic prefix.",
            "string"
        ),
        "num.standby.replicas" to Definition(
            "num.standby.replicas",
            "The number of standby replicas for each task.",
            "int"
        ),
        "state.dir" to Definition(
            "state.dir",
            "Directory location for state store. This path must be unique for each streams instance sharing the same underlying filesystem.",
            "string"
        ),
        "acceptable.recovery.lag" to Definition(
            "acceptable.recovery.lag",
            "The maximum acceptable lag (number of offsets to catch up) for a client to be considered caught-up enough to receive an active task assignment. Upon assignment, it will still restore the rest of the changelog before processing. To avoid a pause in processing during rebalances, this config should correspond to a recovery time of well under a minute for a given workload. Must be at least 0.",
            "long"
        ),
        "cache.max.bytes.buffering" to Definition(
            "cache.max.bytes.buffering",
            "Maximum number of memory bytes to be used for buffering across all threads",
            "long"
        ),
        "default.deserialization.exception.handler" to Definition(
            "default.deserialization.exception.handler",
            "Exception handling class that implements the <code>org.apache.kafka.streams.errors.DeserializationExceptionHandler</code> interface.",
            "class"
        ),
        "default.key.serde" to Definition(
            "default.key.serde",
            " Default serializer / deserializer class for key that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface. Note when windowed serde class is used, one needs to set the inner serde class that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface via 'default.windowed.key.serde.inner' or 'default.windowed.value.serde.inner' as well",
            "class"
        ),
        "default.list.key.serde.inner" to Definition(
            "default.list.key.serde.inner",
            "Default inner class of list serde for key that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface. This configuration will be read if and only if <code>default.key.serde</code> configuration is set to <code>org.apache.kafka.common.serialization.Serdes.ListSerde</code>",
            "class"
        ),
        "default.list.key.serde.type" to Definition(
            "default.list.key.serde.type",
            "Default class for key that implements the <code>java.util.List</code> interface. This configuration will be read if and only if <code>default.key.serde</code> configuration is set to <code>org.apache.kafka.common.serialization.Serdes.ListSerde</code> Note when list serde class is used, one needs to set the inner serde class that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface via 'default.list.key.serde.inner'",
            "class"
        ),
        "default.list.value.serde.inner" to Definition(
            "default.list.value.serde.inner",
            "Default inner class of list serde for value that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface. This configuration will be read if and only if <code>default.value.serde</code> configuration is set to <code>org.apache.kafka.common.serialization.Serdes.ListSerde</code>",
            "class"
        ),
        "default.list.value.serde.type" to Definition(
            "default.list.value.serde.type",
            "Default class for value that implements the <code>java.util.List</code> interface. This configuration will be read if and only if <code>default.value.serde</code> configuration is set to <code>org.apache.kafka.common.serialization.Serdes.ListSerde</code> Note when list serde class is used, one needs to set the inner serde class that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface via 'default.list.value.serde.inner'",
            "class"
        ),
        "default.production.exception.handler" to Definition(
            "default.production.exception.handler",
            "Exception handling class that implements the <code>org.apache.kafka.streams.errors.ProductionExceptionHandler</code> interface.",
            "class"
        ),
        "default.timestamp.extractor" to Definition(
            "default.timestamp.extractor",
            "Default timestamp extractor class that implements the <code>org.apache.kafka.streams.processor.TimestampExtractor</code> interface.",
            "class"
        ),
        "default.value.serde" to Definition(
            "default.value.serde",
            "Default serializer / deserializer class for value that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface. Note when windowed serde class is used, one needs to set the inner serde class that implements the <code>org.apache.kafka.common.serialization.Serde</code> interface via 'default.windowed.key.serde.inner' or 'default.windowed.value.serde.inner' as well",
            "class"
        ),
        "max.task.idle.ms" to Definition(
            "max.task.idle.ms",
            "This config controls whether joins and merges may produce out-of-order results. The config value is the maximum amount of time in milliseconds a stream task will stay idle when it is fully caught up on some (but not all) input partitions to wait for producers to send additional records and avoid potential out-of-order record processing across multiple input streams. The default (zero) does not wait for producers to send more records, but it does wait to fetch data that is already present on the brokers. This default means that for records that are already present on the brokers, Streams will process them in timestamp order. Set to -1 to disable idling entirely and process any locally available data, even though doing so may produce out-of-order processing.",
            "long"
        ),
        "max.warmup.replicas" to Definition(
            "max.warmup.replicas",
            "The maximum number of warmup replicas (extra standbys beyond the configured num.standbys) that can be assigned at once for the purpose of keeping  the task available on one instance while it is warming up on another instance it has been reassigned to. Used to throttle how much extra broker  traffic and cluster state can be used for high availability. Must be at least 1.",
            "int"
        ),
        "num.stream.threads" to Definition(
            "num.stream.threads",
            "The number of threads to execute stream processing.",
            "int"
        ),
        "processing.guarantee" to Definition(
            "processing.guarantee",
            "The processing guarantee that should be used. Possible values are <code>at_least_once</code> (default) and <code>exactly_once_v2</code> (requires brokers version 2.5 or higher). Deprecated options are <code>exactly_once</code> (requires brokers version 0.11.0 or higher) and <code>exactly_once_beta</code> (requires brokers version 2.5 or higher). Note that exactly-once processing requires a cluster of at least three brokers by default what is the recommended setting for production; for development you can change this, by adjusting broker setting <code>transaction.state.log.replication.factor</code> and <code>transaction.state.log.min.isr</code>.",
            "string"
        ),
        "rack.aware.assignment.tags" to Definition(
            "rack.aware.assignment.tags",
            "List of client tag keys used to distribute standby replicas across Kafka Streams instances. When configured, Kafka Streams will make a best-effort to distribute the standby tasks over each client tag dimension.",
            "list"
        ),
        "replication.factor" to Definition(
            "replication.factor",
            "The replication factor for change log topics and repartition topics created by the stream processing application. The default of <code>-1</code> (meaning: use broker default replication factor) requires broker version 2.4 or newer",
            "int"
        ),
        "task.timeout.ms" to Definition(
            "task.timeout.ms",
            "The maximum amount of time in milliseconds a task might stall due to internal errors and retries until an error is raised. For a timeout of 0ms, a task would raise an error for the first internal error. For any timeout larger than 0ms, a task will retry at least once before an error is raised.",
            "long"
        ),
        "topology.optimization" to Definition(
            "topology.optimization",
            "A configuration telling Kafka Streams if it should optimize the topology, disabled by default",
            "string"
        ),
        "application.server" to Definition(
            "application.server",
            "A host:port pair pointing to a user-defined endpoint that can be used for state store discovery and interactive queries on this KafkaStreams instance.",
            "string"
        ),
        "buffered.records.per.partition" to Definition(
            "buffered.records.per.partition",
            "Maximum number of records to buffer per partition.",
            "int"
        ),
        "built.in.metrics.version" to Definition(
            "built.in.metrics.version",
            "Version of the built-in metrics to use.",
            "string"
        ),
        "commit.interval.ms" to Definition(
            "commit.interval.ms",
            "The frequency in milliseconds with which to commit processing progress. For at-least-once processing, committing means to save the position (ie, offsets) of the processor. For exactly-once processing, it means to commit the transaction which includes to save the position and to make the committed data in the output topic visible to consumers with isolation level read_committed. (Note, if <code>processing.guarantee</code> is set to <code>exactly_once_v2</code>, <code>exactly_once</code>,the default value is <code>100</code>, otherwise the default value is <code>30000</code>.",
            "long"
        ),
        "default.dsl.store" to Definition(
            "default.dsl.store",
            "The default state store type used by DSL operators.",
            "string"
        ),
        "poll.ms" to Definition(
            "poll.ms",
            "The amount of time in milliseconds to block waiting for input.",
            "long"
        ),
        "probing.rebalance.interval.ms" to Definition(
            "probing.rebalance.interval.ms",
            "The maximum time in milliseconds to wait before triggering a rebalance to probe for warmup replicas that have finished warming up and are ready to become active. Probing rebalances will continue to be triggered until the assignment is balanced. Must be at least 1 minute.",
            "long"
        ),
        "repartition.purge.interval.ms" to Definition(
            "repartition.purge.interval.ms",
            "The frequency in milliseconds with which to delete fully consumed records from repartition topics. Purging will occur after at least this value since the last purge, but may be delayed until later. (Note, unlike <code>commit.interval.ms</code>, the default for this value remains unchanged when <code>processing.guarantee</code> is set to <code>exactly_once_v2</code>).",
            "long"
        ),
        "rocksdb.config.setter" to Definition(
            "rocksdb.config.setter",
            "A Rocks DB config setter class or class name that implements the <code>org.apache.kafka.streams.state.RocksDBConfigSetter</code> interface",
            "class"
        ),
        "state.cleanup.delay.ms" to Definition(
            "state.cleanup.delay.ms",
            "The amount of time in milliseconds to wait before deleting state when a partition has migrated. Only state directories that have not been modified for at least <code>state.cleanup.delay.ms</code> will be removed",
            "long"
        ),
        "upgrade.from" to Definition(
            "upgrade.from",
            "Allows upgrading in a backward compatible way. This is needed when upgrading from [0.10.0, 1.1] to 2.0+, or when upgrading from [2.0, 2.3] to 2.4+. When upgrading from 3.3 to a newer version it is not required to specify this config. Default is `null`. Accepted values are \"0.10.0\", \"0.10.1\", \"0.10.2\", \"0.11.0\", \"1.0\", \"1.1\", \"2.0\", \"2.1\", \"2.2\", \"2.3\", \"2.4\", \"2.5\", \"2.6\", \"2.7\", \"2.8\", \"3.0\", \"3.1\", \"3.2\" (for upgrading from the corresponding old version).",
            "string"
        ),
        "window.size.ms" to Definition(
            "window.size.ms",
            "Sets window size for the deserializer in order to calculate window end times.",
            "long"
        ),
        "windowed.inner.class.serde" to Definition(
            "windowed.inner.class.serde",
            " Default serializer / deserializer for the inner class of a windowed record. Must implement the <code>org.apache.kafka.common.serialization.Serde</code> interface. Note that setting this config in KafkaStreams application would result in an error as it is meant to be used only from Plain consumer client.",
            "string"
        ),
        "windowstore.changelog.additional.retention.ms" to Definition(
            "windowstore.changelog.additional.retention.ms",
            "Added to a windows maintainMs to ensure data is not deleted from the log prematurely. Allows for clock drift. Default is 1 day",
            "long"
        ),
        "confluent.balancer.disk.max.load" to Definition(
            "confluent.balancer.disk.max.load",
            "This config specifies the maximum load for disk usage as a proportion of disk capacity. Valid values are between 0 and 1.",
            "double"
        ),
        "confluent.balancer.enable" to Definition(
            "confluent.balancer.enable",
            "This config controls whether the balancer is enabled",
            "boolean"
        ),
        "confluent.balancer.heal.broker.failure.threshold.ms" to Definition(
            "confluent.balancer.heal.broker.failure.threshold.ms",
            "This config specifies how long the balancer will wait after detecting a broker failure before triggering a balancing action. -1 means that broker failures will not trigger balancing actions",
            "long"
        ),
        "confluent.balancer.heal.uneven.load.trigger" to Definition(
            "confluent.balancer.heal.uneven.load.trigger",
            "Controls what causes the Confluent DataBalancer to start rebalance operations. Acceptable values are ANY_UNEVEN_LOAD and EMPTY_BROKER",
            "string"
        ),
        "confluent.balancer.incremental.balancing.enabled" to Definition(
            "confluent.balancer.incremental.balancing.enabled",
            "A boolean value controlling whether to use incremental balancing strategy or not.",
            "boolean"
        ),
        "confluent.balancer.max.replicas" to Definition(
            "confluent.balancer.max.replicas",
            "The replica capacity is the maximum number of replicas the balancer will place on a single broker.",
            "long"
        ),
        "confluent.balancer.network.in.max.bytes.per.second" to Definition(
            "confluent.balancer.network.in.max.bytes.per.second",
            "This config specifies the upper capacity limit for network incoming bytes per second per broker. The Confluent DataBalancer will attempt to keep incoming data throughput below this limit.",
            "long"
        ),
        "confluent.balancer.network.out.max.bytes.per.second" to Definition(
            "confluent.balancer.network.out.max.bytes.per.second",
            "This config specifies the upper capacity limit for network outgoing bytes per second per broker. The Confluent DataBalancer will attempt to keep outgoing data throughput below this limit.",
            "long"
        ),
        "confluent.balancer.throttle.bytes.per.second" to Definition(
            "confluent.balancer.throttle.bytes.per.second",
            "This config specifies the upper bound for bandwidth in bytes to move replicas around for replica reassignment. A value of -1 disables throttling entirely.",
            "long"
        ),
        "confluent.offsets.topic.placement.constraints" to Definition(
            "confluent.offsets.topic.placement.constraints",
            "This configuration is a JSON object that controls the set of brokers (replicas) which will always be allowed to join the ISR. And the set of brokers (observers) which are not allowed to join the ISR. The format of JSON is:{  “version”: 1,  “replicas”: [    {      “count”: 2,      “constraints”: {“rack”: “east-1”}    ),    {      “count”: 1,      “constraints”: {“rack”: “east-2”}    }  ],  “observers”:[    {      “count”: 1,      “constraints”: {“rack”: “west-1”}    }  ]}",
            "string"
        ),
        "confluent.security.event.logger.authentication.enable" to Definition(
            "confluent.security.event.logger.authentication.enable",
            "Enable authentication audit logs",
            "boolean"
        ),
        "confluent.security.event.logger.enable" to Definition(
            "confluent.security.event.logger.enable",
            "Whether the event logger is enabled",
            "boolean"
        ),
        "confluent.tier.azure.block.blob.container" to Definition(
            "confluent.tier.azure.block.blob.container",
            "The Azure Block Blob Container to use for tiered storage.",
            "string"
        ),
        "confluent.tier.azure.block.blob.cred.file.path" to Definition(
            "confluent.tier.azure.block.blob.cred.file.path",
            "The path to the credentials file used to create the Azure Block Blob client. It uses a JSON file with one of the following options:- <cite>connectionString</cite> for the target <cite>confluent.tier.azure.block.blob.container</cite>.- <cite>azureClientId</cite>, <cite>azureTenantId</cite> and <cite>azureClientSecret</cite> for the target <cite>confluent.tier.azure.block.blob.container</cite>.Please refer to Azure documentation for further information. If this property is not specified, the Azure Block Blob client will use the <cite>DefaultAzureCredential</cite> to locate the credentials across several well-known locations.",
            "string"
        ),
        "confluent.tier.azure.block.blob.endpoint" to Definition(
            "confluent.tier.azure.block.blob.endpoint",
            "The Azure Storage Account endpoint, in the format of /{accountName}.blob.core.windows.net.",
            "string"
        ),
        "confluent.tier.azure.block.blob.prefix" to Definition(
            "confluent.tier.azure.block.blob.prefix",
            "This prefix will be added to tiered storage objects stored in the target Azure Block Blob Container.",
            "string"
        ),
        "confluent.tier.gcs.bucket" to Definition(
            "confluent.tier.gcs.bucket",
            "The GCS bucket to use for tiered storage.",
            "string"
        ),
        "confluent.tier.gcs.prefix" to Definition(
            "confluent.tier.gcs.prefix",
            "This prefix will be added to tiered storage objects stored in GCS.",
            "string"
        ),
        "confluent.tier.gcs.region" to Definition(
            "confluent.tier.gcs.region",
            "The GCS region to use for tiered storage.",
            "string"
        ),
        "confluent.tier.local.hotset.bytes" to Definition(
            "confluent.tier.local.hotset.bytes",
            "When tiering is enabled, this configuration controls the maximum size a partition (which consists of log segments) can grow to on broker-local storage before we will discard old log segments to free up space. Log segments retained on broker-local storage is referred as the “hotset”. Segments discarded from local store could continue to exist in tiered storage and remain available for fetches depending on retention configurations. By default there is no size limit only a time limit. Since this limit is enforced at the partition level, multiply it by the number of partitions to compute the topic hotset in bytes.",
            "long"
        ),
        "confluent.tier.local.hotset.ms" to Definition(
            "confluent.tier.local.hotset.ms",
            "When tiering is enabled, this configuration controls the maximum time we will retain a log segment on broker-local storage before we will discard it to free up space. Segments discarded from local store could continue to exist in tiered storage and remain available for fetches depending on retention configurations. If set to -1, no time limit is applied.",
            "long"
        ),
        "confluent.tier.metadata.replication.factor" to Definition(
            "confluent.tier.metadata.replication.factor",
            "The replication factor for the tier metadata topic (set higher to ensure availability).",
            "short"
        ),
        "confluent.tier.s3.bucket" to Definition(
            "confluent.tier.s3.bucket",
            "The S3 bucket to use for tiered storage.",
            "string"
        ),
        "confluent.tier.s3.prefix" to Definition(
            "confluent.tier.s3.prefix",
            "This prefix will be added to tiered storage objects stored in S3.",
            "string"
        ),
        "confluent.tier.s3.region" to Definition(
            "confluent.tier.s3.region",
            "The S3 region to use for tiered storage.",
            "string"
        ),
        "confluent.transaction.state.log.placement.constraints" to Definition(
            "confluent.transaction.state.log.placement.constraints",
            "This configuration is a JSON object that controls the set of brokers (replicas) which will always be allowed to join the ISR. And the set of brokers (observers) which are not allowed to join the ISR. The format of JSON is:{  “version”: 1,  “replicas”: [    {      “count”: 2,      “constraints”: {“rack”: “east-1”}    ),    {      “count”: 1,      “constraints”: {“rack”: “east-2”}    }  ],  “observers”:[    {      “count”: 1,      “constraints”: {“rack”: “west-1”}    }  ]}",
            "string"
        ),
        "confluent.balancer.exclude.topic.names" to Definition(
            "confluent.balancer.exclude.topic.names",
            "This config accepts a list of topic names that will be excluded from rebalancing. For example, 'confluent.balancer.exclude.topic.names=[topic1, topic2]'",
            "list"
        ),
        "confluent.balancer.exclude.topic.prefixes" to Definition(
            "confluent.balancer.exclude.topic.prefixes",
            "This config accepts a list of topic prefixes that will be excluded from rebalancing. For example, 'confluent.balancer.exclude.topic.prefixes=[prefix1, prefix2]' would exclude topics 'prefix1-suffix1', 'prefix1-suffix2', 'prefix2-suffix3', but not 'abc-prefix1-xyz' and 'def-prefix2'",
            "list"
        ),
        "confluent.clm.enabled" to Definition(
            "confluent.clm.enabled",
            "Enable/Disable custom lifecycle manager",
            "boolean"
        ),
        "confluent.clm.frequency.in.hours" to Definition(
            "confluent.clm.frequency.in.hours",
            "The frequency to run custom lifecycle manager in hours",
            "int"
        ),
        "confluent.clm.max.backup.days" to Definition(
            "confluent.clm.max.backup.days",
            "Maximum backup in days",
            "int"
        ),
        "confluent.clm.min.delay.in.minutes" to Definition(
            "confluent.clm.min.delay.in.minutes",
            "Minimum delay in minutes before CLM can start when leader replica boots up",
            "int"
        ),
        "confluent.clm.thread.pool.size" to Definition(
            "confluent.clm.thread.pool.size",
            "Size of thread pool used for Azure based clusters",
            "int"
        ),
        "confluent.clm.topic.retention.days.to.backup.days" to Definition(
            "confluent.clm.topic.retention.days.to.backup.days",
            "Comma separated list of KV pairs representing topic retention rounded down to days to retention for backed up segments in days",
            "string"
        ),
        "confluent.cluster.link.enable" to Definition(
            "confluent.cluster.link.enable",
            "Enable cluster linking feature.",
            "boolean"
        ),
        "confluent.cluster.link.fetch.response.min.bytes" to Definition(
            "confluent.cluster.link.fetch.response.min.bytes",
            "Minimum fetch response size used by cluster link fetchers if the total size is limited by 'confluent.cluster.link.fetch.response.total.bytes'.",
            "int"
        ),
        "confluent.cluster.link.fetch.response.total.bytes" to Definition(
            "confluent.cluster.link.fetch.response.total.bytes",
            "Maximum amount of data fetched by all cluster link fetchers in a broker. If total 'replica.fetch.response.max.bytes' for all fetchers on the broker exceeds this value, all cluster link fetchers reduce their response size to meet this limit. Minimum value for each fetcher can be configured using 'confluent.cluster.link.fetch.response.min.bytes'.",
            "int"
        ),
        "confluent.cluster.link.io.max.bytes.per.second" to Definition(
            "confluent.cluster.link.io.max.bytes.per.second",
            "A long value representing the upper bound (bytes/sec) on throughput for cluster link replication. It is suggested that the limit be kept above 1MB/s for accurate behaviour.",
            "long"
        ),
        "confluent.cluster.link.metadata.topic.create.retry.delay.ms" to Definition(
            "confluent.cluster.link.metadata.topic.create.retry.delay.ms",
            "The retry delay in milliseconds when the attempt to create cluster linking metadata topic is failed",
            "long"
        ),
        "confluent.cluster.link.metadata.topic.enable" to Definition(
            "confluent.cluster.link.metadata.topic.enable",
            "Whether the cluster link metadata topic should be created and (or) used. Only applicable in ZK mode",
            "boolean"
        ),
        "confluent.cluster.link.metadata.topic.min.isr" to Definition(
            "confluent.cluster.link.metadata.topic.min.isr",
            "The minimum number of in sync replicas for the cluster linking metadata topic",
            "short"
        ),
        "confluent.cluster.link.metadata.topic.partitions" to Definition(
            "confluent.cluster.link.metadata.topic.partitions",
            "Number of partitions for the cluster linking metadata topic",
            "int"
        ),
        "confluent.cluster.link.metadata.topic.replication.factor" to Definition(
            "confluent.cluster.link.metadata.topic.replication.factor",
            "Replication factor the for the cluster linking metadata topic",
            "short"
        ),
        "confluent.group.metadata.load.threads" to Definition(
            "confluent.group.metadata.load.threads",
            "The number of threads group metadata load / unload can use to concurrently load / unload metadata.",
            "int"
        ),
        "confluent.replica.fetch.backoff.max.ms" to Definition(
            "confluent.replica.fetch.backoff.max.ms",
            "The maximum amount of time in milliseconds to wait when fetch partition fails repeatedly. If provided, the backoff will increase exponentially for each consecutive failure, up to this maximum.",
            "int"
        ),
        "confluent.tier.archiver.num.threads" to Definition(
            "confluent.tier.archiver.num.threads",
            "The size of the thread pool used for tiering data to remote storage. This thread pool is also used to garbage collect data in tiered storage that has been deleted.",
            "int"
        ),
        "confluent.tier.backend" to Definition(
            "confluent.tier.backend",
            "Tiered storage backend to use",
            "string"
        ),
        "confluent.tier.enable" to Definition(
            "confluent.tier.enable",
            "Allow tiering for topic(s). This enables tiering and fetching of data to and from the configured remote storage.",
            "boolean"
        ),
        "confluent.tier.feature" to Definition(
            "confluent.tier.feature",
            "Feature flag that enables components related to tiered storage. This must be enabled before tiering could be enabled by using <cite>confluent.tier.enable</cite> property.",
            "boolean"
        ),
        "confluent.tier.fetcher.num.threads" to Definition(
            "confluent.tier.fetcher.num.threads",
            "The size of the thread pool used by the TierFetcher. Roughly corresponds to number of concurrent fetch requests that can be served from tiered storage.",
            "int"
        ),
        "confluent.tier.max.partition.fetch.bytes.override" to Definition(
            "confluent.tier.max.partition.fetch.bytes.override",
            "For tier fetches, this configuration allows overriding the consumer's <cite>max.partition.fetch.bytes</cite> configuration. When fetching tiered data, we will use the maximum of the consumer's configuration and this override. Setting this to a value higher than that of the consumer's could improve batching and effective throughput of tiered fetches. The override is disabled when set to 0.",
            "int"
        ),
        "confluent.tier.metadata.bootstrap.servers" to Definition(
            "confluent.tier.metadata.bootstrap.servers",
            "The bootstrap servers used to read from and write to the tier metadata topic. If this is not configured, the configured inter-broker listener would be used.",
            "string"
        ),
        "log.deletion.max.segments.per.run" to Definition(
            "log.deletion.max.segments.per.run",
            "The maximum eligible segments that can be deleted during every check",
            "int"
        ),
        "log.deletion.throttler.disk.free.headroom.bytes" to Definition(
            "log.deletion.throttler.disk.free.headroom.bytes",
            "The headroom for the disk space available (in bytes) that will be added toconfluent.backpressure.disk.free.threshold.bytes (if enabled) to determine the threshold for the minimum available disk space across all the log dirs. This configuration acts as a safety net enabling the broker to reclaim disk space quickly when the broker's available disk space is running low. When the available disk space is below the threshold value, the broker auto disables the effect oflog.deletion.max.segments.per.run and deletes all eligible segments during periodic retention. When the available disk space is at or above the threshold, the broker auto enables the effect of log.deletion.max.segments.per.run.",
            "long"
        ),
        "sasl.server.authn.async.enable" to Definition(
            "sasl.server.authn.async.enable",
            "Setting this configuration to true allows the SASL authentication to attempt to perform authentication asynchronously.",
            "boolean"
        ),
        "sasl.server.authn.async.max.threads" to Definition(
            "sasl.server.authn.async.max.threads",
            "Maximum number of threads in async authentication thread pool to perform authentication asynchronously.",
            "int"
        ),
        "sasl.server.authn.async.timeout.ms" to Definition(
            "sasl.server.authn.async.timeout.ms",
            "The broker will attempt to forcibly stop authentication that runs longer than this.",
            "long"
        ),
        "confluent.authorizer.authority.name" to Definition(
            "confluent.authorizer.authority.name",
            "The DNS name of the authority that this clusteruses to authorize. This should be a name for the cluster hosting metadata topics.",
            "string"
        ),
        "confluent.balancer.resource.utilization.detector.enabled" to Definition(
            "confluent.balancer.resource.utilization.detector.enabled",
            "Specify if resource optimization detector is enabled",
            "boolean"
        ),
        "confluent.cluster.link.replication.quota.mode" to Definition(
            "confluent.cluster.link.replication.quota.mode",
            "The mode for cluster link quota that applies to 'confluent.cluster.link.io.max.bytes.per.second'. The mode indicates which inbound traffic is counted towards the limit. Valid values are 'CLUSTER_LINK_ONLY' and 'TOTAL_INBOUND'.",
            "string"
        ),
        "confluent.cluster.link.replication.quota.window.num" to Definition(
            "confluent.cluster.link.replication.quota.window.num",
            "The number of samples to retain in memory for cluster link replication quotas",
            "int"
        ),
        "confluent.cluster.link.replication.quota.window.size.seconds" to Definition(
            "confluent.cluster.link.replication.quota.window.size.seconds",
            "The time span of each sample for cluster link replication quotas",
            "int"
        ),
        "confluent.defer.isr.shrink.enable" to Definition(
            "confluent.defer.isr.shrink.enable",
            "Defer ISR shrinking for partitions that only have messages with acks = “all” if shrinking ISR would make partition fall under min ISR.",
            "boolean"
        ),
        "confluent.log.placement.constraints" to Definition(
            "confluent.log.placement.constraints",
            "This configuration is a JSON object that controls the set of brokers (replicas) which will always be allowed to join the ISR. And the set of brokers (observers) which are not allowed to join the ISR. The format of JSON is:{  “version”: 1,  “replicas”: [    {      “count”: 2,      “constraints”: {“rack”: “east-1”}    ),    {      “count”: 1,      “constraints”: {“rack”: “east-2”}    }  ],  “observers”:[    {      “count”: 1,      “constraints”: {“rack”: “west-1”}    }  ]}",
            "string"
        ),
        "confluent.metadata.server.cluster.registry.clusters" to Definition(
            "confluent.metadata.server.cluster.registry.clusters",
            "JSON defining initial state of Cluster Registry. This should not be set manually, instead Cluster Registry http apis should be used.",
            "string"
        ),
        "confluent.reporters.telemetry.auto.enable" to Definition(
            "confluent.reporters.telemetry.auto.enable",
            "Auto-enable telemetry on the broker. This will add the telemetry reporter to the broker's 'metric.reporters' property if it is not already present. Disabling this property will prevent Self-balancing Clusters from working properly.",
            "boolean"
        ),
        "confluent.security.event.router.config" to Definition(
            "confluent.security.event.router.config",
            "JSON configuration for routing events to topics",
            "string"
        ),
        "confluent.telemetry.enabled" to Definition(
            "confluent.telemetry.enabled",
            "True if telemetry data can to be reported to Confluent Cloud",
            "boolean"
        ),
        "confluent.tier.fenced.segment.delete.delay.ms" to Definition(
            "confluent.tier.fenced.segment.delete.delay.ms",
            "Segments uploaded by fenced leaders may still be being uploaded when retention occurs on a newly elected leader. Storage backends like AWS S3 return success for delete operations if the object is not found, so to address this edge case the deletion of segments uploaded by fenced leaders is delayed by <code>confluent.tier.fenced.segment.delete.delay.ms</code> with the assumption that the upload will be completed by the time the deletion occurs.",
            "long"
        ),
        "confluent.tier.gcs.cred.file.path" to Definition(
            "confluent.tier.gcs.cred.file.path",
            "The path to the credentials file used to create the GCS client. This uses the default GCS configuration file format; please refer to GCP documentation on how to generate the credentials file. If not specified, the GCS client will be instantiated using the default service account available.",
            "string"
        ),
        "confluent.tier.s3.aws.endpoint.override" to Definition(
            "confluent.tier.s3.aws.endpoint.override",
            "Override picking an S3 endpoint. Normally this is performed automatically by the client.",
            "string"
        ),
        "confluent.tier.s3.cred.file.path" to Definition(
            "confluent.tier.s3.cred.file.path",
            "The path to the credentials file used to create the S3 client. It uses a Java properties file and extracts the AWS access key from the “accessKey” property and AWS secret access key from the “secretKey” property. Please refer to AWS documentation for further information. If this property is not specified, the S3 client will use the <cite>DefaultAWSCredentialsProviderChain</cite> to locate the credentials.",
            "string"
        ),
        "confluent.tier.s3.force.path.style.access" to Definition(
            "confluent.tier.s3.force.path.style.access",
            "Configures the client to use path-style access for all requests. This flag is not enabled by default. The default behavior is to detect which access style to use based on the configured endpoint and the bucket being accessed. Setting this flag will result in path-style access being forced for all requests.",
            "boolean"
        ),
        "confluent.tier.s3.ssl.enabled.protocols" to Definition(
            "confluent.tier.s3.ssl.enabled.protocols",
            "The list of protocols enabled for SSL connections. The default is 'TLSv1.2,TLSv1.3' when running with Java 11 or newer, 'TLSv1.2' otherwise.",
            "list"
        ),
        "confluent.tier.s3.ssl.key.password" to Definition(
            "confluent.tier.s3.ssl.key.password",
            "Key password when using TLS connectivity to AWS S3. Overrides any explicit value set via the <code>javax.net.ssl.keyPassword</code> system property (note the camelCase).",
            "password"
        ),
        "confluent.tier.s3.ssl.keystore.location" to Definition(
            "confluent.tier.s3.ssl.keystore.location",
            "Keystore location when using TLS connectivity to AWS S3. Overrides any explicit value set via the <code>javax.net.ssl.keyStore</code> system property (note the camelCase).",
            "string"
        ),
        "confluent.tier.s3.ssl.keystore.password" to Definition(
            "confluent.tier.s3.ssl.keystore.password",
            "Keystore password when using TLS connectivity to AWS S3. Overrides any explicit value set via the <code>javax.net.ssl.keyStorePassword</code> system property (note the camelCase).",
            "password"
        ),
        "confluent.tier.s3.ssl.keystore.type" to Definition(
            "confluent.tier.s3.ssl.keystore.type",
            "Keystore type when using TLS connectivity to AWS S3. Overrides any explicit value set via the <code>javax.net.ssl.keyStoreType</code> system property (note the camelCase).",
            "string"
        ),
        "confluent.tier.s3.ssl.protocol" to Definition(
            "confluent.tier.s3.ssl.protocol",
            "The SSL protocol used to generate the SSLContext. The default is 'TLSv1.3' when running with Java 11 or newer, 'TLSv1.2' otherwise.",
            "string"
        ),
        "confluent.tier.s3.ssl.truststore.location" to Definition(
            "confluent.tier.s3.ssl.truststore.location",
            "Truststore location when using TLS connectivity to AWS S3. Overrides any explicit value set via the <code>javax.net.ssl.trustStore</code> system property (note the camelCase).",
            "string"
        ),
        "confluent.tier.s3.ssl.truststore.password" to Definition(
            "confluent.tier.s3.ssl.truststore.password",
            "Truststore password when using TLS connectivity to AWS S3. Overrides any explicit value set via the <code>javax.net.ssl.trustStorePassword</code> system property (note the camelCase).",
            "password"
        ),
        "confluent.tier.s3.ssl.truststore.type" to Definition(
            "confluent.tier.s3.ssl.truststore.type",
            "Truststore type when using TLS connectivity to AWS S3. Overrides any explicit value set via the <code>javax.net.ssl.trustStoreType</code> system property (note the camelCase).",
            "string"
        ),
        "confluent.tier.topic.delete.backoff.ms" to Definition(
            "confluent.tier.topic.delete.backoff.ms",
            "Maximum amount of time to wait before deleting tiered objects for a deleted partition.",
            "long"
        ),
        "confluent.tier.topic.delete.check.interval.ms" to Definition(
            "confluent.tier.topic.delete.check.interval.ms",
            "Frequency at which tiered objects cleanup is run for deleted topics.",
            "long"
        ),
        "confluent.tier.topic.delete.max.inprogress.partitions" to Definition(
            "confluent.tier.topic.delete.max.inprogress.partitions",
            "Maximum number of partitions deleted from remote storage in the deletion interval defined by <cite>confluent.tier.topic.delete.check.interval.ms</cite>",
            "int"
        ),
        "create.cluster.link.policy.class.name" to Definition(
            "create.cluster.link.policy.class.name",
            "The create cluster link policy class that should be used for validation. This class should implement the <code>org.apache.kafka.server.policy.CreateClusterLinkPolicy</code> interface",
            "class"
        ),
        "enable.fips" to Definition(
            "enable.fips",
            "Enable FIPS mode on the server. If FIPS mode is enabled, broker listener security protocols, TLS versions and cipher suites will be validated based on FIPS compliance requirement.",
            "boolean"
        ),
        "follower.replication.throttled.rate" to Definition(
            "follower.replication.throttled.rate",
            "A long representing the upper bound (bytes/sec) on inbound replication traffic for follower replicas enumerated in the property follower.replication.throttled.replicas (for each topic). It is suggested that the limit be kept above 1MB/s for accurate behavior.",
            "long"
        ),
        "leader.replication.throttled.rate" to Definition(
            "leader.replication.throttled.rate",
            "A long representing the upper bound (bytes/sec) on outbound replication traffic for leader replicas enumerated in the property leader.replication.throttled.replicas (for each topic). It is suggested that the limit be kept above 1MB/s for accurate behavior.",
            "long"
        )
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
