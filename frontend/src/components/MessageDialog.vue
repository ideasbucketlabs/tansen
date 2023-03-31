<template>
    <Dialog :title="title" ref="dialog">
        <div class="flex flex-col rounded-t">
            <div
                class="flex flex-col content-center items-center bg-white p-2 dark:bg-gray-800 sm:flex-row"
                style="min-height: 100px"
            >
                <div v-if="haveIcon" class="w-16 sm:mr-4">
                    <div
                        v-if="isIcon('info')"
                        class="flex items-center justify-center rounded-full bg-blue-500 text-5xl text-white"
                    >
                        <div class="w-full">
                            <InformationIcon class="h-16 w-16 fill-current"></InformationIcon>
                        </div>
                    </div>
                    <AlertIcon v-else-if="isIcon('alert')" class="h-16 w-16"></AlertIcon>
                    <ErrorIcon v-else-if="isIcon('error')" class="h-16 w-16"></ErrorIcon>
                    <div
                        v-else-if="isIcon('question')"
                        class="flex h-16 w-16 items-center justify-center rounded-full bg-blue-500 text-5xl text-white"
                    >
                        <QuestionIcon class="h-16 w-16"></QuestionIcon>
                    </div>
                    <SuccessIcon v-else class="h-16 w-16"></SuccessIcon>
                </div>
                <div class="mt-4 flex-1 select-none sm:mt-0">
                    <slot></slot>
                </div>
            </div>
            <div
                class="flex justify-center rounded-b border-t border-green-300 bg-green-100 p-2 dark:border-gray-800 dark:bg-gray-700"
            >
                <button
                    v-if="renderOkButton"
                    type="button"
                    class="relative mx-2 h-9 w-20 rounded bg-green-500 font-bold text-white transition duration-500 hover:bg-green-600 hover:shadow-lg focus:outline-none focus:ring-0 dark:bg-gray-800 dark:hover:bg-gray-900"
                    @click="onClick('ok')"
                >
                    OK
                </button>
                <button
                    v-if="renderYesButton"
                    type="button"
                    class="relative mx-2 h-9 w-20 rounded bg-green-500 font-bold text-white transition duration-500 hover:bg-green-600 hover:shadow-lg focus:outline-none focus:ring-0"
                    @click="onClick('yes')"
                >
                    Yes
                </button>
                <button
                    v-if="renderNoButton"
                    type="button"
                    class="mx-2 h-9 w-20 rounded border border-blue-300 hover:shadow-lg focus:outline-none focus:ring-0 dark:border-gray-800"
                    @click="onClick('no')"
                >
                    No
                </button>
                <button
                    v-if="renderCancelButton"
                    type="button"
                    class="mx-2 h-9 w-20 rounded border border-blue-300 hover:shadow-lg focus:outline-none focus:ring-0 dark:border-gray-800"
                    @click="onClick('cancel')"
                >
                    Cancel
                </button>
                <button
                    v-if="renderDeleteButton"
                    type="button"
                    class="mx-2 h-9 w-20 rounded bg-red-600 text-white hover:shadow-lg focus:outline-none focus:ring-0"
                    @click="onClick('delete')"
                >
                    Delete
                </button>
                <button
                    v-if="renderDeleteDisabledButton"
                    type="button"
                    class="mx-2 h-9 w-20 rounded bg-gray-600 text-white hover:cursor-default focus:outline-none focus:ring-0"
                >
                    Delete
                </button>
            </div>
        </div>
    </Dialog>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import Dialog from '@/components/Dialog.vue'
import { Buttons } from '@/entity/Buttons'
import eventBus from '@/util/EventBus'
import { computed, defineEmits, defineProps, type PropType, ref } from 'vue'
import type { MessageDialogEvent } from '@/entity/MessageDialogEvent'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import QuestionIcon from '@/icons/QuestionIcon.vue'
import InformationIcon from '@/icons/InformationIcon.vue'
import AlertIcon from '@/icons/AlertIcon.vue'
import ErrorIcon from '@/icons/ErrorIcon.vue'
import SuccessIcon from '@/icons/SuccessIcon.vue'

const props = defineProps({
    data: {
        type: Object as PropType<unknown>,
        required: false,
        default() {
            return {}
        },
    },
    title: {
        type: String as PropType<string | null>,
        required: false,
        default: null,
    },
    icon: {
        type: String as PropType<'info' | 'question' | 'alert' | 'error' | 'success' | null>,
        required: false,
        default: null,
        validator(value: string) {
            return (
                value === 'info' ||
                value === 'question' ||
                value === 'alert' ||
                value === 'error' ||
                value === 'success'
            )
        },
    },
    buttons: {
        type: Number as PropType<Buttons>,
        required: false,
        default: Buttons.ok,
    },
})

const dialog = ref<typeof Dialog | null>(null)

const emit = defineEmits<{
    (e: 'close', value: MessageDialogEvent): void
}>()

const haveIcon = computed(() => {
    return (props.icon ?? null) !== null
})

const renderOkButton = computed(() => {
    return props.buttons & Buttons.ok
})

const renderCancelButton = computed(() => {
    return props.buttons & Buttons.cancel
})

const renderDeleteButton = computed(() => {
    return props.buttons & Buttons.delete
})

const renderYesButton = computed(() => {
    return props.buttons & Buttons.yes
})

const renderNoButton = computed(() => {
    return props.buttons & Buttons.no
})

const renderDeleteDisabledButton = computed(() => {
    return props.buttons & Buttons.deleteDisabled
})

function onClick(type: string) {
    eventBus.once(ApplicationEventTypes.DIALOG_CLOSE, () => {
        emit('close', { condition: type, data: props.data })
    })

    if (dialog.value !== null) {
        dialog.value.hide()
    }
}

function isIcon(icon: 'info' | 'question' | 'alert' | 'error' | 'success'): boolean {
    return (props.icon ?? '') === icon
}
</script>
