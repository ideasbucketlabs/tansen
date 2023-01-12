/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface NewTopic {
    name: string
    partition: number
    'replication.factor': number
    'min.insync.replicas': number
    'cleanup.policy': 'delete' | 'compact' | 'compact,delete'
    'retention.ms': bigint
    'retention.bytes': bigint
    'max.message.bytes': number
    'compression.type': string
    'delete.retention.ms': bigint
    'file.delete.delay.ms': bigint
    'flush.messages': bigint
    'flush.ms': bigint
    'follower.replication.throttled.replicas': string
    'index.interval.bytes': number
    'leader.replication.throttled.replicas': string
    'max.compaction.lag.ms': bigint
    'message.downconversion.enable': boolean
    'message.timestamp.difference.max.ms': bigint
    'message.timestamp.type': 'CreateTime' | 'LogAppendTime'
    'min.cleanable.dirty.ratio': number
    'min.compaction.lag.ms': bigint
    preallocate: boolean
    'segment.bytes': number
    'segment.index.bytes': number
    'segment.jitter.ms': bigint
    'segment.ms': bigint
    'unclean.leader.election.enable': boolean
}
