<template>
    <div class="relative flex flex-1 flex-col">
        <Loading v-if="saveInProgress" class="z-10">Saving your changes please wait...</Loading>
        <MessageDialog
            v-if="showMessageDialog"
            :title="messageDialogTitle"
            :icon="messageDialogIcon"
            :buttons="messageDialogButtons"
            @close="onDialogClick"
        >
            {{ messageDialogMessage }}
        </MessageDialog>
        <NewSchemaForm
            v-if="insertMode"
            class="flex-1"
            :subject="props.subject"
            :loading="insertFormLoading"
            @cancel="cancel"
            @submit="saveNewSchema"
        ></NewSchemaForm>
        <div class="rounded md:flex md:flex-1 md:flex-col" v-if="haveSchema && !loading">
            <SchemaCompatibilitySettingsForm
                v-if="showCompatibilitySettingForm"
                @close="showCompatibilitySettingForm = false"
                @submit="onSchemaCompatibilityFormSubmission"
                :subject="subject"
                :compatibility="compatibilityType"
            ></SchemaCompatibilitySettingsForm>
            <DeleteSchemaForm
                v-if="showDeleteSchemaForm"
                :versionList="versionsList"
                @submit="onDeleteSchemaFormSubmission"
                @close="showDeleteSchemaForm = false"
            ></DeleteSchemaForm>
            <div class="py-2 md:flex md:space-x-5">
                <div>
                    Type: <span class="font-bold" v-if="schemaType === 'JSON'">JSON Schema</span
                    ><span class="font-bold" v-else>{{ schemaType }}</span>
                </div>
                <div>
                    Compatibility mode: <span class="font-bold">{{ compatibilityType }}</span>
                </div>
            </div>
            <div
                class="items-center space-y-2 py-2 md:flex md:space-x-5 md:space-y-0 md:px-4"
                v-if="!editMode && !insertMode"
            >
                <div class="w-64 md:pl-1" v-if="!showDiffer">
                    <BaseSelectField
                        label="Schema version"
                        :hide-label="true"
                        v-model.number="selectedSchemaVersion"
                        :options="versionsList"
                    ></BaseSelectField>
                </div>
                <div v-if="!showDiffer">
                    Schema ID: <span class="font-bold">{{ currentSchemaId }}</span>
                </div>
                <div class="flex-1 items-center justify-end space-y-1 md:flex md:space-x-1 md:space-y-0">
                    <button
                        type="button"
                        v-if="versionsList.length > 1 && showDiffer"
                        @click="showDiffer = false"
                        class="relative flex items-center overflow-hidden rounded border border-green-500 px-4 py-2 text-green-500 transition duration-200 ease-linear hover:bg-green-100 hover:shadow dark:border-gray-300 dark:text-gray-100 dark:shadow-black dark:hover:bg-gray-600"
                    >
                        <ripple></ripple>
                        <span class="mr-1 block"><UpArrowIcon class="w-4 -rotate-90 fill-current"></UpArrowIcon></span>
                        Back to Schema
                    </button>
                    <div
                        class="action-menu relative w-20 md:w-auto"
                        tabindex="-1"
                        v-if="versionsList.length > 0 && !showDiffer"
                    >
                        <button
                            class="relative flex items-center overflow-hidden rounded border border-green-500 px-4 py-2 text-green-500 transition duration-200 ease-linear hover:bg-green-100 hover:shadow dark:border-gray-300 dark:text-gray-100 dark:shadow-black dark:hover:bg-gray-600"
                        >
                            <ActionIcon class="h-6 w-6 fill-current"></ActionIcon>
                        </button>
                        <div class="absolute left-0 z-10 w-52 pt-2">
                            <ul
                                class="cursor-pointer rounded border border-green-500 bg-white shadow-lg dark:border-gray-300 dark:bg-gray-800"
                            >
                                <li
                                    @click="downloadSelectedSchema"
                                    class="rounded-t border-b border-t border-white px-4 py-2 transition duration-200 ease-linear hover:border-green-100 hover:bg-green-50 dark:border-gray-800 dark:hover:border-gray-700 dark:hover:bg-gray-600"
                                >
                                    Download
                                </li>
                                <li
                                    @click="showDeleteSchemaForm = true"
                                    class="border-b border-t border-white px-4 py-2 transition duration-200 ease-linear hover:border-green-100 hover:bg-green-50 dark:border-gray-800 dark:hover:border-gray-900 dark:hover:bg-gray-600"
                                >
                                    Delete
                                </li>
                                <li
                                    @click="showCompatibilitySettingForm = true"
                                    class="rounded-b border-b border-t border-white px-4 py-2 transition duration-200 ease-linear hover:border-green-100 hover:bg-green-50 dark:border-gray-800 dark:hover:border-gray-900 dark:hover:bg-gray-600"
                                >
                                    Compatibility setting(s)
                                </li>
                            </ul>
                        </div>
                    </div>
                    <button
                        type="button"
                        v-if="versionsList.length > 0 && !showDiffer"
                        @click="turnOnEditMode"
                        class="relative flex items-center overflow-hidden truncate rounded border border-green-500 px-4 py-2 text-green-500 transition duration-200 ease-linear hover:bg-green-100 hover:shadow dark:border-gray-300 dark:text-gray-100 dark:shadow-black dark:hover:bg-gray-600"
                    >
                        <span class="mr-1 block"><EditIcon class="w-4 fill-current"></EditIcon></span>
                        <ripple></ripple>
                        Update Schema
                    </button>
                    <button
                        type="button"
                        @click="showDiffer = true"
                        v-if="versionsList.length > 1 && !showDiffer"
                        class="relative flex items-center overflow-hidden truncate rounded border border-green-500 px-4 py-2 text-green-500 transition duration-200 ease-linear hover:bg-green-100 hover:shadow dark:border-gray-300 dark:text-gray-100 dark:shadow-black dark:hover:bg-gray-600"
                    >
                        <ripple></ripple>
                        <span class="mr-1 block"><CompareIcon class="w-4 fill-current"></CompareIcon></span>
                        Compare version(s)
                    </button>
                </div>
            </div>
            <div class="flex flex-1 flex-col" v-if="!showDiffer && currentlyDisplayedSchema !== null">
                <code-mirror
                    :key="codeMirrorKey"
                    :schemaType="schemaType"
                    :value="currentlyDisplayedSchema"
                    :read-only="readOnly"
                    @change="updateSchema"
                    :errors="errors"
                    class="flex-1 md:p-5"
                ></code-mirror>
            </div>
            <div v-if="compatibilityErrorCode !== -1" class="m-4 flex flex-col flex-nowrap rounded shadow">
                <div class="rounded-t bg-red-500 p-2 text-white dark:bg-red-900">
                    Schema Compatibility Error (Error code {{ compatibilityErrorCode }})
                </div>
                <div class="flex flex-nowrap overflow-auto rounded-b bg-red-50 p-2 dark:bg-gray-900">
                    <pre class="w-32 flex-1 break-all">{{ compatibilityError }}</pre>
                </div>
            </div>
            <SchemaDiffViewer
                v-if="showDiffer"
                :schemaVersions="versionsList"
                class="md:flex md:flex-1 md:flex-col"
                :subject="subject"
            ></SchemaDiffViewer>
            <SchemaReference
                v-if="editMode || insertMode"
                :subject="subject"
                :errors="errors"
                class="min-h-20 mt-2 md:flex-1"
                v-model="references"
                :schemaType="schemaType"
            ></SchemaReference>
        </div>
        <div v-if="!haveSchema && !loading && !insertMode" class="flex items-center p-2 lg:h-96 lg:space-x-5">
            <art class="ml-2 lg:h-72 lg:w-72 xl:h-72 xl:w-72"></art>
            <div>
                <div class="text-xl font-bold">A message {{ type }} schema is not set.</div>
                <div>Set a schema to ensure the compatibility of your data and code.</div>
                <div>
                    <BaseButton class="mt-4 h-10 w-48" @click="insertMode = true" label="Set a schema"></BaseButton>
                </div>
            </div>
        </div>
        <div v-if="loading" class="relative flex flex-1 flex-col rounded bg-white shadow dark:bg-gray-900">
            <Loading>Loading...</Loading>
        </div>
        <div class="sticky bottom-0 flex" v-if="editMode">
            <div
                style="box-shadow: 0 -10px 14px -10px rgba(0, 0, 0, 0.1)"
                class="flex w-full justify-between border-t bg-gray-100 px-4 pt-2 dark:border-gray-900 dark:bg-gray-800"
            >
                <button
                    class="w-24 rounded bg-red-500 px-4 py-2 text-white transition duration-200 ease-linear hover:bg-red-700"
                    @click="cancel"
                    type="button"
                >
                    Cancel
                </button>
                <div class="flex items-center space-x-2">
                    <div
                        v-if="isDataDirty && errors.size === 0 && compatibilityErrorCode === -1"
                        class="flex h-10 items-center font-bold text-green-500"
                    >
                        <CheckAll class="h-full w-10 fill-current"></CheckAll>
                        <span class="w-34 hidden md:block">Valid schema</span>
                    </div>
                    <button
                        class="relative h-10 w-24 overflow-hidden rounded bg-cyan-500 px-4 py-2 text-white transition duration-200 ease-linear hover:bg-cyan-600"
                        type="button"
                        @click="validateSchema"
                    >
                        <Ripple ripple-class="bg-cyan-400 opacity-25"></Ripple>
                        Validate
                    </button>
                    <BaseButton
                        label="Save"
                        class="h-10 w-32"
                        @click="saveSchema"
                        :disabled="haveErrors(errors) || compatibilityErrorCode !== -1"
                    ></BaseButton>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { computed, defineAsyncComponent, onMounted, onUnmounted, type PropType, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { schemaStore } from '@/stores/SchemaStore'
import type { SchemaReference as SchemaReferenceEntity } from '@/entity/SchemaReference'
import Loading from '@/components/Loading.vue'
import CodeMirror from '@/components/CodeMirror.vue'
import BaseSelectField from '@/components/BaseSelectField.vue'
import AppComponentLoader from '@/components/AppComponentLoader.vue'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import BaseButton from '@/components/BaseButton.vue'
import CompareIcon from '@/icons/CompareIcon.vue'
import Ripple from '@/components/Ripple.vue'
import UpArrowIcon from '@/icons/UpArrowIcon.vue'
import EditIcon from '@/icons/EditIcon.vue'
import { downloadFile, getId } from '@/util/Util'
import type { SubjectSchemaDetails } from '@/entity/SubjectSchemaDetails'
import { haveErrors, validate } from '@/validators/SchemaValidator'
import type { SchemaCompatibilityError } from '@/entity/SchemaCompatibilityError'
import CheckAll from '@/icons/CheckAll.vue'
import type { ServerResponse } from '@/entity/ServerResponse'
import MessageDialog from '@/components/MessageDialog.vue'
import { Buttons } from '@/entity/Buttons'
import type { CompatibilityType } from '@/entity/CompatibilityType'
import type { ErrorResponse } from '@/entity/ErrorResponse'
import type { GenericError } from '@/entity/GenericError'
import ActionIcon from '@/icons/ActionIcon.vue'

const store = schemaStore()
const clusterId = useRoute().params.clusterId as string
const insertFormLoading = ref<boolean>(false)
const codeMirrorKey = ref<string>(getId())
const showCompatibilitySettingForm = ref<boolean>(false)
const showDeleteSchemaForm = ref<boolean>(false)
const SchemaDiffViewer = defineAsyncComponent({
    loader: () => import('@/components/SchemaDiffViewer.vue'),
    loadingComponent: AppComponentLoader,
    delay: 200,
})
const NewSchemaForm = defineAsyncComponent({
    loader: () => import('@/forms/NewSchemaForm.vue'),
    loadingComponent: AppComponentLoader,
    delay: 200,
})
const Art = defineAsyncComponent(() => import('@/components/Art.vue'))
const SchemaReference = defineAsyncComponent({
    loader: () => import('@/components/SchemaReference.vue'),
    loadingComponent: AppComponentLoader,
    delay: 200,
})
const SchemaCompatibilitySettingsForm = defineAsyncComponent({
    loader: () => import('@/forms/SchemaCompatibilitySettingsForm.vue'),
    loadingComponent: AppComponentLoader,
    delay: 200,
})
const DeleteSchemaForm = defineAsyncComponent({
    loader: () => import('@/forms/DeleteSchemaForm.vue'),
    loadingComponent: AppComponentLoader,
    delay: 200,
})
const props = defineProps({
    subject: {
        type: String as PropType<string>,
        required: true,
    },
    type: {
        type: String as PropType<string>,
        required: true,
    },
})
const subjectThatIsInProcess = ref<string>('')
const showMessageDialog = ref<boolean>(false)
const messageDialogTitle = ref<string>('')
const messageDialogIcon = ref<'info' | 'question' | 'alert' | 'error' | 'success'>('success')
const messageDialogButtons = ref<Buttons>(Buttons.ok)
const errors = ref<Map<string, string>>(new Map<string, string>())
const currentSchemaData = ref<SubjectSchemaDetails | null>(null)
const references = ref<SchemaReferenceEntity[]>([])
const selectedSchemaVersion = ref<number>(0)
const readOnly = ref<boolean>(true)
const loading = ref<boolean>(true)
const editMode = ref<boolean>(false)
const insertMode = ref<boolean>(false)
const showDiffer = ref<boolean>(false)
const isDataDirty = ref<boolean>(false)
const compatibilityError = ref<string>('')
const saveInProgress = ref<boolean>(false)
const compatibilityErrorCode = ref<number>(-1)
const currentlyDisplayedSchema = ref<string | null>(null)
const messageDialogMessage = ref<string>('')
const versionsList = computed<{ label: string; value: number }[]>(() => {
    const versions = store.getSubjectVersionsList(clusterId, props.subject)
    const versionedSchema = store.getSchemaBySubjectAndVersion(clusterId, props.subject, 'latest')
    return versions.map((it) => {
        return {
            label: versionedSchema?.version === it ? 'Version ' + it + ' (Current version)' : 'Version ' + it,
            value: it,
        }
    })
})
const haveSchema = computed<boolean>(() => {
    return store.getSubjectVersionsList(clusterId, props.subject).length !== 0
})
const schemaType = computed<'JSON' | 'AVRO' | 'PROTOBUF'>(() => {
    // eslint-disable-next-line max-len
    return (
        store.getSchemaBySubjectAndVersion(clusterId, props.subject, selectedSchemaVersion.value ?? 'latest')
            ?.schemaType ?? 'AVRO'
    )
})
const compatibilityType = computed<CompatibilityType>(() => {
    return store.getSchemaCompatibility(clusterId, props.subject)
})
const currentSchemaId = computed<number>(() => {
    return store.getSchemaBySubjectAndVersion(clusterId, props.subject, 'latest')?.id ?? 0
})
const computedSubjectSchemaDetails = computed<SubjectSchemaDetails>(() => {
    return {
        subject: props.subject,
        version: selectedSchemaVersion.value,
        id: selectedSchemaVersion.value,
        schema: currentlyDisplayedSchema.value ?? '',
        schemaType: schemaType.value,
        references: references.value,
    }
})

watch(selectedSchemaVersion, async (newSelectedSchemaVersion: number) => {
    if (!store.isSchemaLoaded(clusterId, props.subject, newSelectedSchemaVersion)) {
        await store.loadSchemaIfNotLoaded(clusterId, props.subject, newSelectedSchemaVersion)
    }
    const schemaData = store.getSchemaBySubjectAndVersion(clusterId, props.subject, selectedSchemaVersion.value)
    if (schemaData !== null) {
        currentlyDisplayedSchema.value = schemaData.schema
    }
})

watch(
    references,
    () => {
        if (isDataDirty.value) {
            validateSchema()
        }
    },
    { deep: true }
)

watch(currentlyDisplayedSchema, () => {
    if (isDataDirty.value && !readOnly.value) {
        validateSchema()
    }
})

function updateSchema(event: string) {
    currentlyDisplayedSchema.value = event
}

function beforeDataLoad() {
    loading.value = true
}

async function onSchemaCompatibilityFormSubmission(compatibilityType: CompatibilityType) {
    subjectThatIsInProcess.value = props.subject
    await store.updateSchemaCompatibilitySettings(clusterId, props.subject, compatibilityType)
}

function cancel() {
    readOnly.value = true
    if (editMode.value) {
        selectedSchemaVersion.value = versionsList.value.slice().reverse()[0].value
        currentlyDisplayedSchema.value = currentSchemaData?.value?.schema ?? ''
        references.value = currentSchemaData?.value?.references ?? []
    }
    editMode.value = false
    insertMode.value = false
    isDataDirty.value = false
    compatibilityErrorCode.value = -1
    errors.value = new Map<string, string>()
}

function turnOnEditMode() {
    selectedSchemaVersion.value = versionsList.value.slice().reverse()[0].value
    currentlyDisplayedSchema.value = currentSchemaData?.value?.schema ?? ''
    editMode.value = true
    readOnly.value = false
}

function validateSchema() {
    errors.value = validate(computedSubjectSchemaDetails.value)
    compatibilityErrorCode.value = -1
    compatibilityError.value = ''
    isDataDirty.value = true
}

function onDialogClick() {
    showMessageDialog.value = false
}

async function saveNewSchema(newSchema: SubjectSchemaDetails) {
    insertFormLoading.value = true
    subjectThatIsInProcess.value = props.subject
    await store.saveSchema(clusterId, newSchema)
}

async function onDeleteSchemaFormSubmission(schemaVersion: number, hardDelete: boolean) {
    subjectThatIsInProcess.value = props.subject
    if (schemaVersion > 0) {
        await store.deleteSchemaVersion(clusterId, props.subject, schemaVersion, hardDelete)
        return
    }

    if (schemaVersion === 0) {
        await store.deleteSubject(clusterId, props.subject, hardDelete)
        return
    }
}

async function checkSchemaCompatibility() {
    await store.checkSchemaCompatibility(
        clusterId,
        computedSubjectSchemaDetails.value,
        (result: ServerResponse<Record<string, boolean>>) => {
            if (result.data['isCompatible'] as boolean) {
                compatibilityError.value = ''
                compatibilityErrorCode.value = -1
            } else {
                compatibilityError.value = 'Schema is not compatible.'
                compatibilityErrorCode.value = 422

                showMessageDialog.value = true
                messageDialogButtons.value = Buttons.ok
                messageDialogIcon.value = 'error'
                messageDialogTitle.value = 'ERROR 422'
                messageDialogMessage.value = 'Schema is not compatible.'
            }
        },
        (error) => {
            if (error.httpCode === 422) {
                compatibilityError.value = (error.errors as SchemaCompatibilityError).detail.message
                compatibilityErrorCode.value = (error.errors as SchemaCompatibilityError).detail.errorCode

                showMessageDialog.value = true
                messageDialogButtons.value = Buttons.ok
                messageDialogIcon.value = 'error'
                messageDialogTitle.value = `ERROR ${(
                    error.errors as SchemaCompatibilityError
                ).detail.errorCode.toString(10)}`
                messageDialogMessage.value = (error.errors as SchemaCompatibilityError).detail.message
            } else {
                compatibilityError.value = error.response
                compatibilityErrorCode.value = error.httpCode

                showMessageDialog.value = true
                messageDialogButtons.value = Buttons.ok
                messageDialogIcon.value = 'error'
                messageDialogTitle.value = `ERROR ${error.httpCode}`
                messageDialogMessage.value = error.response
            }
        }
    )
}

async function saveSchema() {
    validateSchema()

    if (errors.value.size === 0) {
        await checkSchemaCompatibility()
    }

    if (errors.value.size !== 0 || compatibilityErrorCode.value !== -1) {
        return
    }

    // Save the schema.
    subjectThatIsInProcess.value = props.subject
    saveInProgress.value = true
    await store.saveSchema(clusterId, computedSubjectSchemaDetails.value)
}

function afterDataLoad(event: ApplicationEvent) {
    if (event.eventType === ApplicationEventTypes.SCHEMA_READY) {
        if (store.getSubjectVersionsList(clusterId, props.subject).length !== 0) {
            selectedSchemaVersion.value = store.getSubjectVersionsList(clusterId, props.subject).slice().reverse()[0]
            const schemaData = store.getSchemaBySubjectAndVersion(clusterId, props.subject, selectedSchemaVersion.value)
            if (schemaData !== null) {
                currentlyDisplayedSchema.value = schemaData.schema
                references.value = schemaData.references
                currentSchemaData.value = schemaData
            }
        }
    }
    loading.value = false
}

async function afterSchemaUpdate(applicationEvent: ApplicationEvent) {
    saveInProgress.value = false
    insertFormLoading.value = false

    if (subjectThatIsInProcess.value !== props.subject) {
        return
    }

    if (applicationEvent.success) {
        showMessageDialog.value = true
        messageDialogButtons.value = Buttons.ok
        messageDialogIcon.value = 'success'
        messageDialogTitle.value = 'SUCCESS'
        messageDialogMessage.value = 'Schema saved successfully.'
    } else {
        showMessageDialog.value = true
        messageDialogButtons.value = Buttons.ok
        messageDialogIcon.value = 'error'
        messageDialogTitle.value = 'ERROR'
        messageDialogMessage.value = 'Could not save schema.'
    }

    readOnly.value = true
    editMode.value = false
    insertMode.value = false
    isDataDirty.value = false
    await store.loadData(clusterId, props.subject, 'latest')
}

function downloadSelectedSchema() {
    downloadFile(
        currentlyDisplayedSchema.value as string,
        props.subject + '-version-' + selectedSchemaVersion.value,
        schemaType.value
    )
}

function afterCompatibilityUpdate(applicationEvent: ApplicationEvent) {
    if (subjectThatIsInProcess.value !== props.subject) {
        return
    }

    if (applicationEvent.success) {
        showCompatibilitySettingForm.value = false
        showMessageDialog.value = true
        messageDialogButtons.value = Buttons.ok
        messageDialogIcon.value = 'success'
        messageDialogTitle.value = 'SUCCESS'
        messageDialogMessage.value = 'Schema compatibility saved successfully.'
    } else {
        showMessageDialog.value = true
        messageDialogButtons.value = Buttons.ok
        messageDialogIcon.value = 'error'
        messageDialogTitle.value = 'ERROR - ' + (applicationEvent.data as ErrorResponse).httpCode
        messageDialogMessage.value = 'Could not save Schema compatibility.'
    }
}

function afterSchemaVersionDelete(applicationEvent: ApplicationEvent) {
    const isSubjectDelete = applicationEvent.eventType === ApplicationEventTypes.SUBJECT_DELETE
    if (subjectThatIsInProcess.value !== props.subject) {
        return
    }
    if (applicationEvent.success) {
        showDeleteSchemaForm.value = false
        showMessageDialog.value = true
        messageDialogButtons.value = Buttons.ok
        messageDialogIcon.value = 'success'
        messageDialogTitle.value = 'SUCCESS'
        messageDialogMessage.value = !isSubjectDelete
            ? 'Schema version deleted successfully.'
            : 'Subject schema deleted successfully.'
    } else {
        const errorResponse = applicationEvent.data as ErrorResponse
        showMessageDialog.value = true
        messageDialogButtons.value = Buttons.ok
        messageDialogIcon.value = 'error'
        messageDialogTitle.value = 'ERROR - ' + errorResponse.httpCode
        messageDialogMessage.value =
            errorResponse.httpCode === 422
                ? 'ERROR ' +
                  (errorResponse.errors as GenericError).detail.errorCode +
                  ' ' +
                  (errorResponse.errors as GenericError).detail.message
                : 'Could not perform deletion.'
    }
}

onMounted(async () => {
    eventBus.on(ApplicationEventTypes.SCHEMA_READY, afterDataLoad)
    eventBus.on(ApplicationEventTypes.BEFORE_SCHEMA_READY, beforeDataLoad)
    eventBus.on(ApplicationEventTypes.SCHEMA_DATA_LOADED, afterDataLoad)
    eventBus.on(ApplicationEventTypes.SCHEMA_SAVE, afterSchemaUpdate)
    eventBus.on(ApplicationEventTypes.SCHEMA_COMPATIBILITY_UPDATE, afterCompatibilityUpdate)
    eventBus.on(ApplicationEventTypes.SCHEMA_VERSION_DELETE, afterSchemaVersionDelete)
    eventBus.on(ApplicationEventTypes.SUBJECT_DELETE, afterSchemaVersionDelete)

    await store.loadData(clusterId, props.subject, 'latest')
})

onUnmounted(async () => {
    eventBus.off(ApplicationEventTypes.SCHEMA_READY, afterDataLoad)
    eventBus.off(ApplicationEventTypes.BEFORE_SCHEMA_READY, afterDataLoad)
    eventBus.off(ApplicationEventTypes.SCHEMA_DATA_LOADED, afterDataLoad)
    eventBus.off(ApplicationEventTypes.SCHEMA_SAVE, afterSchemaUpdate)
    eventBus.off(ApplicationEventTypes.SCHEMA_COMPATIBILITY_UPDATE, afterCompatibilityUpdate)
    eventBus.off(ApplicationEventTypes.SCHEMA_VERSION_DELETE, afterSchemaVersionDelete)
    eventBus.off(ApplicationEventTypes.SUBJECT_DELETE, afterSchemaVersionDelete)
})
</script>

<style scoped>
.action-menu div {
    display: none;
}

.action-menu:hover div,
.action-menu button:focus + div {
    display: block;
}

.action-menu:hover button {
    @apply bg-green-100 dark:border-gray-300 dark:bg-gray-600 dark:text-gray-100 dark:shadow-black;
}
</style>
