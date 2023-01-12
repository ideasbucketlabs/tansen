/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
// @ts-nocheck
import { describe, it, expect } from 'vitest'
import { validate, convertToProperType } from '@/validators/NewTopicValidator'
import type { NewTopic } from '../../entity/NewTopic'
import { isBigInt, isNumber } from '../../util/Util'

const validData: NewTopic = {
    'cleanup.policy': 'delete',
    'compression.type': 'producer',
    'delete.retention.ms': 86400000n,
    'file.delete.delay.ms': 60000n,
    'flush.messages': 9223372036854775807n,
    'flush.ms': 9223372036854775807n,
    'follower.replication.throttled.replicas': '',
    'index.interval.bytes': 4096,
    'leader.replication.throttled.replicas': '',
    'max.compaction.lag.ms': 9223372036854775807n,
    'max.message.bytes': 1048588,
    'message.downconversion.enable': true,
    'message.timestamp.difference.max.ms': 9223372036854775807n,
    'message.timestamp.type': 'CreateTime',
    'min.cleanable.dirty.ratio': 0.5,
    'min.compaction.lag.ms': 0n,
    'min.insync.replicas': 2,
    'replication.factor': 3,
    'retention.bytes': -1n,
    'retention.ms': 604800000n,
    'segment.bytes': 1073741824,
    'segment.index.bytes': 10485760,
    'segment.jitter.ms': 0n,
    'segment.ms': 604800000n,
    'unclean.leader.election.enable': false,
    name: 'Test',
    partition: 1,
    preallocate: false,
}
describe('Can validate topic name', () => {
    it('can validate valid data ', () => {
        expect(validate(validData).size).toBe(0)
    })

    it('does not allow empty topic name', () => {
        expect(validate({ ...validData, ...{ name: '' } }).has('name')).toBe(true)
        expect(validate({ ...validData, ...{ name: '   ' } }).has('name')).toBe(true)
        expect(validate({ ...validData, ...{ name: '514151' } }).has('name')).toBe(false)
    })

    it('does not allow invalid topic name that Kafka does not allow', () => {
        expect(validate({ ...validData, ...{ name: '&^&ert' } }).has('name')).toBe(true)
    })
})

describe('Can validate partition', () => {
    it('does not allow null partition', () => {
        expect(validate({ ...validData, ...{ partition: null } }).size).toBe(1)
    })

    it('does not allow negative partition', () => {
        expect(validate({ ...validData, ...{ partition: '-1' } }).size).toBe(1)
    })

    it('does not allow non numeric value', () => {
        expect(validate({ ...validData, ...{ partition: 'test' } }).has('partition')).toBe(true)
    })

    it('does not allow invalid numeric value #1', () => {
        expect(validate({ ...validData, ...{ partition: '-1' } }).get('partition')).toBe(
            'Topic partition cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ partition: '0' } }).get('partition')).toBe(
            'Topic partition cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ partition: -1 } }).get('partition')).toBe(
            'Topic partition cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #4', () => {
        expect(validate({ ...validData, ...{ partition: 0 } }).get('partition')).toBe(
            'Topic partition cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #5', () => {
        expect(validate({ ...validData, ...{ partition: 1.56 } }).get('partition')).toBe(
            'Topic partition cannot be null, must be integer and greater than 0.'
        )
    })

    it('allows valid partition', () => {
        expect(validate(validData).size).toBe(0)
    })
})

describe('Can validate replication factor', () => {
    it('does not allow null replication factor', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': null } }).size).toBe(1)
    })

    it('does not allow negative replication factor', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': '-1' } }).size).toBe(1)
    })

    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': 'test' } }).has('replication.factor')).toBe(true)
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': '-1' } }).get('replication.factor')).toBe(
            'replication.factor cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': '0' } }).get('replication.factor')).toBe(
            'replication.factor cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': '3.434' } }).get('replication.factor')).toBe(
            'replication.factor cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #4', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': 0 } }).get('replication.factor')).toBe(
            'replication.factor cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #5', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': 3.434 } }).get('replication.factor')).toBe(
            'replication.factor cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #6', () => {
        expect(validate({ ...validData, ...{ 'replication.factor': -1 } }).get('replication.factor')).toBe(
            'replication.factor cannot be null, must be integer and greater than 0.'
        )
    })

    it('allows valid replication factor', () => {
        expect(validate(validData).size).toBe(0)
    })
})

