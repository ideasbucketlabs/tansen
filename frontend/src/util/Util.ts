/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import drop from 'lodash.drop'
import filter from 'lodash.filter'
import orderBy from 'lodash.orderby'
import type { Filter } from '@/entity/Filter'
import type { Sorter } from '@/entity/Sorter'
import type { PagedData } from '@/entity/PagedData'
import { saveAs } from 'file-saver'

let id = 1
const regex = /^(?<month>\d{2})\/(?<day>\d{2})\/(?<year>\d{4}) (?<hours>\d{2}):(?<minutes>\d{2}):(?<seconds>\d{2})$/

export function getPaginateItems<T>(items: T[], currentPage = 1, pageSize = 50): PagedData<T> {
    const offset = (currentPage - 1) * pageSize
    const pagedItems = drop(items, offset).slice(0, pageSize)

    return {
        currentPage,
        pageSize,
        total: items.length,
        totalPages: Math.ceil(items.length / pageSize),
        data: pagedItems,
    }
}

export function getAppliedColumnFilter(columnName: string, appliedFilters?: Filter[]): Filter[] {
    if (appliedFilters === undefined || appliedFilters === null || appliedFilters.length === 0) {
        return []
    }

    const filterApplied = []

    for (const individualFilter of appliedFilters) {
        // Unlike sorter column can have multiple filters.
        if (individualFilter.property === columnName) {
            filterApplied.push(individualFilter)
        }
    }

    return filterApplied
}

export function isColumnSorted(columnName: string, appliedSorters?: Sorter[]): Sorter | null {
    if (appliedSorters === undefined || appliedSorters === null || appliedSorters.length === 0) {
        return null
    }

    let sorter = null

    for (const individualSorter of appliedSorters) {
        if (individualSorter.property === columnName) {
            sorter = individualSorter
            break
        }
    }

    return sorter
}

export function sortData<T>(items: T[], sorters?: Sorter[]): T[] {
    if (sorters === undefined || sorters === null || sorters.length === 0) {
        return items
    }

    const fieldsToSort = sorters.map((sorter) => sorter.property)
    const directionsToSort = sorters.map((sorter) => sorter.direction)

    return orderBy(items, fieldsToSort, directionsToSort) as T[]
}

export function downloadFile(content: string, fileName: string, type: 'AVRO' | 'JSON' | 'PROTOBUF'): void {
    let contentType = ''
    let extension = ''

    if (type === 'AVRO') {
        contentType = 'application/octet-stream;charset=utf-8'
        extension = '.avsc'
    } else if (type === 'PROTOBUF') {
        contentType = 'application/x-protobuf;charset=utf-8'
        extension = '.proto'
    } else {
        contentType = 'application/json;charset=utf-8'
        extension = '.json'
    }

    const blob = new Blob([content], {
        type: contentType,
    })

    saveAs(blob, fileName + extension)
}

export function getFilterData<T>(items: T[], filters: Filter[]): T[] {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    return filter(items, (record: any) => {
        if (filters.length === 0) {
            return true
        }

        let result = 0
        let filterRun = 0

        type ObjectKey = keyof typeof record

        filters.forEach((individualFilter) => {
            const valueToFilter = record[individualFilter.property as ObjectKey]

            if (valueToFilter === null) {
                result += 0
            } else if (individualFilter.type === 'string' && typeof valueToFilter === 'string') {
                const givenValue = (individualFilter.value as string).toLowerCase()

                switch (individualFilter.operator) {
                    case 'ct':
                        result += valueToFilter.toLowerCase().includes(givenValue) ? 1 : 0
                        break
                    case 'sw':
                        result += valueToFilter.toLowerCase().startsWith(givenValue) ? 1 : 0
                        break
                    case 'eq':
                        result += valueToFilter.toLowerCase() === givenValue ? 1 : 0
                        break
                    case 'ew':
                        result += valueToFilter.toLowerCase().endsWith(givenValue) ? 1 : 0
                        break
                }
            } else if (
                individualFilter.type === 'number' &&
                typeof valueToFilter === 'number' &&
                Number.isFinite(valueToFilter)
            ) {
                const givenValue = individualFilter.value as number

                switch (individualFilter.operator) {
                    case 'gt':
                        result += valueToFilter > givenValue ? 1 : 0
                        break
                    case 'lt':
                        result += valueToFilter < givenValue ? 1 : 0
                        break
                    case 'eq':
                        result += valueToFilter === givenValue ? 1 : 0
                        break
                    case 'lte':
                        result += valueToFilter <= givenValue ? 1 : 0
                        break
                    case 'gte':
                        result += valueToFilter >= givenValue ? 1 : 0
                        break
                }
            } else if (individualFilter.type === 'boolean' && typeof valueToFilter === 'boolean') {
                switch (individualFilter.operator) {
                    case 'eq':
                        result += valueToFilter === (individualFilter.value as boolean) ? 1 : 0
                        break
                }
            }

            filterRun += 1
        })

        return result === filterRun
    })
}

