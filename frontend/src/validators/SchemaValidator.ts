/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { SubjectSchemaDetails } from '@/entity/SubjectSchemaDetails'

export function validate(schemaDetails: SubjectSchemaDetails): Map<string, string> {
    const errors = new Map<string, string>()
    if (schemaDetails.schema.trim().length === 0) {
        errors.set('schema', 'Schema cannot be blank or empty.')
    }
    if (
        schemaDetails.schemaType.toString() !== 'AVRO' &&
        schemaDetails.schemaType.toString() !== 'JSON' &&
        schemaDetails.schemaType.toString() !== 'PROTOBUF'
    ) {
        errors.set('schemaType', 'Schema must be AVRO, PROTOBUF or JSON.')
    } else {
        if (schemaDetails.schemaType === 'AVRO' || schemaDetails.schemaType === 'JSON') {
            try {
                const parsedContent = JSON.parse(schemaDetails.schema)
                if (!(parsedContent && typeof parsedContent === 'object')) {
                    errors.set('schema', 'Invalid schema.')
                }
            } catch (e) {
                errors.set('schema', (e as Error).toString())
            }
        }
    }

    const names = new Set<string>()
    const subjects = new Set<string>()

    schemaDetails.references.forEach((reference, index) => {
        if (reference.name.trim().length === 0) {
            errors.set(`reference-${index}-name`, 'Reference name cannot be blank.')
        } else {
            if (!/^(?:[A-Za-z]|[A-Za-z][A-Za-z\\-]*[A-Za-z]+)$/.test(reference.name)) {
                errors.set(
                    `reference-${index}-name`,
                    'Can only have alphanumeric characters with optional dash in between.'
                )
            } else if (names.has(reference.name)) {
                errors.set(`reference-${index}-name`, 'Same reference name cannot be repeated.')
            }
            names.add(reference.name)
        }

        if (reference.subject.trim().length === 0) {
            errors.set(`reference-${index}-subject`, 'Subject name cannot be blank.')
        } else {
            if (subjects.has(reference.subject)) {
                errors.set(`reference-${index}-subject`, 'Subject name cannot be repeated.')
            }
            subjects.add(reference.subject)
        }

        if (reference.version === -1) {
            errors.set(`reference-${index}-version`, 'Subject version cannot be blank.')
        }
    })

    return errors
}

export function getError(errors: Map<string, string>, key: string, referenceIndex = -1): string | null {
    const finalKey = referenceIndex === -1 ? key : `reference-${referenceIndex}-${key}`
    return errors.get(finalKey) ?? null
}

export function hasError(errors: Map<string, string>, key: string, referenceIndex = -1): boolean {
    const finalKey = referenceIndex === -1 ? key : `reference-${referenceIndex}-${key}`
    return errors.has(finalKey)
}

export function haveErrors(errors: Map<string, string>): boolean {
    return errors.size !== 0
}