describe('Can validate min.insync.replicas', () => {
    it('does not allow negative min.insync.replicas', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': '-1' } }).size).toBe(1)
    })

    it('does not allow null min.insync.replicas', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': null } }).size).toBe(1)
    })

    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': 'test' } }).has('min.insync.replicas')).toBe(true)
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': '-1' } }).get('min.insync.replicas')).toBe(
            'min.insync.replicas cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': '0' } }).get('min.insync.replicas')).toBe(
            'min.insync.replicas cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': '3.434' } }).get('min.insync.replicas')).toBe(
            'min.insync.replicas cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #4', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': 0 } }).get('min.insync.replicas')).toBe(
            'min.insync.replicas cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #5', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': 3.434 } }).get('min.insync.replicas')).toBe(
            'min.insync.replicas cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #6', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': -1 } }).get('min.insync.replicas')).toBe(
            'min.insync.replicas cannot be null, must be integer and greater than 0.'
        )
    })

    it('min.insync.replicas cannot be more than replication factor', () => {
        expect(validate({ ...validData, ...{ 'min.insync.replicas': 10 } }).get('min.insync.replicas')).toBe(
            'min.insync.replicas cannot be more than Replication factor.'
        )
    })

    it('allows valid replication factor', () => {
        expect(validate(validData).size).toBe(0)
    })
})

describe('Can validate clean up policy', () => {
    it('does not allow null value', () => {
        expect(validate({ ...validData, ...{ 'cleanup.policy': null } }).get('cleanup.policy')).toBe(
            'cleanup.policy must be either delete or compact.'
        )
    })

    it('does not allow blank value #1', () => {
        expect(validate({ ...validData, ...{ 'cleanup.policy': '' } }).get('cleanup.policy')).toBe(
            'cleanup.policy must be either delete or compact.'
        )
    })

    it('does not allow blank value #2', () => {
        expect(validate({ ...validData, ...{ 'cleanup.policy': '   ' } }).get('cleanup.policy')).toBe(
            'cleanup.policy must be either delete or compact.'
        )
    })

    it('allows valid clean up policy', () => {
        expect(validate({ ...validData, ...{ 'cleanup.policy': 'compact' } }).has('cleanup.policy')).toBe(false)
    })
})

describe('Can validate message.timestamp.type', () => {
    it('does not allow null value', () => {
        expect(validate({ ...validData, ...{ 'message.timestamp.type': null } }).get('message.timestamp.type')).toBe(
            'message.timestamp.type must be either CreateTime or LogAppendTime.'
        )
    })

    it('does not allow blank value #1', () => {
        expect(validate({ ...validData, ...{ 'message.timestamp.type': '' } }).get('message.timestamp.type')).toBe(
            'message.timestamp.type must be either CreateTime or LogAppendTime.'
        )
    })

    it('does not allow blank value #2', () => {
        expect(validate({ ...validData, ...{ 'message.timestamp.type': '   ' } }).get('message.timestamp.type')).toBe(
            'message.timestamp.type must be either CreateTime or LogAppendTime.'
        )
    })

    it('allows valid clean up policy', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.type': 'LogAppendTime' } }).has('message.timestamp.type')
        ).toBe(false)
    })
})

describe('Can validate retention ms', () => {
    it('does not allow non numeric value', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': 'test' } }).has('retention.ms')).toBe(true)
    })

    it('does not allow null value', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': null } }).has('retention.ms')).toBe(true)
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': '0' } }).get('retention.ms')).toBe(
            'Retention time cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': 0 } }).get('retention.ms')).toBe(
            'Retention time cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': 1.56 } }).get('retention.ms')).toBe(
            'Retention time cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    })

    it('allows valid retention.ms', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': '-1' } }).has('retention.ms')).toBe(false)
    })

    it('allows valid retention.ms', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': '1' } }).has('retention.ms')).toBe(false)
    })

    it('does not allow 0 retention.ms #1', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': '0' } }).has('retention.ms')).toBe(true)
    })

    it('does not allow 0 retention.ms #2', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': 0 } }).has('retention.ms')).toBe(true)
    })

    it('does not allow negative retention.ms other than -1 #1', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': '-2' } }).has('retention.ms')).toBe(true)
    })

    it('does not allow negative retention.ms other than -1 #2', () => {
        expect(validate({ ...validData, ...{ 'retention.ms': -2 } }).has('retention.ms')).toBe(true)
    })
})

