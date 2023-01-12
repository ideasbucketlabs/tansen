/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { SchemaReference } from '@/entity/SchemaReference'

export interface SubjectSchemaDetails {
    subject: string
    version: number
    id: number
    schema: string
    schemaType: 'JSON' | 'AVRO' | 'PROTOBUF'
    references: SchemaReference[]
}
