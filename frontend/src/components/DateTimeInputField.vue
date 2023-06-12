<template>
    <div class="flex flex-col">
        <div class="flex items-center space-x-2">
            <label
                :for="id"
                class="mb-1 block text-left text-black dark:text-gray-100"
                :class="[hideLabel ? 'sr-only' : '', labelClass]"
                v-html="label"
            ></label>
            <span
                v-if="infoTooltip !== ''"
                class="block w-5 items-center rounded-full border-2 border-blue-500 hover:cursor-help"
                :title="infoTooltip"
            >
                <InformationIcon class="w-full fill-current text-blue-500"></InformationIcon>
            </span>
        </div>
        <div
            tabindex="-1"
            v-if="!disabled"
            :id="id"
            @focus="$emit('focus')"
            :aria-invalid="hasError"
            @click="showPicker = true"
            class="flex cursor-pointer items-center rounded border bg-white text-black transition-shadow duration-200 ease-linear focus:shadow-lg dark:bg-transparent dark:text-gray-100"
            :class="[
                { 'px-2 py-1': size === 'small', 'px-2 py-3': size === 'medium', 'px-2 py-4': size === 'large' },
                [
                    hasError
                        ? 'border-red-400 hover:border-red-500 focus:border-red-500 focus:shadow-red-100 focus:ring-red-500 dark:focus:shadow-red-900'
                        : 'focus:outline-blue border-gray-400 hover:border-blue-500 focus:border-blue-500 focus:outline-2 focus:ring-1 focus:ring-blue-500',
                ],
                inputClass,
                { 'outline-blue border border-blue-600 outline-2 ring-1 ring-blue-500': showPicker },
            ]"
        >
            <div :data-parent-id="id">{{ formattedDateTime }}</div>
        </div>
        <div
            v-else
            class="cursor-not-allowed rounded border border-gray-300 bg-white p-6 dark:border-gray-500 dark:bg-transparent dark:text-gray-100"
        ></div>
        <slot></slot>
        <transition name="fade">
            <DateTimePicker
                v-if="showPicker"
                class="absolute top-12 z-10 shadow-lg dark:shadow-black"
                v-model="clonedModelValue"
                @close="showPicker = false"
                @clicked-outside="datePickerClickedOutside"
            ></DateTimePicker>
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
import { computed, type PropType, ref, watch } from 'vue'
import { getId } from '@/util/Util'
import InformationIcon from '@/icons/InformationIcon.vue'
import DateTimePicker from '@/components/DateTimePicker.vue'
import { format } from 'date-fns'
const props = defineProps({
    name: {
        type: String,
        default: () => {
            return getId()
        },
        required: false,
    },
    id: {
        type: String,
        default: () => {
            return getId()
        },
        required: false,
    },
    label: {
        type: String,
        required: true,
    },
    hasError: {
        type: Boolean as PropType<boolean>,
        default: false,
    },
    disabled: {
        type: Boolean as PropType<boolean>,
        default: false,
    },
    labelClass: {
        type: String,
        default: '',
    },
    hideLabel: {
        type: Boolean,
        required: false,
        default: false,
    },
    inputClass: {
        type: String,
        default: '',
    },
    placeholder: {
        type: String,
        default: 'Select a date time',
    },
    size: {
        type: String as PropType<'small' | 'medium' | 'large'>,
        default: 'medium',
        required: false,
    },
    modelValue: {
        default: null,
        required: true,
        type: null as unknown as PropType<Date | null>,
    },
    type: {
        type: String,
        default: 'text',
    },
    infoTooltip: {
        default: '',
        required: false,
        type: String as PropType<string>,
    },
})
const clonedModelValue = ref<Date | null>(props.modelValue === null ? null : new Date(props.modelValue))
const showPicker = ref<boolean>(false)
const emit = defineEmits<{
    (e: 'update:model-value', value: Date | null): void
    (e: 'focus'): void
}>()

const formattedDateTime = computed<string>(() => {
    if (clonedModelValue.value === null) {
        return props.placeholder
    }

    return `${format(clonedModelValue.value, 'MMM dd, yyyy, hh:mm aaa')}`
})
watch(clonedModelValue, (newClonedModelValue: Date | null) => {
    emit('update:model-value', newClonedModelValue)
})

function datePickerClickedOutside(element: HTMLElement) {
    if ((element.getAttribute('data-parent-id') ?? element.getAttribute('id') ?? '') === props.id) {
        return
    }
    showPicker.value = false
}
</script>