describe('Can validate retention bytes', () => {
    it('does not allow non numeric value', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': 'test' } }).has('retention.bytes')).toBe(true)
    })

    it('does not allow null value', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': null } }).has('retention.bytes')).toBe(true)
    })

    it('does not allow blank value', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': '' } }).has('retention.bytes')).toBe(true)
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': '0' } }).get('retention.bytes')).toBe(
            'Retention bytes cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': 0 } }).get('retention.bytes')).toBe(
            'Retention bytes cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': 1.56 } }).get('retention.bytes')).toBe(
            'Retention bytes cannot be null, must be numeric either exactly -1 or greater than 0.'
        )
    })

    it('allows valid retention.bytes', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': '-1' } }).has('retention.bytes')).toBe(false)
    })

    it('allows valid retention.bytes', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': '1' } }).has('retention.bytes')).toBe(false)
    })

    it('does not allow 0 retention.bytes #1', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': '0' } }).has('retention.bytes')).toBe(true)
    })

    it('does not allow 0 retention.bytes #2', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': 0 } }).has('retention.bytes')).toBe(true)
    })

    it('does not allow negative retention.bytes other than -1 #1', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': '-2' } }).has('retention.bytes')).toBe(true)
    })

    it('does not allow negative retention.bytes other than -1 #2', () => {
        expect(validate({ ...validData, ...{ 'retention.bytes': -2 } }).has('retention.bytes')).toBe(true)
    })
})

describe('Can validate max.message.bytes', () => {
    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': 'test' } }).has('max.message.bytes')).toBe(true)
    })

    it('does not allow null value', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': null } }).has('max.message.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #1', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': '-1' } }).has('max.message.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #2', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': -1 } }).has('max.message.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': '-2' } }).has('max.message.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #4', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': -2 } }).has('max.message.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': 1.5 } }).has('max.message.bytes')).toBe(true)
    })

    it('allows valid max.message.bytes', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': '2323' } }).has('max.message.bytes')).toBe(false)
    })

    it('allows valid max.message.bytes', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': '1' } }).has('max.message.bytes')).toBe(false)
    })

    it('does allow 0 max.message.bytes #1', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': '0' } }).has('max.message.bytes')).toBe(false)
    })

    it('does allow 0 max.message.bytes #2', () => {
        expect(validate({ ...validData, ...{ 'max.message.bytes': 0 } }).has('max.message.bytes')).toBe(false)
    })
})

describe('Can validate compression.type', () => {
    it('does not allow null value', () => {
        expect(validate({ ...validData, ...{ 'compression.type': null } }).get('compression.type')).toBe(
            'Compression type cannot be null.'
        )
    })

    it('does not allow blank value #1', () => {
        expect(validate({ ...validData, ...{ 'compression.type': '' } }).get('compression.type')).toBe(
            'Compression type must be producer or uncompressed or zstd or lz4 or snappy or gzip.'
        )
    })

    it('does not allow blank value #2', () => {
        expect(validate({ ...validData, ...{ 'compression.type': '   ' } }).get('compression.type')).toBe(
            'Compression type must be producer or uncompressed or zstd or lz4 or snappy or gzip.'
        )
    })

    it('allows valid compression.type #1', () => {
        expect(validate({ ...validData, ...{ 'compression.type': 'lz4' } }).has('compression.type')).toBe(false)
    })

    it('allows valid compression.type #2', () => {
        expect(validate({ ...validData, ...{ 'compression.type': 'uncompressed' } }).has('compression.type')).toBe(
            false
        )
    })

    it('allows valid compression.type #3', () => {
        expect(validate({ ...validData, ...{ 'compression.type': 'snappy' } }).has('compression.type')).toBe(false)
    })

    it('allows valid compression.type #4', () => {
        expect(validate({ ...validData, ...{ 'compression.type': 'gzip' } }).has('compression.type')).toBe(false)
    })
})

describe('Can validate delete.retention.ms', () => {
    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': 'test' } }).has('delete.retention.ms')).toBe(true)
    })

    it('does not allow value less than 0 #1', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': '-1' } }).get('delete.retention.ms')).toBe(
            'Delete retention time cannot be null and must be greater than -1.'
        )
    })

    it('does not allow value less than 0 #2', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': -1 } }).has('delete.retention.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': '-2' } }).has('delete.retention.ms')).toBe(true)
    })

    it('does not allow value less than 0 #4', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': -2 } }).has('delete.retention.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': 1.5 } }).has('delete.retention.ms')).toBe(true)
    })

    it('allows valid delete.retention.ms', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': '2323' } }).has('delete.retention.ms')).toBe(false)
    })

    it('allows valid delete.retention.ms', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': '1' } }).has('delete.retention.ms')).toBe(false)
    })

    it('does allow 0 delete.retention.ms #1', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': '0' } }).has('delete.retention.ms')).toBe(false)
    })

    it('does allow 0 delete.retention.ms #2', () => {
        expect(validate({ ...validData, ...{ 'delete.retention.ms': 0 } }).has('delete.retention.ms')).toBe(false)
    })
})

