<template>
    <FlyInPanel :loading="loading" title="What do you want to delete?" @close="$emit('close')" ref="flyInPanel">
        <div class="flex flex-grow flex-col overflow-hidden dark:text-gray-100">
            <form class="flex flex-grow flex-col items-stretch" @submit.prevent="submit" novalidate>
                <div class="flex h-32 flex-grow flex-col overflow-y-auto bg-white dark:bg-gray-900">
                    <div class="flex flex-grow flex-col">
                        <div class="flex flex-col p-4">
                            <div v-for="version in versionList" :key="version.value" class="">
                                <label
                                    class="mx-4 flex cursor-pointer items-center space-x-4 rounded p-2 transition duration-200 ease-linear"
                                >
                                    <input
                                        class="text-blue-500 focus:ring-2 focus:ring-blue-500 dark:bg-gray-100"
                                        type="radio"
                                        name="compatibility"
                                        v-model="selectedVersion"
                                        :value="version.value"
                                    />
                                    <span class="block font-bold">{{ version.label }}</span>
                                </label>
                            </div>
                            <div class="mt-4 border-t pt-4">
                                <label
                                    class="mx-4 flex cursor-pointer items-center space-x-4 rounded p-2 transition duration-200 ease-linear"
                                >
                                    <input
                                        class="text-blue-500 focus:ring-2 focus:ring-blue-500 dark:bg-gray-100"
                                        type="radio"
                                        name="compatibility"
                                        v-model="selectedVersion"
                                        :value="0"
                                    />
                                    <span class="block font-bold"
                                        >Delete the entire subject including all versions</span
                                    >
                                </label>
                            </div>
                            <div
                                class="mt-8 rounded border border-red-500"
                                :class="[hardDelete ? 'bg-red-500 dark:bg-red-700' : '']"
                            >
                                <label
                                    class="mx-4 flex cursor-pointer select-none items-center space-x-4 rounded p-2 transition duration-200 ease-linear"
                                >
                                    <input
                                        class="text-blue-500 focus:ring-2 focus:ring-blue-500 dark:bg-gray-100"
                                        type="checkbox"
                                        name="hard_delete"
                                        v-model="hardDelete"
                                        :value="true"
                                    />
                                    <span class="block font-bold" :class="[hardDelete ? 'text-white' : 'text-red-500']"
                                        >Hard delete</span
                                    >
                                </label>
                            </div>
                            <div class="mt-6 flex rounded bg-blue-500 text-white dark:bg-blue-800 dark:text-gray-100">
                                <div class="flex w-16 items-center">
                                    <InformationIcon class="fill-current"></InformationIcon>
                                </div>
                                <div class="px- py-4">
                                    <div>Please note:</div>
                                    <div>
                                        Hard delete will free up the space but will make recovery impossible. So, please
                                        use caution.
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="shadow-top rounded-b bg-green-100 py-2 dark:bg-gray-700">
                    <BaseButton
                        class="mx-auto block h-10 w-8/12 lg:w-4/12"
                        type="submit"
                        :disabled="selectedVersion === -1"
                        :loading="loading"
                        label="Delete"
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
import { onMounted, onUnmounted, type PropType, ref } from 'vue'
import BaseButton from '@/components/BaseButton.vue'
import FlyInPanel from '@/components/FlyInPanel.vue'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import InformationIcon from '@/icons/InformationIcon.vue'

defineProps({
    versionList: {
        type: Array as PropType<{ label: string; value: number }[]>,
        required: true,
    },
})

const loading = ref<boolean>(false)
const selectedVersion = ref<number>(-1)
const hardDelete = ref<boolean>(false)
const emit = defineEmits<{
    (e: 'close'): void
    (e: 'submit', selectedVersion: number, hardDelete: boolean): void
}>()

function submit() {
    emit('submit', parseInt(selectedVersion.value.toString(), 10), hardDelete.value)
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
