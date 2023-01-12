<template>
    <div
        class="absolute top-0 left-0 z-30 flex h-full w-full content-center items-center justify-center overflow-hidden"
    >
        <transition name="fade" @after-enter="displayContent = true" @after-leave="onClose">
            <div class="absolute left-0 top-0 h-full w-full" v-if="displayOverlay">
                <div class="absolute left-0 top-0 h-full w-full bg-green-100 opacity-50 dark:bg-gray-700"></div>
            </div>
        </transition>
        <transition name="fade-bottom" @after-leave="displayOverlay = false">
            <div
                class="absolute flex w-full flex-col overflow-hidden rounded border border-green-300 bg-transparent shadow-lg dark:border-gray-800 dark:shadow-gray-900 md:w-8/12 lg:w-5/12 xl:w-4/12"
                v-if="displayContent"
            >
                <div
                    v-if="title"
                    class="flex items-center justify-between rounded-t border-b border-green-300 bg-green-500 px-2 py-2 dark:border-gray-800 dark:bg-gray-700"
                >
                    <div class="select-none font-bold text-white dark:text-gray-100">{{ title }}</div>
                </div>
                <div class="flex flex-grow flex-col">
                    <slot></slot>
                </div>
            </div>
        </transition>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineEmits, defineProps, nextTick, defineExpose, onMounted, ref, withDefaults } from 'vue'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'

withDefaults(defineProps<{ title: string | null }>(), { title: null })

const displayContent = ref<boolean>(false)
const displayOverlay = ref<boolean>(false)

const emit = defineEmits<{ (e: 'close'): void }>()

function hide() {
    displayContent.value = false
}

function onClose() {
    eventBus.emit(ApplicationEventTypes.DIALOG_CLOSE, true)
    emit('close')
}

defineExpose({
    hide,
})

onMounted(() => {
    nextTick(() => {
        displayOverlay.value = true
    })
})
</script>