describe('Can validate file.delete.delay.ms', () => {
    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': 'test' } }).has('file.delete.delay.ms')).toBe(true)
    })

    it('does not allow value less than 0 #1', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': '-1' } }).get('file.delete.delay.ms')).toBe(
            'File delete delay time cannot be null, must be greater than -1.'
        )
    })

    it('does not allow value less than 0 #2', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': -1 } }).has('file.delete.delay.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': '-2' } }).has('file.delete.delay.ms')).toBe(true)
    })

    it('does not allow value less than 0 #4', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': -2 } }).has('file.delete.delay.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': 1.5 } }).has('file.delete.delay.ms')).toBe(true)
    })

    it('allows valid file.delete.delay.ms', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': '1' } }).has('file.delete.delay.ms')).toBe(false)
    })

    it('does allow 0 file.delete.delay.ms #1', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': '0' } }).has('file.delete.delay.ms')).toBe(false)
    })

    it('does allow 0 file.delete.delay.ms #2', () => {
        expect(validate({ ...validData, ...{ 'file.delete.delay.ms': 0 } }).has('file.delete.delay.ms')).toBe(false)
    })
})

describe('Can validate flush.messages', () => {
    it('does not allow negative replication factor', () => {
        expect(validate({ ...validData, ...{ 'flush.messages': '-1' } }).size).toBe(1)
    })

    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'flush.messages': 'test' } }).has('flush.messages')).toBe(true)
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'flush.messages': '-1' } }).get('flush.messages')).toBe(
            'Flush messages interval cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'flush.messages': '0' } }).get('flush.messages')).toBe(
            'Flush messages interval cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'flush.messages': '3.434' } }).get('flush.messages')).toBe(
            'Flush messages interval cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #4', () => {
        expect(validate({ ...validData, ...{ 'flush.messages': 0 } }).get('flush.messages')).toBe(
            'Flush messages interval cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #5', () => {
        expect(validate({ ...validData, ...{ 'flush.messages': 3.434 } }).get('flush.messages')).toBe(
            'Flush messages interval cannot be null, must be integer and greater than 0.'
        )
    })

    it('does not allow invalid integer value #6', () => {
        expect(validate({ ...validData, ...{ 'flush.messages': -1 } }).get('flush.messages')).toBe(
            'Flush messages interval cannot be null, must be integer and greater than 0.'
        )
    })

    it('allows valid flush.messages', () => {
        expect(validate(validData).size).toBe(0)
    })
})

describe('Can validate max.compaction.lag.ms', () => {
    it('does not allow negative replication factor', () => {
        expect(validate({ ...validData, ...{ 'max.compaction.lag.ms': '-1' } }).size).toBe(1)
    })

    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'max.compaction.lag.ms': 'test' } }).has('max.compaction.lag.ms')).toBe(
            true
        )
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'max.compaction.lag.ms': '-1' } }).get('max.compaction.lag.ms')).toBe(
            'Maximum compaction lag cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'max.compaction.lag.ms': '0' } }).get('max.compaction.lag.ms')).toBe(
            'Maximum compaction lag cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'max.compaction.lag.ms': '3.434' } }).get('max.compaction.lag.ms')).toBe(
            'Maximum compaction lag cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #4', () => {
        expect(validate({ ...validData, ...{ 'max.compaction.lag.ms': 0 } }).get('max.compaction.lag.ms')).toBe(
            'Maximum compaction lag cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #5', () => {
        expect(validate({ ...validData, ...{ 'max.compaction.lag.ms': 3.434 } }).get('max.compaction.lag.ms')).toBe(
            'Maximum compaction lag cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #6', () => {
        expect(validate({ ...validData, ...{ 'max.compaction.lag.ms': -1 } }).get('max.compaction.lag.ms')).toBe(
            'Maximum compaction lag cannot be null, must be greater than 0.'
        )
    })

    it('allows valid flush.messages', () => {
        expect(validate(validData).size).toBe(0)
    })
})

describe('Can validate flush.ms', () => {
    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': 'test' } }).has('flush.ms')).toBe(true)
    })

    it('does not allow value less than 0 #1', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': '-1' } }).get('flush.ms')).toBe(
            'Flush messages time interval cannot be null, must be greater than -1.'
        )
    })

    it('does not allow value less than 0 #2', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': -1 } }).has('flush.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': '-2' } }).has('flush.ms')).toBe(true)
    })

    it('does not allow value less than 0 #4', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': -2 } }).has('flush.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': 1.5 } }).has('flush.ms')).toBe(true)
    })

    it('allows valid flush.ms', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': '2323' } }).has('flush.ms')).toBe(false)
    })

    it('allows valid flush.ms', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': '1' } }).has('flush.ms')).toBe(false)
    })

    it('does allow 0 flush.ms #1', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': '0' } }).has('flush.ms')).toBe(false)
    })

    it('does allow 0 flush.ms #2', () => {
        expect(validate({ ...validData, ...{ 'flush.ms': 0 } }).has('flush.ms')).toBe(false)
    })
})

