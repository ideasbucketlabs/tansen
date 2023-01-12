/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface TopicMessage {
    headerData: Array<unknown>
    valueData: unknown
    keyData: unknown
    timestamp: number
    timestampType: 'NoTimestampType' | 'CreateTime' | 'LogAppendTime'
    partition: number
    valueSchemaFormat: 'AVRO' | 'JSON' | 'PROTOBUF' | null
    keySchemaFormat: 'AVRO' | 'JSON' | 'PROTOBUF' | null
    offset: number
}
