<template>
    <div class="absolute left-0 top-0 z-20 flex h-full w-full flex-row-reverse overflow-hidden" ref="root">
        <slot name="dialog"></slot>
        <transition name="fade" @after-enter="displayContent = true" @after-leave="$emit('close')">
            <div class="absolute left-0 top-0 h-full w-full" v-if="displayOverlay" @click="hide">
                <div class="absolute left-0 top-0 h-full w-full" :class="overlayClass"></div>
            </div>
        </transition>
        <transition name="slide-left" @after-leave="displayOverlay = false">
            <div
                class="absolute flex h-full flex-col overflow-hidden bg-transparent"
                :class="widthClassConfiguration"
                ref="innerContent"
                v-if="displayContent"
            >
                <div class="relative flex flex-grow flex-col rounded shadow-lg md:m-2">
                    <Loading v-if="loading">{{ loadingMessage }}</Loading>
                    <div class="flex items-center justify-between rounded-t bg-green-500 px-2 py-2 dark:bg-gray-600">
                        <div class="select-none font-bold text-white dark:text-gray-100">{{ title }}</div>
                        <div class="flex items-center">
                            <div title="Cancel/Close" class="cursor-pointer rounded-full">
                                <CloseIcon
                                    class="h-6 w-6 fill-current text-white dark:text-gray-100"
                                    @click="hide"
                                ></CloseIcon>
                            </div>
                        </div>
                    </div>
                    <div class="flex flex-grow rounded-b">
                        <slot></slot>
                    </div>
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
import { nextTick, onMounted, type PropType, ref, watch } from 'vue'
import Loading from '@/components/Loading.vue'
import CloseIcon from '@/icons/CloseIcon.vue'

const props = defineProps({
    title: {
        type: String as PropType<string>,
        required: true,
    },
    loading: {
        type: Boolean as PropType<boolean>,
        required: true,
    },
    loadingMessage: {
        type: String as PropType<string>,
        required: false,
        default: 'Processing...',
    },
    widthClassConfiguration: {
        type: String as PropType<string>,
        required: false,
        default: 'w-full md:w-8/12 lg:w-5/12',
    },
    overlayClass: {
        type: String as PropType<string>,
        required: false,
        default: 'bg-green-100 opacity-50 dark:bg-gray-700 rounded',
    },
})

const emit = defineEmits<{ (e: 'close'): void; (e: 'open'): void }>()
const displayContent = ref<boolean>(false)
const displayOverlay = ref<boolean>(false)
const root = ref<Element | null>(null)
const innerContent = ref<Element | null>(null)

function hide() {
    if (!props.loading) {
        displayContent.value = false
    }
}

defineExpose({
    hide,
})

watch(displayContent, (newDisplayContent) => {
    if (newDisplayContent) {
        emit('open')
    }
})

onMounted(() => {
    nextTick(() => {
        displayOverlay.value = true
    }).then()
})
</script>