describe('Can validate index.interval.bytes', () => {
    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': 'test' } }).has('index.interval.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #1', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': '-1' } }).get('index.interval.bytes')).toBe(
            'Index interval cannot be null, must be greater than -1.'
        )
    })

    it('does not allow value less than 0 #2', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': -1 } }).has('index.interval.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': '-2' } }).has('index.interval.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #4', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': -2 } }).has('index.interval.bytes')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': 1.5 } }).has('index.interval.bytes')).toBe(true)
    })

    it('allows valid index.interval.bytes', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': '2323' } }).has('index.interval.bytes')).toBe(
            false
        )
    })

    it('allows valid index.interval.bytes', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': '1' } }).has('index.interval.bytes')).toBe(false)
    })

    it('does allow 0 index.interval.bytes #1', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': '0' } }).has('index.interval.bytes')).toBe(false)
    })

    it('does allow 0 index.interval.bytes #2', () => {
        expect(validate({ ...validData, ...{ 'index.interval.bytes': 0 } }).has('index.interval.bytes')).toBe(false)
    })
})

describe('Can validate message.timestamp.difference.max.ms', () => {
    it('does not allow non integer value', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': 'test' } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(true)
    })

    it('does not allow value less than 0 #1', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': '-1' } }).get(
                'message.timestamp.difference.max.ms'
            )
        ).toBe('Maximum timestamp difference cannot be null, must be greater than -1.')
    })

    it('does not allow value less than 0 #2', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': -1 } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': '-2' } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(true)
    })

    it('does not allow value less than 0 #4', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': -2 } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': 1.5 } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(true)
    })

    it('allows valid message.timestamp.difference.max.ms', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': '2323' } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(false)
    })

    it('allows valid message.timestamp.difference.max.ms', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': '1' } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(false)
    })

    it('does allow 0 message.timestamp.difference.max.ms #1', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': '0' } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(false)
    })

    it('does allow 0 message.timestamp.difference.max.ms #2', () => {
        expect(
            validate({ ...validData, ...{ 'message.timestamp.difference.max.ms': 0 } }).has(
                'message.timestamp.difference.max.ms'
            )
        ).toBe(false)
    })
})

describe('Can validate min.cleanable.dirty.ratio', () => {
    it('does not allow non numeric value', () => {
        expect(
            validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': 'test' } }).has('min.cleanable.dirty.ratio')
        ).toBe(true)
    })

    it('does not allow numeric less than 0 #1', () => {
        expect(
            validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': '-1' } }).has('min.cleanable.dirty.ratio')
        ).toBe(true)
    })

    it('does not allow numeric less than 0 #1', () => {
        expect(
            validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': -1 } }).has('min.cleanable.dirty.ratio')
        ).toBe(true)
    })

    it('does not allow numeric more than 100 #1', () => {
        expect(
            validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': '101' } }).has('min.cleanable.dirty.ratio')
        ).toBe(true)
    })

    it('does not allow numeric more than 1 #1', () => {
        expect(validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': 2 } }).has('min.cleanable.dirty.ratio')).toBe(
            true
        )
    })

    it('does not allow more than 2 digits after decimal', () => {
        expect(
            validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': 0.567 } }).has('min.cleanable.dirty.ratio')
        ).toBe(true)
    })

    it('does allow 0', () => {
        expect(validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': 0 } }).has('min.cleanable.dirty.ratio')).toBe(
            false
        )
    })

    it('does allow 1', () => {
        expect(validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': 1 } }).has('min.cleanable.dirty.ratio')).toBe(
            false
        )
    })

    it('does not allow 1.5', () => {
        expect(
            validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': '1.5' } }).has('min.cleanable.dirty.ratio')
        ).toBe(true)
    })

    it('gives valid message', () => {
        expect(
            validate({ ...validData, ...{ 'min.cleanable.dirty.ratio': '2.5' } }).get('min.cleanable.dirty.ratio')
        ).toBe(
            // eslint-disable-next-line vue/max-len
            'Minimum cleanable ratio cannot be null must be numeric and between 0 and 1. Only two digits allowed after decimal.'
        )
    })
})

