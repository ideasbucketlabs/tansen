<template>
    <div class="relative flex flex-col">
        <Loading v-if="loading"></Loading>
        <div>
            <h1 class="text-xl font-bold">Schema references</h1>
            <p>
                When referencing a schema (in a field) from a main schema, you need to provide the associated reference
                to register the main schema.
            </p>
        </div>
        <div class="p-4">
            <div
                v-for="(reference, index) in references"
                :key="'reference-schema-' + index"
                class="overflow-y-auto md:mt-2 md:flex md:flex-col md:space-x-2 md:space-y-0 md:space-y-2"
            >
                <div class="font-bold">Reference {{ index + 1 }}</div>
                <div class="space-y-2 overflow-y-auto md:mt-2 md:flex md:items-center md:space-x-2 md:space-y-0">
                    <div class="md:flex md:flex-1 md:flex-col md:p-1">
                        <div class="flex items-center">
                            <div>Reference name *</div>
                            <div
                                class="ml-1 w-4 cursor-help rounded-full border border-green-500 dark:border-gray-200"
                                v-bind:title="referenceTitle"
                            >
                                <question-icon class="text-green-500 dark:text-gray-100" />
                            </div>
                        </div>
                        <BaseInputField
                            label="Reference name"
                            :has-error="hasError(errors, 'name', index)"
                            :hideLabel="true"
                            class="h-20"
                            v-model="reference.name"
                            ><span class="text-red-500" v-if="hasError(errors, 'name', index)">{{
                                getError(errors, 'name', index)
                            }}</span></BaseInputField
                        >
                    </div>
                    <div class="md:flex md:flex-1 md:flex-col">
                        <div class="flex items-center">
                            <div>Subject *</div>
                            <div
                                class="ml-1 w-4 cursor-help rounded-full border border-green-500 dark:border-gray-200"
                                title="Subject name for the reference"
                            >
                                <question-icon class="text-green-500 dark:text-gray-100" />
                            </div>
                        </div>
                        <BaseSelectField
                            label="Subject"
                            class="h-20"
                            :has-error="hasError(errors, 'subject', index)"
                            v-model="reference.subject"
                            :hide-label="true"
                            :options="subjects"
                            ><span class="text-red-500" v-if="hasError(errors, 'subject', index)">{{
                                getError(errors, 'subject', index)
                            }}</span></BaseSelectField
                        >
                    </div>
                    <div class="md:flex md:flex-1 md:flex-col">
                        <div class="flex items-center">
                            <div>Version *</div>
                            <div
                                class="ml-1 w-4 cursor-help rounded-full border border-green-500 dark:border-gray-200"
                                title="Version of the reference"
                            >
                                <question-icon class="text-green-500 dark:text-gray-100" />
                            </div>
                        </div>
                        <BaseSelectField
                            label="Version"
                            class="h-20"
                            :disabled="getVersionsListBySubject(reference.subject, index).length === 0"
                            :has-error="hasError(errors, 'version', index)"
                            v-model="reference.version"
                            :hide-label="true"
                            :options="getVersionsListBySubject(reference.subject, index)"
                            ><span class="text-red-500" v-if="hasError(errors, 'version', index)">{{
                                getError(errors, 'version', index)
                            }}</span></BaseSelectField
                        >
                    </div>
                    <div class="md:flex md:flex-col">
                        <button
                            class="relative mb-4 flex items-center justify-center overflow-hidden rounded border border-green-500 border-red-500 bg-transparent px-4 py-2 text-red-500 transition duration-200 ease-linear hover:bg-red-100 hover:shadow dark:border-red-500 dark:hover:bg-gray-700 dark:hover:shadow-black md:mb-1"
                            @click="removeReference(index)"
                        >
                            <ripple ripple-class="bg-red-200 dark:bg-red-500 opacity-25"></ripple>
                            <span class="mr-2 w-4"
                                ><minus-icon class="w-full fill-current text-red-500"></minus-icon
                            ></span>
                            <span>Remove</span>
                        </button>
                    </div>
                </div>
            </div>
            <button
                class="relative mb-14 mt-4 flex items-center justify-center overflow-hidden rounded border border-green-500 border-green-500 px-4 py-2 text-green-500 transition duration-200 ease-linear hover:bg-green-100 hover:shadow dark:hover:bg-gray-700 dark:hover:shadow-black"
                @click="addReference"
            >
                <ripple ripple-class="bg-green-200 dark:bg-green-200 opacity-25"></ripple>
                <span class="mr-2 w-4"><plus-icon class="w-full fill-current text-green-500"></plus-icon></span>
                <span>Add Reference</span>
            </button>
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
import { computed, onMounted, onUnmounted, type PropType, ref, watch } from 'vue'
import { schemaStore } from '@/stores/SchemaStore'
import type { SchemaReference } from '@/entity/SchemaReference'
import PlusIcon from '@/icons/PlusIcon.vue'
import Ripple from '@/components/Ripple.vue'
import BaseInputField from '@/components/BaseInputField.vue'
import MinusIcon from '@/icons/MinusIcon.vue'
import QuestionIcon from '@/icons/QuestionIcon.vue'
import eventBus from '@/util/EventBus'
import { boldWord, clone } from '@/util/Util'
import Loading from '@/components/Loading.vue'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import BaseSelectField from '@/components/BaseSelectField.vue'
import { getError, hasError } from '@/validators/SchemaValidator'
import { useRoute } from 'vue-router'

