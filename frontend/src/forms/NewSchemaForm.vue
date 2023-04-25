<template>
    <div class="flex flex-col">
        <Loading v-if="loading">Saving please wait...</Loading>
        <div class="my-4 md:flex md:items-center">
            <div class="w-32">Schema Type</div>
            <BaseSelectField
                label="Schema type"
                class="w-64"
                :hide-label="true"
                v-model="selectedType"
                :options="typeOptions"
            ></BaseSelectField>
        </div>
        <div class="flex flex-1 flex-col">
            <div v-if="compatibilityErrorCode !== -1" class="m-4 flex flex-col flex-nowrap rounded shadow">
                <div class="rounded-t bg-red-500 p-2 text-white dark:bg-red-900">
                    Schema Compatibility Error (Error code {{ compatibilityErrorCode }})
                </div>
                <div class="flex flex-nowrap overflow-auto rounded-b bg-red-50 p-2 dark:bg-red-900/25">
                    <pre class="w-32 flex-1 break-all">{{ compatibilityError }}</pre>
                </div>
            </div>
            <code-mirror
                :key="codeMirrorKey"
                :errors="errors"
                :schema-type="selectedType"
                @change="value = $event"
                :value="value"
                class="flex-1 md:pr-8"
            ></code-mirror>
            <SchemaReference
                :key="referenceKey"
                :subject="subject"
                :errors="errors"
                class="min-h-20 mt-2 md:flex-1"
                v-model="references"
                :schemaType="selectedType"
            ></SchemaReference>
        </div>
        <div class="sticky bottom-0 flex">
            <div
                style="box-shadow: 0 -10px 14px -10px rgba(0, 0, 0, 0.1)"
                class="flex w-full justify-between border-t bg-gray-100 px-4 pt-2 dark:border-gray-900 dark:bg-gray-800"
            >
                <button
                    class="w-24 rounded bg-red-500 px-4 py-2 text-white transition duration-200 ease-linear hover:bg-red-700"
                    @click="emit('cancel')"
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
import { computed, defineEmits, defineProps, type PropType, ref, watch } from 'vue'
import CheckAll from '@/icons/CheckAll.vue'
import { schemaStore } from '@/stores/SchemaStore'
import BaseButton from '@/components/BaseButton.vue'
import Ripple from '@/components/Ripple.vue'
import { haveErrors, validate } from '@/validators/SchemaValidator'
import CodeMirror from '@/components/CodeMirror.vue'
import SchemaReference from '@/components/SchemaReference.vue'
import BaseSelectField from '@/components/BaseSelectField.vue'
import { getId } from '@/util/Util'
import type { SchemaReference as SchemaReferenceEntity } from '@/entity/SchemaReference'
import type { SubjectSchemaDetails } from '@/entity/SubjectSchemaDetails'
import type { ServerResponse } from '@/entity/ServerResponse'
import type { SchemaCompatibilityError } from '@/entity/SchemaCompatibilityError'
import Loading from '@/components/Loading.vue'
import { useRoute } from 'vue-router'

const clusterId = useRoute().params.clusterId as string
const store = schemaStore()
const errors = ref<Map<string, string>>(new Map<string, string>())
const references = ref<SchemaReferenceEntity[]>([])
const props = defineProps({
    subject: {
        type: String as PropType<string>,
        required: true,
    },
    loading: {
        type: Boolean as PropType<boolean>,
        required: false,
        default: false,
    },
})

const emit = defineEmits<{
    (e: 'cancel'): void
    (e: 'submit', value: SubjectSchemaDetails): void
}>()

const codeMirrorKey = ref<string>(getId())
const referenceKey = ref<string>(getId())
const typeOptions = ref<{ label: string; value: string }[]>([
    {
        label: 'AVRO',
        value: 'AVRO',
    },
    {
        label: 'JSON',
        value: 'JSON',
    },
    {
        label: 'PROTOBUF',
        value: 'PROTOBUF',
    },
])

const isDataDirty = ref<boolean>(false)
const compatibilityErrorCode = ref<number>(-1)
const compatibilityError = ref<string>('')
const selectedType = ref<'AVRO' | 'JSON' | 'PROTOBUF'>('AVRO')
const sampleSchemas: Record<string, string> = {
    protobufSampleSchema: `syntax = "proto3";

package com.mycorp.mynamespace;

/* Sample schema to help you get started. */

message SampleRecord {
    int32  my_field1 = 1;
    double  my_field2 = 2;
    string  my_field3 = 3;
}`,
    avroSampleSchema: `{
    "type": "record",
    "namespace": "com.mycorp.mynamespace",
    "name": "sampleRecord",
    "doc": "Sample schema to help you get started.",
    "fields": [
        {
            "name": "my_field1",
            "type": "int",
            "doc": "The int type is a 32-bit signed integer."
        },
        {
            "name": "my_field2",
            "type": "double",
            "doc": "The double type is a double precision (64-bit) IEEE 754 floating-point number."
        },
        {
            "name": "my_field3",
            "type": "string",
            "doc": "The string is a unicode character sequence."
        }
    ]
}`,
    jsonSampleSchema: `{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "$id": "http://example.com/myURI.schema.json",
    "title": "SampleRecord",
    "description": "Sample schema to help you get started.",
    "type": "object",
    "additionalProperties": false,
    "properties": {
        "myField1": {
            "type": "integer",
            "description": "The integer type is used for integral numbers."
        },
        "myField2": {
            "type": "number",
            "description": "Number type is used for any numeric type, either integers or floating point numbers."
        },
        "myField3": {
            "type": "string",
            "description": "The string type is used for strings of text."
        }
    }
}`,
}

const value = ref<string>(sampleSchemas.avroSampleSchema.toString())

const computedSubjectSchemaDetails = computed<SubjectSchemaDetails>(() => {
    return {
        subject: props.subject,
        version: -1,
        id: -1,
        schema: value.value ?? '',
        schemaType: selectedType.value,
        references: references.value,
    }
})

watch(selectedType, (newSchemaType: 'AVRO' | 'JSON' | 'PROTOBUF') => changeLanguage(newSchemaType))

watch(value, (newValue: string) => {
    if (selectedType.value === 'AVRO') {
        sampleSchemas.avroSampleSchema = newValue
    } else if (selectedType.value === 'JSON') {
        sampleSchemas.jsonSampleSchema = newValue
    } else {
        sampleSchemas.protobufSampleSchema = newValue
    }
})

watch(
    computedSubjectSchemaDetails,
    () => {
        if (isDataDirty.value) {
            validateSchema()
        }
    },
    { deep: true }
)

function changeLanguage(newSchemaType: 'AVRO' | 'JSON' | 'PROTOBUF') {
    value.value = sampleSchemas[newSchemaType.toString().toLowerCase() + 'SampleSchema']
    codeMirrorKey.value = getId()
    references.value = []
    referenceKey.value = getId()
}

function validateSchema() {
    errors.value = validate(computedSubjectSchemaDetails.value)
    compatibilityErrorCode.value = -1
    compatibilityError.value = ''
    isDataDirty.value = true
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
            }
        },
        (error) => {
            if (error.httpCode === 422) {
                compatibilityError.value = (error.errors as SchemaCompatibilityError).detail.message
                compatibilityErrorCode.value = (error.errors as SchemaCompatibilityError).detail.errorCode
            } else {
                compatibilityError.value = error.response
                compatibilityErrorCode.value = error.httpCode
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

    emit('submit', computedSubjectSchemaDetails.value)
}
</script>