describe('Can validate min.compaction.lag.ms', () => {
    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': 'test' } }).has('min.compaction.lag.ms')).toBe(
            true
        )
    })

    it('does not allow value less than 0 #1', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': '-1' } }).get('min.compaction.lag.ms')).toBe(
            'Minimum compaction lag cannot be null, must be greater than -1.'
        )
    })

    it('does not allow value less than 0 #2', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': -1 } }).has('min.compaction.lag.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': '-2' } }).has('min.compaction.lag.ms')).toBe(true)
    })

    it('does not allow value less than 0 #4', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': -2 } }).has('min.compaction.lag.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': 1.5 } }).has('min.compaction.lag.ms')).toBe(true)
    })

    it('allows valid min.compaction.lag.ms', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': '2323' } }).has('min.compaction.lag.ms')).toBe(
            false
        )
    })

    it('allows valid min.compaction.lag.ms', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': '1' } }).has('min.compaction.lag.ms')).toBe(false)
    })

    it('does allow 0 min.compaction.lag.ms #1', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': '0' } }).has('min.compaction.lag.ms')).toBe(false)
    })

    it('does allow 0 min.compaction.lag.ms #2', () => {
        expect(validate({ ...validData, ...{ 'min.compaction.lag.ms': 0 } }).has('min.compaction.lag.ms')).toBe(false)
    })
})

describe('Can validate segment.bytes', () => {
    it('does not allow negative replication factor', () => {
        expect(validate({ ...validData, ...{ 'segment.bytes': '-1' } }).size).toBe(1)
    })

    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'segment.bytes': 'test' } }).has('segment.bytes')).toBe(true)
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'segment.bytes': '-1' } }).get('segment.bytes')).toBe(
            'Segment bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'segment.bytes': '0' } }).get('segment.bytes')).toBe(
            'Segment bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'segment.bytes': '3.434' } }).get('segment.bytes')).toBe(
            'Segment bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #4', () => {
        expect(validate({ ...validData, ...{ 'segment.bytes': 0 } }).get('segment.bytes')).toBe(
            'Segment bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #5', () => {
        expect(validate({ ...validData, ...{ 'segment.bytes': 3.434 } }).get('segment.bytes')).toBe(
            'Segment bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #6', () => {
        expect(validate({ ...validData, ...{ 'segment.bytes': -1 } }).get('segment.bytes')).toBe(
            'Segment bytes cannot be null, must be greater than 0.'
        )
    })

    it('allows valid flush.messages', () => {
        expect(validate(validData).size).toBe(0)
    })
})

describe('Can validate segment.index.bytes', () => {
    it('does not allow negative replication factor', () => {
        expect(validate({ ...validData, ...{ 'segment.index.bytes': '-1' } }).size).toBe(1)
    })

    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'segment.index.bytes': 'test' } }).has('segment.index.bytes')).toBe(true)
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'segment.index.bytes': '-1' } }).get('segment.index.bytes')).toBe(
            'Segment index bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'segment.index.bytes': '0' } }).get('segment.index.bytes')).toBe(
            'Segment index bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'segment.index.bytes': '3.434' } }).get('segment.index.bytes')).toBe(
            'Segment index bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #4', () => {
        expect(validate({ ...validData, ...{ 'segment.index.bytes': 0 } }).get('segment.index.bytes')).toBe(
            'Segment index bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #5', () => {
        expect(validate({ ...validData, ...{ 'segment.index.bytes': 3.434 } }).get('segment.index.bytes')).toBe(
            'Segment index bytes cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #6', () => {
        expect(validate({ ...validData, ...{ 'segment.index.bytes': -1 } }).get('segment.index.bytes')).toBe(
            'Segment index bytes cannot be null, must be greater than 0.'
        )
    })

    it('allows valid flush.messages', () => {
        expect(validate(validData).size).toBe(0)
    })
})

describe('Can validate segment.ms', () => {
    it('does not allow negative replication factor', () => {
        expect(validate({ ...validData, ...{ 'segment.ms': '-1' } }).size).toBe(1)
    })

    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'segment.ms': 'test' } }).has('segment.ms')).toBe(true)
    })

    it('does not allow invalid integer value #1', () => {
        expect(validate({ ...validData, ...{ 'segment.ms': '-1' } }).get('segment.ms')).toBe(
            'Segment cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #2', () => {
        expect(validate({ ...validData, ...{ 'segment.ms': '0' } }).get('segment.ms')).toBe(
            'Segment cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #3', () => {
        expect(validate({ ...validData, ...{ 'segment.ms': '3.434' } }).get('segment.ms')).toBe(
            'Segment cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #4', () => {
        expect(validate({ ...validData, ...{ 'segment.ms': 0 } }).get('segment.ms')).toBe(
            'Segment cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #5', () => {
        expect(validate({ ...validData, ...{ 'segment.ms': 3.434 } }).get('segment.ms')).toBe(
            'Segment cannot be null, must be greater than 0.'
        )
    })

    it('does not allow invalid integer value #6', () => {
        expect(validate({ ...validData, ...{ 'segment.ms': -1 } }).get('segment.ms')).toBe(
            'Segment cannot be null, must be greater than 0.'
        )
    })

    it('allows valid flush.messages', () => {
        expect(validate(validData).size).toBe(0)
    })
})

describe('Can validate segment.jitter.ms', () => {
    it('does not allow non integer value', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': 'test' } }).has('segment.jitter.ms')).toBe(true)
    })

    it('does not allow null value', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': null } }).has('segment.jitter.ms')).toBe(true)
    })

    it('does not allow blank value', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': '' } }).has('segment.jitter.ms')).toBe(true)
    })

    it('does not allow value less than 0 #1', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': '-1' } }).get('segment.jitter.ms')).toBe(
            'Segment jitter cannot be null, must be greater than -1.'
        )
    })

    it('does not allow value less than 0 #2', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': -1 } }).has('segment.jitter.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': '-2' } }).has('segment.jitter.ms')).toBe(true)
    })

    it('does not allow value less than 0 #4', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': -2 } }).has('segment.jitter.ms')).toBe(true)
    })

    it('does not allow value less than 0 #3', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': 1.5 } }).has('segment.jitter.ms')).toBe(true)
    })

    it('allows valid segment.jitter.ms', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': '2323' } }).has('segment.jitter.ms')).toBe(false)
    })

    it('allows valid segment.jitter.ms', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': '1' } }).has('segment.jitter.ms')).toBe(false)
    })

    it('does allow 0 segment.jitter.ms #1', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': '0' } }).has('segment.jitter.ms')).toBe(false)
    })

    it('does allow 0 segment.jitter.ms #2', () => {
        expect(validate({ ...validData, ...{ 'segment.jitter.ms': 0 } }).has('segment.jitter.ms')).toBe(false)
    })
})

