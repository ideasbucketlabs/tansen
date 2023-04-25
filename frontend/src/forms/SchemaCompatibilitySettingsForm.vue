<template>
    <FlyInPanel :loading="loading" title="Change compatibility setting(s)" @close="$emit('close')" ref="flyInPanel">
        <div class="flex flex-grow flex-col overflow-hidden dark:text-gray-100">
            <form class="flex flex-grow flex-col items-stretch" @submit.prevent="submit" novalidate>
                <div class="flex h-32 flex-grow flex-col overflow-y-auto bg-white dark:bg-gray-900">
                    <div class="flex flex-grow flex-col">
                        <div class="flex flex-col">
                            <div class="">
                                <base-radio-field
                                    v-model="currentCompatibility"
                                    :value="CompatibilityType.BACKWARD"
                                    label="Backward"
                                    un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                    selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800 rounded"
                                    class="mx-4 mb-2 mt-4 p-4"
                                >
                                    <span class="italic text-gray-600 dark:text-gray-300"
                                        >Consumers using the new schema can read data written by producers using the
                                        latest registered schema.</span
                                    >
                                </base-radio-field>
                            </div>
                            <div class="">
                                <base-radio-field
                                    v-model="currentCompatibility"
                                    :value="CompatibilityType.BACKWARD_TRANSITIVE"
                                    label="Transitive Backward"
                                    un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                    selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800 rounded"
                                    class="mx-4 mb-2 mt-4 p-4"
                                >
                                    <span class="italic text-gray-600 dark:text-gray-300"
                                        >Consumers using the new schema can read data written by producers using the
                                        latest registered schema.</span
                                    >
                                </base-radio-field>
                            </div>
                            <div class="">
                                <base-radio-field
                                    v-model="currentCompatibility"
                                    :value="CompatibilityType.FORWARD"
                                    label="Forward"
                                    un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                    selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800 rounded"
                                    class="mx-4 mb-2 mt-4 p-4"
                                >
                                    <span class="italic text-gray-600 dark:text-gray-300"
                                        >Consumers using the latest registered schema can read data written by producers
                                        using the new schema.</span
                                    >
                                </base-radio-field>
                            </div>
                            <div class="">
                                <base-radio-field
                                    v-model="currentCompatibility"
                                    :value="CompatibilityType.FORWARD_TRANSITIVE"
                                    label="Transitive Forward"
                                    un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                    selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800 rounded"
                                    class="mx-4 mb-2 mt-4 p-4"
                                >
                                    <span class="italic text-gray-600 dark:text-gray-300">
                                        Consumers using all the previously registered schemas can read data written by
                                        producers using the new schema.
                                    </span>
                                </base-radio-field>
                            </div>
                            <div class="">
                                <base-radio-field
                                    v-model="currentCompatibility"
                                    :value="CompatibilityType.FULL"
                                    label="Full"
                                    un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                    selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800 rounded"
                                    class="mx-4 mb-2 mt-4 p-4"
                                >
                                    <span class="italic text-gray-600 dark:text-gray-300">
                                        The new schema is forward and backward compatible with the latest registered
                                        schema.
                                    </span>
                                </base-radio-field>
                            </div>
                            <div class="">
                                <base-radio-field
                                    v-model="currentCompatibility"
                                    :value="CompatibilityType.FULL_TRANSITIVE"
                                    label="Transitive Full"
                                    un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                    selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800 rounded"
                                    class="mx-4 mb-2 mt-4 p-4"
                                >
                                    <span class="italic text-gray-600 dark:text-gray-300">
                                        The new schema is forward and backward compatible with all previously registered
                                        schemas.
                                    </span>
                                </base-radio-field>
                            </div>
                            <div class="">
                                <base-radio-field
                                    v-model="currentCompatibility"
                                    :value="CompatibilityType.NONE"
                                    label="None"
                                    un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                    selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800 rounded"
                                    class="mx-4 mb-2 mt-4 p-4"
                                >
                                    <span class="italic text-gray-600 dark:text-gray-300">
                                        Schema compatibility checks are disabled.
                                    </span>
                                </base-radio-field>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="shadow-top rounded-b bg-green-100 py-2 dark:bg-gray-700">
                    <BaseButton
                        class="mx-auto block h-10 w-8/12 lg:w-4/12"
                        type="submit"
                        :disabled="currentCompatibility === compatibility"
                        :loading="loading"
                        label="Save"
                    ></BaseButton>
                </div>
            </form>
        </div>
    </FlyInPanel>
</template>

<script lang="ts" setup>
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineEmits, defineProps, onMounted, onUnmounted, type PropType, ref } from 'vue'
import BaseButton from '@/components/BaseButton.vue'
import FlyInPanel from '@/components/FlyInPanel.vue'
import { CompatibilityType } from '@/entity/CompatibilityType'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import BaseRadioField from '@/components/BaseRadioField.vue'

const props = defineProps({
    subject: {
        type: String as PropType<string>,
        required: true,
    },
    compatibility: {
        type: String as PropType<CompatibilityType>,
        required: true,
    },
})

const loading = ref<boolean>(false)
const emit = defineEmits<{
    (e: 'close'): void
    (e: 'submit', value: CompatibilityType): void
}>()

const currentCompatibility = ref<CompatibilityType>(
    CompatibilityType[
        props.compatibility.toString() as
            | 'BACKWARD'
            | 'BACKWARD_TRANSITIVE'
            | 'FORWARD'
            | 'FORWARD_TRANSITIVE'
            | 'FULL'
            | 'FULL_TRANSITIVE'
            | 'NONE'
    ]
)

function submit() {
    emit('submit', currentCompatibility.value)
}

function beforeUpdate() {
    loading.value = true
}

function afterUpdate() {
    loading.value = false
}

onMounted(async () => {
    eventBus.on(ApplicationEventTypes.SCHEMA_COMPATIBILITY_UPDATE, afterUpdate)
    eventBus.on(ApplicationEventTypes.BEFORE_SCHEMA_COMPATIBILITY_UPDATE, beforeUpdate)
})

onUnmounted(async () => {
    eventBus.off(ApplicationEventTypes.SCHEMA_COMPATIBILITY_UPDATE, afterUpdate)
    eventBus.off(ApplicationEventTypes.BEFORE_SCHEMA_COMPATIBILITY_UPDATE, beforeUpdate)
})
</script>
