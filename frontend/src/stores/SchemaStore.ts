/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineStore } from 'pinia'
import { getTimestamp } from '@/util/Util'
import eventBus from '@/util/EventBus'
import { deleteAction, getAction, postAction, putAction } from '@/util/HttpService'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ServerResponse } from '@/entity/ServerResponse'
import type { SubjectSchemaDetails } from '@/entity/SubjectSchemaDetails'
import type { ErrorResponse } from '@/entity/ErrorResponse'
import { CompatibilityType } from '@/entity/CompatibilityType'

export const schemaStore = defineStore('schemas', {
    state: () => ({
        schemaData: new Map<string, Map<string, SubjectSchemaDetails>>(),
        subjectVersionsList: new Map<string, Map<string, number[]>>(),
        schemaDataLoadedTime: new Map<string, Map<string, number>>(),
        schemaCompatibility: new Map<string, Map<string, CompatibilityType>>(),
        schemaReferences: new Map<string, Map<string, string>>(),
        isSchemaRegistryConfigured: new Map<string, boolean>(),
        isSchemaRegistryConfigurationChecked: new Map<string, boolean>(),
        schemaSubjectsByType: new Map<string, Map<string, string[]>>(),
    }),
    getters: {
        getSchemaBySubjectAndVersion: (
            state
        ): ((clusterId: string, subject: string, version: number | 'latest') => SubjectSchemaDetails | null) => {
            return (clusterId: string, subject: string, version: number | 'latest') => {
                return (state.schemaData.get(clusterId) ?? new Map()).get(subject + version.toString()) ?? null
            }
        },
        getSubjectsByType: (state): ((clusterId: string, type: 'AVRO' | 'PROTOBUF' | 'JSON') => string[]) => {
            return (clusterId: string, type: string) => {
                return (state.schemaSubjectsByType.get(clusterId) ?? new Map()).get(type) ?? []
            }
        },
        getSubjectVersionsList: (state): ((clusterId: string, subject: string) => number[]) => {
            return (clusterId: string, subject: string) => {
                return (state.subjectVersionsList.get(clusterId) ?? new Map()).get(subject) ?? []
            }
        },
        getSchemaCompatibility: (state): ((clusterId: string, subject: string) => CompatibilityType) => {
            return (clusterId: string, subject: string) => {
                return (
                    (state.schemaCompatibility.get(clusterId) ?? new Map()).get(subject) ?? CompatibilityType.BACKWARD
                )
            }
        },
        isSchemaLoaded: (state): ((clusterId: string, subject: string, version: number | 'latest') => boolean) => {
            return (clusterId: string, subject: string, version: number | 'latest') => {
                return (state.schemaData.get(clusterId) ?? new Map()).has(`${subject}${version.toString()}`)
            }
        },
        isSubjectVersionsListLoaded: (state): ((clusterId: string, subject: string) => boolean) => {
            return (clusterId: string, subject: string) => {
                return (state.subjectVersionsList.get(clusterId) ?? new Map()).has(subject)
            }
        },
    },
    actions: {
        async saveSchema(clusterId: string, schemaDetails: SubjectSchemaDetails) {
            eventBus.emit(ApplicationEventTypes.BEFORE_SCHEMA_SAVE, true)
            await postAction(
                `${clusterId}/subjects/${schemaDetails.subject}/versions`,
                {
                    schema: schemaDetails.schema,
                    schemaType: schemaDetails.schemaType,
                    references: schemaDetails.references,
                },
                (result: ServerResponse<Record<string, number>>) => {
                    eventBus.emit(ApplicationEventTypes.SCHEMA_SAVE, true, result)
                },
                (error: ErrorResponse) => {
                    eventBus.emit(ApplicationEventTypes.SCHEMA_SAVE, false, error)
                }
            )
        },
        async checkSchemaCompatibility(
            clusterId: string,
            schemaDetails: SubjectSchemaDetails,
            successFn: (response: ServerResponse<Record<string, boolean>>) => void,
            errorFn: (response: ErrorResponse) => void
        ) {
            await postAction(
                `${clusterId}/compatibility/subjects/${schemaDetails.subject}/versions`,
                {
                    schema: schemaDetails.schema,
                    schemaType: schemaDetails.schemaType,
                    references: schemaDetails.references,
                },
                successFn,
                errorFn
            )
        },
        async checkIfSchemaRegistryIsConfigured(clusterId: string) {
            if (!this.isSchemaRegistryConfigurationChecked.get(clusterId) ?? false) {
                await getAction(
                    `${clusterId}/schemas/configured`,
                    () => {
                        this.isSchemaRegistryConfigurationChecked.set(clusterId, true)
                        this.isSchemaRegistryConfigured.set(clusterId, true)
                    },
                    () => {
                        this.isSchemaRegistryConfigurationChecked.set(clusterId, true)
                        this.isSchemaRegistryConfigured.set(clusterId, false)
                    }
                )
            }
        },
        async loadSchemaByType(clusterId: string, type: 'JSON' | 'AVRO' | 'PROTOBUF') {
            eventBus.emit(ApplicationEventTypes.BEFORE_SUBJECTS_BY_TYPE_LOADED, true)
            await getAction(
                `${clusterId}/subjects/${type}`,
                (response: ServerResponse<string[]>) => {
                    const existingTypes = this.schemaSubjectsByType.get(clusterId) ?? new Map()
                    existingTypes.set(type, response.data)
                    this.schemaSubjectsByType.set(clusterId, existingTypes)
                    eventBus.emit(ApplicationEventTypes.SUBJECTS_BY_TYPE_LOADED, true)
                },
                (error: ErrorResponse) => {
                    eventBus.emit(ApplicationEventTypes.SUBJECTS_BY_TYPE_LOADED, false, error)
                }
            )
        },
        async loadSchemaData(clusterId: string, subject: string, version: number | 'latest') {
            eventBus.emit(ApplicationEventTypes.BEFORE_SCHEMA_DATA_LOADED, true)
            await getAction(
                `${clusterId}/subjects/${subject}/versions/${version}`,
                async (response: ServerResponse<SubjectSchemaDetails>) => {
                    const formattedSchema =
                        response.data.schemaType === 'PROTOBUF'
                            ? response.data.schema
                            : JSON.stringify(JSON.parse(response.data.schema), null, 4)
                    const existingOne = this.schemaData.get(clusterId) ?? new Map()
                    if (version.toString() === 'latest') {
                        existingOne.set(`${subject}${version.toString()}`, {
                            subject: response.data.subject,
                            version: response.data.version,
                            id: response.data.id,
                            schema: formattedSchema,
                            schemaType: response.data.schemaType,
                            references: response.data.references ?? [],
                        })
                    }

                    existingOne.set(`${subject}${response.data.version.toString()}`, {
                        subject: response.data.subject,
                        version: response.data.version,
                        id: response.data.id,
                        schema: formattedSchema,
                        schemaType: response.data.schemaType,
                        references: response.data.references ?? [],
                    })
                    this.schemaData.set(clusterId, existingOne)
                    const existingTimestamps = this.schemaDataLoadedTime.get(clusterId) ?? new Map()
                    existingTimestamps.set(subject, getTimestamp())
                    this.schemaDataLoadedTime.set(clusterId, existingTimestamps)
                    eventBus.emit(ApplicationEventTypes.SCHEMA_DATA_LOADED, true, response.data)
                },
                (errorResponse) => {
                    eventBus.emit(ApplicationEventTypes.SCHEMA_DATA_LOADED, false, errorResponse)
                }
            )
        },
        async loadSubjectVersionsList(clusterId: string, subject: string) {
            eventBus.emit(ApplicationEventTypes.BEFORE_SUBJECT_VERSIONS_LIST_LOADED, true)
            await getAction(
                `${clusterId}/subjects/${subject}/versions`,
                async (result: ServerResponse<number[]>) => {
                    const existingEntry = this.subjectVersionsList.get(clusterId) ?? new Map()
                    existingEntry.set(subject, result.data)
                    eventBus.emit(ApplicationEventTypes.SUBJECT_VERSIONS_LIST_LOADED, true)
                    this.subjectVersionsList.set(clusterId, existingEntry)
                },
                (response: ErrorResponse) => {
                    if (response.httpCode === 404) {
                        const existingEntry = this.subjectVersionsList.get(clusterId) ?? new Map()
                        existingEntry.set(subject, [])
                        eventBus.emit(ApplicationEventTypes.SUBJECT_VERSIONS_LIST_LOADED, true)
                        this.subjectVersionsList.set(clusterId, existingEntry)
                    } else {
                        eventBus.emit(ApplicationEventTypes.SUBJECT_VERSIONS_LIST_LOADED, false)
                    }
                }
            )
        },
        async loadCompatibilityMode(clusterId: string, subject: string) {
            eventBus.emit(ApplicationEventTypes.BEFORE_SCHEMA_COMPATIBILITY_DATA_LOADED, true)
            await getAction(
                `${clusterId}/config/${subject}`,
                (result: ServerResponse<Record<'compatibilityLevel', string>>) => {
                    const existingEntry = this.schemaCompatibility.get(clusterId) ?? new Map()
                    existingEntry.set(
                        subject,
                        CompatibilityType[
                            result.data.compatibilityLevel as
                                | 'BACKWARD'
                                | 'BACKWARD_TRANSITIVE'
                                | 'FORWARD'
                                | 'FORWARD_TRANSITIVE'
                                | 'FULL'
                                | 'FULL_TRANSITIVE'
                                | 'NONE'
                        ]
                    )
                    this.schemaCompatibility.set(clusterId, existingEntry)
                    eventBus.emit(ApplicationEventTypes.SCHEMA_COMPATIBILITY_DATA_LOADED, true)
                },
                (response: ErrorResponse) => {
                    if (response.httpCode === 404) {
                        const existingEntry = this.schemaCompatibility.get(clusterId) ?? new Map()
                        existingEntry.set(subject, CompatibilityType.BACKWARD)
                        this.schemaCompatibility.set(clusterId, existingEntry)
                        eventBus.emit(ApplicationEventTypes.SCHEMA_COMPATIBILITY_DATA_LOADED, true)
                    } else {
                        eventBus.emit(ApplicationEventTypes.SCHEMA_COMPATIBILITY_DATA_LOADED, false, response)
                    }
                }
            )
        },
        async loadData(clusterId: string, subject: string, version: number | 'latest') {
            eventBus.emit(ApplicationEventTypes.BEFORE_SCHEMA_READY, true)

            try {
                await Promise.all([
                    this.loadSubjectVersionsList(clusterId, subject),
                    this.loadSchemaData(clusterId, subject, version),
                    this.loadCompatibilityMode(clusterId, subject),
                ])
                eventBus.emit(ApplicationEventTypes.SCHEMA_READY, true)
            } catch (exception) {
                eventBus.emit(ApplicationEventTypes.SCHEMA_READY, false)
            }
        },
        async loadSchemaIfNotLoaded(clusterId: string, subject: string, version: number | 'latest') {
            if (!this.schemaData.has(`${subject}${version.toString()}`)) {
                await this.loadSchemaData(clusterId, subject, version)
            }
        },
        async updateSchemaCompatibilitySettings(
            clusterId: string,
            subject: string,
            compatibilitySetting: CompatibilityType
        ) {
            eventBus.emit(ApplicationEventTypes.BEFORE_SCHEMA_COMPATIBILITY_UPDATE, true)
            await putAction(
                `${clusterId}/config/${subject}`,
                {
                    compatibility: compatibilitySetting.toString(),
                },
                (result: ServerResponse<Record<'compatibility', CompatibilityType>>) => {
                    const existingEntries = this.schemaCompatibility.get(clusterId) ?? new Map()
                    existingEntries.set(
                        subject,
                        CompatibilityType[
                            result.data.compatibility as
                                | 'BACKWARD'
                                | 'BACKWARD_TRANSITIVE'
                                | 'FORWARD'
                                | 'FORWARD_TRANSITIVE'
                                | 'FULL'
                                | 'FULL_TRANSITIVE'
                                | 'NONE'
                        ]
                    )
                    this.schemaCompatibility.set(clusterId, existingEntries)
                    eventBus.emit(ApplicationEventTypes.SCHEMA_COMPATIBILITY_UPDATE, true)
                },
                (response: ErrorResponse) => {
                    eventBus.emit(ApplicationEventTypes.SCHEMA_COMPATIBILITY_UPDATE, false, response)
                }
            )
        },
        async deleteSchemaVersion(clusterId: string, subject: string, version: number, hardDelete = false) {
            eventBus.emit(ApplicationEventTypes.BEFORE_SCHEMA_VERSION_DELETE, true)
            await deleteAction(
                hardDelete
                    ? `${clusterId}/subjects/${subject}/versions/${version}/?permanent=true`
                    : `${clusterId}/subjects/${subject}/versions/${version}`,
                (result: ServerResponse<number>) => {
                    // Delete the version
                    const existingEntries = this.subjectVersionsList.get(clusterId) ?? new Map()
                    const filteredList = (existingEntries.get(subject) ?? []).filter((it: number) => it !== result.data)
                    existingEntries.set(subject, filteredList)
                    this.subjectVersionsList.set(clusterId, existingEntries)
                    eventBus.emit(ApplicationEventTypes.SCHEMA_VERSION_DELETE, true)
                },
                (response: ErrorResponse) => {
                    eventBus.emit(ApplicationEventTypes.SCHEMA_VERSION_DELETE, false, response)
                }
            )
        },
        async deleteSubject(clusterId: string, subject: string, hardDelete = false) {
            eventBus.emit(ApplicationEventTypes.BEFORE_SUBJECT_DELETE, true)
            await deleteAction(
                hardDelete ? `${clusterId}/subjects/${subject}?permanent=true` : `${clusterId}/subjects/${subject}`,
                () => {
                    const type = this.getSchemaBySubjectAndVersion(clusterId, subject, 'latest')?.schemaType ?? 'AVRO'

                    let existingEntries = this.subjectVersionsList.get(clusterId) ?? new Map()
                    existingEntries.delete(subject)
                    this.subjectVersionsList.set(clusterId, new Map<string, number[]>(existingEntries))

                    existingEntries = this.schemaData.get(clusterId) ?? new Map()
                    existingEntries.delete(subject)
                    this.schemaData.set(clusterId, new Map(existingEntries))

                    existingEntries = this.schemaCompatibility.get(clusterId) ?? new Map()
                    existingEntries.delete(subject)
                    this.schemaCompatibility.set(clusterId, new Map(existingEntries))

                    existingEntries = this.schemaReferences.get(clusterId) ?? new Map()
                    existingEntries.delete(subject)
                    this.schemaReferences.set(clusterId, new Map(existingEntries))

                    existingEntries = this.schemaSubjectsByType.get(clusterId) ?? new Map()
                    const filteredList = (existingEntries.get(type) ?? []).filter((it: string) => it !== subject)
                    existingEntries.set(type, filteredList)
                    this.schemaSubjectsByType.set(clusterId, existingEntries)

                    eventBus.emit(ApplicationEventTypes.SUBJECT_DELETE, true, subject)
                },
                (response: ErrorResponse) => {
                    eventBus.emit(ApplicationEventTypes.SUBJECT_DELETE, false, response)
                }
            )
        },
    },
})