describe('Validate all null values', () => {
    it('does not allow null values', () => {
        expect(
            validate({
                'cleanup.policy': null,
                'compression.type': null,
                'delete.retention.ms': null,
                'file.delete.delay.ms': null,
                'flush.messages': null,
                'flush.ms': null,
                'follower.replication.throttled.replicas': null,
                'index.interval.bytes': null,
                'leader.replication.throttled.replicas': null,
                'max.compaction.lag.ms': null,
                'max.message.bytes': null,
                'message.downconversion.enable': null,
                'message.timestamp.difference.max.ms': null,
                'message.timestamp.type': null,
                'min.cleanable.dirty.ratio': null,
                'min.compaction.lag.ms': null,
                'min.insync.replicas': null,
                'replication.factor': null,
                'retention.bytes': null,
                'retention.ms': null,
                'segment.bytes': null,
                'segment.index.bytes': null,
                'segment.jitter.ms': null,
                'segment.ms': null,
                'unclean.leader.election.enable': null,
                name: null,
                partition: null,
                preallocate: null,
            }).size
        ).toBe(23)
    })
})

describe('Validates all overflow values', () => {
    it('does not allow long overflow values #1', () => {
        expect(
            validate({
                ...validData,
                ...{
                    'delete.retention.ms': 9223372036854775808n,
                    'file.delete.delay.ms': 9223372036854775808n,
                    'flush.messages': 9223372036854775808n,
                    'flush.ms': 9223372036854775808n,
                    'max.compaction.lag.ms': 9223372036854775808n,
                    'message.timestamp.difference.max.ms': 9223372036854775808n,
                    'min.compaction.lag.ms': 9223372036854775808n,
                    'retention.bytes': 9223372036854775808n,
                    'retention.ms': 9223372036854775808n,
                    'segment.jitter.ms': 9223372036854775808n,
                    'segment.ms': 9223372036854775808n,
                },
            }).size
        ).toBe(11)
    })

    it('does not allow long overflow values #2', () => {
        expect(
            validate({
                ...validData,
                ...{
                    'retention.ms': 92233720368547758089n,
                },
            }).get('retention.ms')
        ).toBe('retention.ms cannot be more than 9,223,372,036,854,775,807')
    })

    it('does not allow long overflow values #3', () => {
        expect(
            validate({
                ...validData,
                ...{
                    'delete.retention.ms': 9223372036854775808n,
                },
            }).get('delete.retention.ms')
        ).toBe('delete.retention.ms cannot be more than 9,223,372,036,854,775,807')
    })

    it('does not allow integer overflow values #1', () => {
        expect(
            validate({
                ...validData,
                ...{
                    'index.interval.bytes': 2147483648,
                    partition: 2147483648,
                    'min.insync.replicas': 2147483648,
                    'replication.factor': 2147483648,
                    'segment.bytes': 2147483648,
                },
            }).size
        ).toBe(5)
    })

    it('does not allow integer overflow values #2', () => {
        expect(
            validate({
                ...validData,
                ...{
                    'index.interval.bytes': 2147483648,
                },
            }).get('index.interval.bytes')
        ).toBe('index.interval.bytes cannot be more than 2,147,483,647')
    })

    it('does not allow integer overflow values #2', () => {
        expect(
            validate({
                ...validData,
                ...{
                    'index.interval.bytes': 21474836480,
                },
            }).get('index.interval.bytes')
        ).toBe('index.interval.bytes cannot be more than 2,147,483,647')
    })
})