export function EMPTY_FN() {
    // EMPTY
}

export function slugify(text: string | null = null) {
    if (text === null) {
        return text
    }

    return text
        .toString()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase()
        .trim()
        .replace(/\s+/g, '-')
        .replace(/[^\w-]+/g, '')
        .replace(/--+/g, '-')
}

export function getId(): string {
    id += 1
    return `tansen-ui-${id}`
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function isInteger(value: any): boolean {
    return !isNaN(value) && ((x) => (x | 0) === x)(parseFloat(value))
}

export function getTimestamp(): number {
    return Math.round(new Date().getTime() / 1000)
}

export function parseDate(input: string): Date | null {
    const match = regex.exec(input)

    if (match === null) {
        return null
    }

    if (!Object.prototype.hasOwnProperty.call(match, 'groups')) {
        return null
    }

    if (!Object.prototype.hasOwnProperty.call(match.groups, 'year')) {
        return null
    }

    if (!Object.prototype.hasOwnProperty.call(match.groups, 'month')) {
        return null
    }

    if (!Object.prototype.hasOwnProperty.call(match.groups, 'day')) {
        return null
    }

    if (!Object.prototype.hasOwnProperty.call(match.groups, 'hours')) {
        return null
    }

    if (!Object.prototype.hasOwnProperty.call(match.groups, 'minutes')) {
        return null
    }

    if (!Object.prototype.hasOwnProperty.call(match.groups, 'seconds')) {
        return null
    }

    const tz = new Date(`${match.groups?.year}-${match.groups?.month}-${match.groups?.day}`)
    if (tz.toString() === 'Invalid Date') {
        return null
    }
    const tzo = -tz.getTimezoneOffset()
    const dif = tzo >= 0 ? '+' : '-'
    const pad = function (num: number) {
        return (num < 10 ? '0' : '') + num
    }

    const parsedDate = new Date(
        // eslint-disable-next-line max-len
        `${match.groups?.year}-${match.groups?.month}-${match.groups?.day}T${match.groups?.hours}:${
            match.groups?.minutes
        }:${match.groups?.seconds}.000${dif}${pad(Math.floor(Math.abs(tzo) / 60))}:${pad(Math.abs(tzo) % 60)}`
    )

    if (parsedDate.toString() === 'Invalid Date') {
        return null
    }

    return parsedDate
}

export function flatten(data: unknown): Record<string, string | boolean | number | Date> {
    const result = {} as Record<string, string | boolean | number | object | Date>

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    function recurse(cur: any, prop: string) {
        if (Object(cur) !== cur) {
            result[prop] = cur
        } else if (Array.isArray(cur)) {
            const currentStackSize = cur.length
            for (let i = 0; i < currentStackSize; i++) {
                recurse(cur[i], prop + '[' + i + ']')
            }
            if (currentStackSize == 0) {
                result[prop] = []
            }
        } else {
            let isEmpty = true
            for (const p in cur) {
                isEmpty = false
                recurse(cur[p], prop ? prop + '.' + p : p)
            }
            if (isEmpty) {
                result[prop] = {}
            }
        }
    }

    recurse(data, '')

    return result as Record<string, string | boolean | number | Date>
}

export function unflatten(data: Record<string, string | boolean | number | object | Date>): unknown {
    const result = {}

    for (const path in data) {
        let cursor = result
        const length = path.length
        let property = ''
        let index = 0

        while (index < length) {
            const char = path.charAt(index)

            if (char === '[') {
                const start = index + 1
                const end = path.indexOf(']', start)
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                cursor = (cursor as any)[property] = (cursor as any)[property] || []
                property = path.slice(start, end)
                index = end + 1
            } else {
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                cursor = (cursor as any)[property] = (cursor as any)[property] || {}
                const start = char === '.' ? index + 1 : index
                const bracket = path.indexOf('[', start)
                const dot = path.indexOf('.', start)
                let end
                if (bracket < 0 && dot < 0) {
                    end = index = length
                } else if (bracket < 0) {
                    end = index = dot
                } else if (dot < 0) {
                    end = index = bracket
                } else {
                    end = index = bracket < dot ? bracket : dot
                }

                property = path.slice(start, end)
            }
        }
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        ;(cursor as any)[property] = (data as any)[path]
    }
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    return (result as any)[''] as unknown
}

export function boldChar(char: string) {
    let diff
    if (/[A-Z]/.test(char)) {
        diff = ('𝗔'.codePointAt(0) as number) - ('A'.codePointAt(0) as number)
    } else {
        diff = (('𝗮'.codePointAt(0) as number) - ('a'.codePointAt(0) as number)) as number
    }
    return String.fromCodePoint((char.codePointAt(0) as number) + diff)
}

export function clone<T>(input: T): T {
    return JSON.parse(JSON.stringify(input))
}

export function boldWord(word: string) {
    return word
        .split('')
        .map((it) => {
            return it.trim().length === 0 ? it : boldChar(it)
        })
        .join('')
}

const topicConfigurationDefinition = new Map<string, string>()

topicConfigurationDefinition.set(
    'compression.type',
    // eslint-disable-next-line vue/max-len
    'Specify the final compression type for a given topic. This configuration accepts the standard compression codecs (‘gzip’, ‘snappy’, ‘lz4’, ‘zstd’). It additionally accepts ‘uncompressed’ which is equivalent to no compression; and ‘producer’ which means retain the original compression codec set by the producer.'
)
topicConfigurationDefinition.set(
    'delete.retention.ms',
    // eslint-disable-next-line vue/max-len
    'The amount of time to retain delete tombstone markers for log compacted topics. This setting also gives a bound on the time in which a consumer must complete a read if they begin from offset 0 to ensure that they get a valid snapshot of the final stage (otherwise delete tombstones may be collected before they complete their scan).'
)
topicConfigurationDefinition.set('file.delete.delay.ms', 'The time to wait before deleting a file from the filesystem')
topicConfigurationDefinition.set(
    'flush.messages',
    // eslint-disable-next-line vue/max-len
    "This setting allows specifying an interval at which we will force an fsync of data written to the log. For example if this was set to 1 we would fsync after every message; if it were 5 we would fsync after every five messages. In general we recommend you not set this and use replication for durability and allow the operating system's background flush capabilities as it is more efficient. This setting can be overridden on a per-topic basis"
)
topicConfigurationDefinition.set(
    'flush.ms',
    // eslint-disable-next-line vue/max-len
    "This setting allows specifying a time interval at which we will force an fsync of data written to the log. For example if this was set to 1000 we would fsync after 1000 ms had passed. In general we recommend you not set this and use replication for durability and allow the operating system's background flush capabilities as it is more efficient."
)
topicConfigurationDefinition.set(
    'follower.replication.throttled.replicas',
    // eslint-disable-next-line vue/max-len
    'A list of replicas for which log replication should be throttled on the follower side. The list should describe a set of replicas in the form [PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:… or alternatively the wildcard ‘*’ can be used to throttle all replicas for this topic.'
)
topicConfigurationDefinition.set(
    'index.interval.bytes',
    // eslint-disable-next-line vue/max-len
    'This setting controls how frequently Kafka adds an index entry to its offset index. The default setting ensures that we index a message roughly every 4096 bytes. More indexing allows reads to jump closer to the exact position in the log but makes the index larger. You probably don’t need to change this.'
)
topicConfigurationDefinition.set(
    'leader.replication.throttled.replicas',
    // eslint-disable-next-line vue/max-len
    "A list of replicas for which log replication should be throttled on the leader side. The list should describe a set of replicas in the form [PartitionId]:[BrokerId],[PartitionId]:[BrokerId]:... or alternatively the wildcard '*' can be used to throttle all replicas for this topic."
)
topicConfigurationDefinition.set(
    'max.compaction.lag.ms',
    // eslint-disable-next-line vue/max-len
    'The maximum time a message will remain ineligible for compaction in the log. Only applicable for logs that are being compacted.'
)
topicConfigurationDefinition.set(
    'message.downconversion.enable',
    // eslint-disable-next-line vue/max-len
    'This configuration controls whether down-conversion of message formats is enabled to satisfy consume requests. When set to false, broker will not perform down-conversion for consumers expecting an older message format. The broker responds with UNSUPPORTED_VERSION error for consume requests from such older clients. This configuration does not apply to any message format conversion that might be required for replication to followers.'
)
topicConfigurationDefinition.set(
    'message.format.version',
    // eslint-disable-next-line vue/max-len
    '[DEPRECATED] Specify the message format version the broker will use to append messages to the logs. The value of this config is always assumed to be 3.0 if inter.broker.protocol.version is 3.0 or higher (the actual config value is ignored). Otherwise, the value should be a valid ApiVersion. Some examples are: 0.10.0, 1.1, 2.8, 3.0. By setting a particular message format version, the user is certifying that all the existing messages on disk are smaller or equal than the specified version. Setting this value incorrectly will cause consumers with older versions to break as they will receive messages with a format that they don’t understand.'
)
topicConfigurationDefinition.set(
    'message.timestamp.difference.max.ms',
    // eslint-disable-next-line vue/max-len
    'The maximum difference allowed between the timestamp when a broker receives a message and the timestamp specified in the message. If message.timestamp.type=CreateTime, a message will be rejected if the difference in timestamp exceeds this threshold. This configuration is ignored if message.timestamp.type=LogAppendTime.'
)
topicConfigurationDefinition.set(
    'message.timestamp.type',
    // eslint-disable-next-line vue/max-len
    'Define whether the timestamp in the message is message create time or log append time. The value should be either CreateTime or LogAppendTime'
)
topicConfigurationDefinition.set(
    'min.cleanable.dirty.ratio',
    // eslint-disable-next-line vue/max-len
    'This configuration controls how frequently the log compactor will attempt to clean the log (assuming log compaction is enabled). By default we will avoid cleaning a log where more than 50% of the log has been compacted. This ratio bounds the maximum space wasted in the log by duplicates (at 50% at most 50% of the log could be duplicates). A higher ratio will mean fewer, more efficient cleanings but will mean more wasted space in the log. If the max.compaction.lag.ms or the min.compaction.lag.ms configurations are also specified, then the log compactor considers the log to be eligible for compaction as soon as either: (i) the dirty ratio threshold has been met and the log has had dirty (uncompacted) records for at least the min.compaction.lag.ms duration, or (ii) if the log has had dirty (uncompacted) records for at most the max.compaction.lag.ms period.'
)
topicConfigurationDefinition.set(
    'min.compaction.lag.ms',
    // eslint-disable-next-line vue/max-len
    'The minimum time a message will remain uncompacted in the log. Only applicable for logs that are being compacted.'
)
topicConfigurationDefinition.set(
    'preallocate',
    'True if we should preallocate the file on disk when creating a new log segment.'
)
topicConfigurationDefinition.set(
    'segment.bytes',
    // eslint-disable-next-line vue/max-len
    'This configuration controls the segment file size for the log. Retention and cleaning is always done a file at a time so a larger segment size means fewer files but less granular control over retention.'
)
topicConfigurationDefinition.set(
    'segment.index.bytes',
    // eslint-disable-next-line vue/max-len
    'This configuration controls the size of the index that maps offsets to file positions. We preallocate this index file and shrink it only after log rolls. You generally should not need to change this setting.'
)
topicConfigurationDefinition.set('segment.jitter.ms', 'test')
topicConfigurationDefinition.set(
    'segment.ms',
    // eslint-disable-next-line vue/max-len
    'The maximum random jitter subtracted from the scheduled segment roll time to avoid thundering herds of segment rolling'
)
topicConfigurationDefinition.set(
    'unclean.leader.election.enable',
    // eslint-disable-next-line vue/max-len
    'The unclean leader election configuration is used to determine whether a replica that is not in-sync with the lead replica can itself become leader in a failure scenario. However if this were to happen, any messages that the unclean leader did not have would be lost.'
)
export function getTopicConfigurationDefinition(configuration: string): string {
    return topicConfigurationDefinition.get(configuration) ?? ''
}

function getTag(value: unknown) {
    if (value == null) {
        return value === undefined ? '[object Undefined]' : '[object Null]'
    }
    return toString.call(value)
}

function isObjectLike(value: unknown): boolean {
    return typeof value === 'object' && value !== null
}

export function isString(value: unknown): boolean {
    const type = typeof value
    return (
        type === 'string' ||
        (type === 'object' && value != null && !Array.isArray(value) && getTag(value) == '[object String]')
    )
}

export function isBigInt(value: unknown) {
    if (value === null || value === undefined) {
        return false
    }
    return typeof value == 'bigint' || (isObjectLike(value) && getTag(value) == '[object BigInt]')
}

export function isNumber(value: unknown) {
    if (value === null || value === undefined) {
        return false
    }
    return typeof value === 'number' || (isObjectLike(value) && getTag(value) == '[object Number]')
}

export function isBoolean(value: unknown) {
    if (value === null || value === undefined) {
        return false
    }
    return value === true || value === false || (isObjectLike(value) && getTag(value) == '[object Boolean]')
}

export function filterOutCommonErrorAttributes(data: Record<string, string>): Record<string, string> {
    return Object.keys(data)
        .filter((key) => key !== 'httpCode' && key !== 'httpStatus')
        .reduce((obj, key) => {
            return {
                ...obj,
                [key]: data[key],
            }
        }, {})
}

export function convertToBigInt(value: unknown): bigint {
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
