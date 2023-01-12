/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { NewTopic } from '@/entity/NewTopic'
import { isBigInt, isNumber, isString } from '@/util/Util'

const positiveIntegerRegex = /^[1-9][0-9]*$/
const positiveIntegerWithInfinityRegex = /^(-1|[1-9][0-9]*)$/
const positiveIntegerWithZeroRegex = /^[0-9]+$/

function convertToBigInt(value: unknown): bigint {
    if (isBigInt(value)) {
        return value as bigint
    }

    if (isString(value)) {
        return BigInt(value as string)
    }

    if (isNumber(value)) {
        return BigInt((value as number).toString(10))
    }

    throw new Error('Unknown type')
}

export function validate(topic: NewTopic): Map<string, string> {
    const result = new Map()
    if (topic.name === null || topic.name.trim().length === 0 || !/^([a-zA-Z0-9._-])+$/.test(topic.name)) {
        result.set('name', 'Topic name cannot be null, blank or empty.')
    }

    if (topic.partition === null) {
        result.set('partition', 'Topic partition cannot be null, must be integer and greater than 0.')
    } else if (!positiveIntegerRegex.test(topic.partition.toString(10))) {
        result.set('partition', 'Topic partition cannot be null, must be integer and greater than 0.')
    }

    if (topic['replication.factor'] === null) {
        result.set('replication.factor', 'replication.factor cannot be null, must be integer and greater than 0.')
    } else if (!positiveIntegerRegex.test(topic['replication.factor'].toString(10))) {
        result.set('replication.factor', 'replication.factor cannot be null, must be integer and greater than 0.')
    }

    if (!result.has('replication.factor')) {
        if (parseInt(topic['replication.factor'].toString(10), 10) > 32767) {
            result.set('replication.factor', 'replication.factor cannot  greater than 32,767.')
        }
    }

    if (topic['min.insync.replicas'] === null) {
        result.set('min.insync.replicas', 'min.insync.replicas cannot be null, must be integer and greater than 0.')
    } else if (!positiveIntegerRegex.test(topic['min.insync.replicas'].toString(10))) {
        result.set('min.insync.replicas', 'min.insync.replicas cannot be null, must be integer and greater than 0.')
    }

    if (!result.has('min.insync.replicas')) {
        if (parseInt(topic['min.insync.replicas'].toString(10), 10) > 32767) {
            result.set('min.insync.replicas', 'min.insync.replicas cannot  greater than 32,767.')
        }
    }

    if (!result.has('min.insync.replicas') && !result.has('replication.factor')) {
        const replicationFactor = parseInt(topic['replication.factor'].toString(), 10)
        const minimumInSyncReplica = parseInt(topic['min.insync.replicas'].toString(), 10)

        if (minimumInSyncReplica > replicationFactor) {
            result.set('min.insync.replicas', 'min.insync.replicas cannot be more than Replication factor.')
        }
    }

    if (
        topic['cleanup.policy'] === null ||
        (topic['cleanup.policy'] !== 'delete' &&
            topic['cleanup.policy'] !== 'compact' &&
            topic['cleanup.policy'] !== 'compact,delete')
    ) {
        result.set('cleanup.policy', 'cleanup.policy must be either delete or compact or compact,delete.')
    }

    if (
        topic['message.timestamp.type'] === null ||
        (topic['message.timestamp.type'] !== 'CreateTime' && topic['message.timestamp.type'] !== 'LogAppendTime')
    ) {
        result.set('message.timestamp.type', 'message.timestamp.type must be either CreateTime or LogAppendTime.')
    }

    if (topic['retention.ms'] === null) {
        result.set(
            'retention.ms',
            'Retention time cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    } else if (!positiveIntegerWithInfinityRegex.test(topic['retention.ms'].toString(10))) {
        result.set(
            'retention.ms',
            'Retention time cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    }

    if (topic['retention.bytes'] === null) {
        result.set(
            'retention.bytes',
            'Retention bytes cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    } else if (!positiveIntegerWithInfinityRegex.test(topic['retention.bytes'].toString(10))) {
        result.set(
            'retention.bytes',
            'Retention bytes cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    }

    if (topic['max.message.bytes'] === null) {
        result.set('max.message.bytes', 'Maximum message bytes cannot be null, must be numeric and greater than -1.')
    } else if (!positiveIntegerWithZeroRegex.test(topic['max.message.bytes'].toString(10))) {
        result.set('max.message.bytes', 'Maximum message bytes cannot be null, must be numeric and greater than -1.')
    }

    const compressionType = ['producer', 'uncompressed', 'zstd', 'lz4', 'snappy', 'gzip']

    if (topic['compression.type'] === null) {
        result.set('compression.type', 'Compression type cannot be null.')
    } else if (isString(topic['compression.type']) && !compressionType.includes(topic['compression.type'])) {
        result.set('compression.type', `Compression type must be ${compressionType.join(' or ')}.`)
    }

    if (topic['delete.retention.ms'] === null) {
        result.set('delete.retention.ms', 'Delete retention time cannot be null and must be greater than -1.')
    } else if (!positiveIntegerWithZeroRegex.test(topic['delete.retention.ms'].toString(10))) {
        result.set('delete.retention.ms', 'Delete retention time cannot be null and must be greater than -1.')
    }

    if (topic['file.delete.delay.ms'] === null) {
        result.set('file.delete.delay.ms', 'File delete delay time cannot be null, must be greater than -1.')
    } else if (!positiveIntegerWithZeroRegex.test(topic['file.delete.delay.ms'].toString(10))) {
        result.set('file.delete.delay.ms', 'File delete delay time cannot be null, must be greater than -1.')
    }

    if (topic['flush.messages'] === null) {
        result.set('flush.messages', 'Flush messages interval cannot be null, must be integer and greater than 0.')
    } else if (!positiveIntegerRegex.test(topic['flush.messages'].toString(10))) {
        result.set('flush.messages', 'Flush messages interval cannot be null, must be integer and greater than 0.')
    }

    if (topic['flush.ms'] === null) {
        result.set('flush.ms', 'Flush messages time interval cannot be null, must be greater than -1.')
    } else if (!positiveIntegerWithZeroRegex.test(topic['flush.ms'].toString(10))) {
        result.set('flush.ms', 'Flush messages time interval cannot be null, must be greater than -1.')
    }

    if (topic['index.interval.bytes'] === null) {
        result.set('index.interval.bytes', 'Index interval cannot be null, must be greater than -1.')
    } else if (!positiveIntegerWithZeroRegex.test(topic['index.interval.bytes'].toString(10))) {
        result.set('index.interval.bytes', 'Index interval cannot be null, must be greater than -1.')
    }

    if (topic['max.compaction.lag.ms'] === null) {
        result.set('max.compaction.lag.ms', 'Maximum compaction lag cannot be null, must be greater than 0.')
    } else if (!positiveIntegerRegex.test(topic['max.compaction.lag.ms'].toString(10))) {
        result.set('max.compaction.lag.ms', 'Maximum compaction lag cannot be null, must be greater than 0.')
    }

    if (topic['message.timestamp.difference.max.ms'] === null) {
        result.set(
            'message.timestamp.difference.max.ms',
            'Maximum timestamp difference cannot be null, must be greater than -1.'
        )
    } else if (!positiveIntegerWithZeroRegex.test(topic['message.timestamp.difference.max.ms'].toString(10))) {
        result.set(
            'message.timestamp.difference.max.ms',
            'Maximum timestamp difference cannot be null, must be greater than -1.'
        )
    }

    if (topic['min.cleanable.dirty.ratio'] === null) {
        result.set('min.cleanable.dirty.ratio', 'Minimum cleanable ration cannot be null and must be between 0 and 1.')
    } else if (!/^0?(\.)?[0-9]{1,2}$/.test(topic['min.cleanable.dirty.ratio'].toString(10))) {
        result.set(
            'min.cleanable.dirty.ratio',
            // eslint-disable-next-line vue/max-len
            'Minimum cleanable ratio cannot be null must be numeric and between 0 and 1. Only two digits allowed after decimal.'
        )
    } else if (isNumber(topic['min.cleanable.dirty.ratio'])) {
        if (topic['min.cleanable.dirty.ratio'] < 0 || topic['min.cleanable.dirty.ratio'] > 1) {
            result.set(
                'min.cleanable.dirty.ratio',
                'Minimum cleanable ratio cannot be null and must be between 0 and 1.'
            )
        }
    }

    if (topic['min.compaction.lag.ms'] === null) {
        result.set('min.compaction.lag.ms', 'Minimum compaction lag cannot be null, must be greater than -1.')
    } else if (!positiveIntegerWithZeroRegex.test(topic['min.compaction.lag.ms'].toString(10))) {
        result.set('min.compaction.lag.ms', 'Minimum compaction lag cannot be null, must be greater than -1.')
    }

    if (topic['segment.bytes'] === null) {
        result.set('segment.bytes', 'Segment bytes cannot be null, must be greater than 0.')
    } else if (!positiveIntegerRegex.test(topic['segment.bytes'].toString(10))) {
        result.set('segment.bytes', 'Segment bytes cannot be null, must be greater than 0.')
    }

    if (topic['segment.index.bytes'] === null) {
        result.set('segment.index.bytes', 'Segment index bytes cannot be null, must be greater than 0.')
    } else if (!positiveIntegerRegex.test(topic['segment.index.bytes'].toString(10))) {
        result.set('segment.index.bytes', 'Segment index bytes cannot be null, must be greater than 0.')
    }

    if (topic['segment.jitter.ms'] === null) {
        result.set('segment.jitter.ms', 'Segment jitter cannot be null, must be greater than -1.')
    } else if (!positiveIntegerWithZeroRegex.test(topic['segment.jitter.ms'].toString(10))) {
        result.set('segment.jitter.ms', 'Segment jitter cannot be null, must be greater than -1.')
    }

    if (topic['segment.ms'] === null) {
        result.set('segment.ms', 'Segment cannot be null, must be greater than 0.')
    } else if (!positiveIntegerRegex.test(topic['segment.ms'].toString(10))) {
        result.set('segment.ms', 'Segment cannot be null, must be greater than 0.')
    }

    // Check for BigInt overflow since Long is used in backend hence there is limit of 9223372036854775807
    ;[
        'delete.retention.ms',
        'file.delete.delay.ms',
        'flush.messages',
        'flush.ms',
        'max.compaction.lag.ms',
        'message.timestamp.difference.max.ms',
        'min.compaction.lag.ms',
        'retention.bytes',
        'retention.ms',
        'segment.jitter.ms',
        'segment.ms',
    ].forEach((it) => {
        if (!result.has(it)) {
            if (topic[it as keyof NewTopic].toString(10).length > 19) {
                result.set(it, `${it} cannot be more than 9,223,372,036,854,775,807`)
            } else {
                try {
                    const convertedValue = convertToBigInt(topic[it as keyof NewTopic])
                    if (convertedValue > 9223372036854775807n) {
                        result.set(it, `${it} cannot be more than 9,223,372,036,854,775,807`)
                    }
                } catch (e) {
                    result.set(it, `${it} cannot be more than 9,223,372,036,854,775,807. ${e}`)
                }
            }
        }
    })
    // Check for Int overflow
    ;[
        'index.interval.bytes',
        'partition',
        'max.message.bytes',
        'min.insync.replicas',
        'replication.factor',
        'segment.bytes',
    ].forEach((it) => {
        if (!result.has(it)) {
            if (topic[it as keyof NewTopic].toString(10).length > 10) {
                result.set(it, `${it} cannot be more than 2,147,483,647`)
            } else {
                try {
                    const convertedValue = isNumber(topic[it as keyof NewTopic])
                        ? topic[it as keyof NewTopic]
                        : parseInt(topic[it as keyof NewTopic].toString(10), 10)
                    if (convertedValue > 2147483647) {
                        result.set(it, `${it} cannot be more than 2,147,483,647`)
                    }
                } catch (e) {
                    result.set(it, `${it} cannot be more than 2,147,483,647. ${e}`)
                }
            }
        }
    })

    return result
}

export function convertToProperType(topic: NewTopic): NewTopic {
    return {
        'cleanup.policy': topic['cleanup.policy'],
        'compression.type': topic['compression.type'],
        'delete.retention.ms': convertToBigInt(topic['delete.retention.ms']),
        'file.delete.delay.ms': convertToBigInt(topic['file.delete.delay.ms']),
        'flush.messages': convertToBigInt(topic['flush.messages']),
        'flush.ms': convertToBigInt(topic['flush.ms']),
        'follower.replication.throttled.replicas': topic['follower.replication.throttled.replicas'],
        'index.interval.bytes': parseInt(topic['index.interval.bytes'].toString(10), 10),
        'leader.replication.throttled.replicas': topic['leader.replication.throttled.replicas'],
        'max.compaction.lag.ms': convertToBigInt(topic['max.compaction.lag.ms']),
        'max.message.bytes': parseInt(topic['max.message.bytes'].toString(10), 10),
        'message.downconversion.enable': topic['message.downconversion.enable'],
        'message.timestamp.difference.max.ms': convertToBigInt(topic['message.timestamp.difference.max.ms']),
        'message.timestamp.type': topic['message.timestamp.type'],
        'min.cleanable.dirty.ratio': parseFloat(topic['min.cleanable.dirty.ratio'].toString(10)),
        'min.compaction.lag.ms': convertToBigInt(topic['min.compaction.lag.ms']),
        'min.insync.replicas': parseInt(topic['min.insync.replicas'].toString(10), 10),
        'replication.factor': parseInt(topic['replication.factor'].toString(10), 10),
        'retention.bytes': convertToBigInt(topic['retention.bytes']),
        'retention.ms': convertToBigInt(topic['retention.ms']),
        'segment.bytes': parseInt(topic['segment.bytes'].toString(10), 10),
        'segment.index.bytes': parseInt(topic['segment.index.bytes'].toString(10), 10),
        'segment.jitter.ms': convertToBigInt(topic['segment.jitter.ms']),
        'segment.ms': convertToBigInt(topic['segment.ms']),
        'unclean.leader.election.enable': topic['unclean.leader.election.enable'],
        name: topic.name,
        partition: topic.partition,
        preallocate: topic.preallocate,
    }
}