describe('Can convert values to proper type', () => {
    it('Can convert to proper type', () => {
        const data1 = convertToProperType({
            'cleanup.policy': 'delete',
            'compression.type': 'producer',
            'delete.retention.ms': '86400000',
            'file.delete.delay.ms': '60000',
            'flush.messages': '9223372036854775807',
            'flush.ms': '9223372036854775807',
            'follower.replication.throttled.replicas': '',
            'index.interval.bytes': 4096,
            'leader.replication.throttled.replicas': '',
            'max.compaction.lag.ms': '9223372036854775807',
            'max.message.bytes': 1048588,
            'message.downconversion.enable': true,
            'message.timestamp.difference.max.ms': '9223372036854775807',
            'message.timestamp.type': 'CreateTime',
            'min.cleanable.dirty.ratio': 0.5,
            'min.compaction.lag.ms': '0',
            'min.insync.replicas': '2',
            'replication.factor': 3,
            'retention.bytes': '-1',
            'retention.ms': '604800000',
            'segment.bytes': 1073741824,
            'segment.index.bytes': 10485760,
            'segment.jitter.ms': '0',
            'segment.ms': '604800000',
            'unclean.leader.election.enable': false,
            name: 'Test',
            partition: 1,
            preallocate: false,
        })

        const data2 = convertToProperType({
            'cleanup.policy': 'delete',
            'compression.type': 'producer',
            'delete.retention.ms': '86400000',
            'file.delete.delay.ms': '60000',
            'flush.messages': '9223372036854775807',
            'flush.ms': '9223372036854775807',
            'follower.replication.throttled.replicas': '',
            'index.interval.bytes': 4096,
            'leader.replication.throttled.replicas': '',
            'max.compaction.lag.ms': '9223372036854775807',
            'max.message.bytes': 1048588,
            'message.downconversion.enable': true,
            'message.timestamp.difference.max.ms': '9223372036854775807',
            'message.timestamp.type': 'CreateTime',
            'min.cleanable.dirty.ratio': 0.5,
            'min.compaction.lag.ms': '0',
            'min.insync.replicas': '2',
            'replication.factor': 3,
            'retention.bytes': '-1',
            'retention.ms': 604800000,
            'segment.bytes': 1073741824,
            'segment.index.bytes': 10485760,
            'segment.jitter.ms': '0',
            'segment.ms': '604800000',
            'unclean.leader.election.enable': false,
            name: 'Test',
            partition: 1,
            preallocate: false,
        })

        const data3 = convertToProperType({
            'cleanup.policy': 'delete',
            'compression.type': 'producer',
            'delete.retention.ms': '86400000',
            'file.delete.delay.ms': '60000',
            'flush.messages': '9223372036854775807',
            'flush.ms': '9223372036854775807',
            'follower.replication.throttled.replicas': '',
            'index.interval.bytes': 4096,
            'leader.replication.throttled.replicas': '',
            'max.compaction.lag.ms': '9223372036854775807',
            'max.message.bytes': 1048588,
            'message.downconversion.enable': true,
            'message.timestamp.difference.max.ms': '9223372036854775807',
            'message.timestamp.type': 'CreateTime',
            'min.cleanable.dirty.ratio': 0.5,
            'min.compaction.lag.ms': '0',
            'min.insync.replicas': '2',
            'replication.factor': 3,
            'retention.bytes': '-1',
            'retention.ms': 604800000n,
            'segment.bytes': 1073741824,
            'segment.index.bytes': 10485760,
            'segment.jitter.ms': '0',
            'segment.ms': '604800000',
            'unclean.leader.election.enable': false,
            name: 'Test',
            partition: 1,
            preallocate: false,
        })

        expect(isBigInt(data1['message.timestamp.difference.max.ms'])).toBe(true)
        expect(isNumber(data1['min.insync.replicas'])).toBe(true)
        expect(isBigInt(data1['retention.bytes'])).toBe(true)
        expect(isBigInt(data2['retention.ms'])).toBe(true)
        expect(isBigInt(data3['retention.ms'])).toBe(true)
    })
})