const clusterId = useRoute().params.clusterId as string
const store = schemaStore()
const props = defineProps({
    subject: {
        type: String as PropType<string>,
        required: true,
    },
    schemaType: {
        type: String as PropType<'JSON' | 'AVRO' | 'PROTOBUF'>,
        required: true,
    },
    modelValue: {
        required: true,
        type: Array as PropType<SchemaReference[]>,
    },
    errors: {
        required: true,
        type: Object as PropType<Map<string, string>>,
    },
})

const loading = ref<boolean>(true)
const references = ref<SchemaReference[]>(clone(props.modelValue))
const referenceTitle = ref<string>(
    // eslint-disable-next-line max-len
    "In JSON, it's the value on the $" +
        boldWord('ref') +
        " field. \u000AIn Avro, it's the value on the " +
        boldWord('type') +
        " field. \u000AIn Protobuf, it's the value on the " +
        boldWord('import statement')
)
const haveExistingReference = ref<boolean>(references.value.length !== 0)

const emit = defineEmits<{ (e: 'update:modelValue', value: SchemaReference[]): void }>()

const subjects = computed<{ label: string; value: number | string }[]>(() => {
    const result = (store.getSubjectsByType(clusterId, props.schemaType) ?? [])
        .filter((it) => {
            return it !== props.subject
        })
        .map((it) => {
            return {
                label: it,
                value: it,
            }
        })

    result.push({
        label: '',
        value: '',
    })

    return result
})

function removeReference(index: number): void {
    references.value.splice(index, 1)
}

function addReference(): void {
    references.value.push({
        name: '',
        subject: '',
        version: -1,
    })
}

function afterDataLoad() {
    loading.value = false
}

function beforeDataLoad() {
    loading.value = true
}

function getVersionsListBySubject(subject: string, index: number): { label: string; value: number }[] {
    if (!store.isSubjectVersionsListLoaded(clusterId, subject)) {
        store.loadSubjectVersionsList(clusterId, subject).then()
    }

    if (references.value[index].version === undefined || references.value[index].version === -1) {
        // Select the last index.
        references.value[index].version = store.getSubjectVersionsList(clusterId, subject).slice().reverse()[0]
    }

    return store.getSubjectVersionsList(clusterId, subject).map((it) => {
        return {
            label: it.toString(),
            value: it,
        }
    })
}

async function loadSchemasByType(type: 'JSON' | 'AVRO' | 'PROTOBUF') {
    await store.loadSchemaByType(clusterId, type)
}

watch(
    references,
    (newReferences: SchemaReference[]) => {
        // Not sure why cloning is again necessary here. But without this parent is not picking up changes all the time.
        emit('update:modelValue', clone(newReferences))
    },
    { deep: true }
)

watch(
    () => props.schemaType,
    (newSchemaType) => loadSchemasByType(newSchemaType),
    { immediate: false }
)

onMounted(async () => {
    eventBus.on(ApplicationEventTypes.BEFORE_SUBJECTS_BY_TYPE_LOADED, beforeDataLoad)
    eventBus.on(ApplicationEventTypes.SUBJECTS_BY_TYPE_LOADED, afterDataLoad)
    if (haveExistingReference.value) {
        await Promise.all(references.value.map((it) => store.loadSubjectVersionsList(clusterId, it.subject)))
    }
    await loadSchemasByType(props.schemaType)
})

onUnmounted(() => {
    eventBus.off(ApplicationEventTypes.BEFORE_SUBJECTS_BY_TYPE_LOADED, beforeDataLoad)
    eventBus.off(ApplicationEventTypes.SUBJECTS_BY_TYPE_LOADED, afterDataLoad)
})
</script>
