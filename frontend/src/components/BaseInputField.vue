<template>
    <div class="flex flex-col">
        <div class="block flex items-center space-x-2">
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
        <input
            v-if="!disabled"
            :id="id"
            :type="type"
            :name="name"
            :value="modelValue"
            :placeholder="placeholder"
            @focus="$emit('focus')"
            :aria-invalid="hasError"
            @input="handleInput"
            class="block rounded border bg-white text-black transition-shadow duration-200 ease-linear focus:shadow-lg dark:bg-transparent dark:text-gray-100"
            :class="[
                { 'px-2 py-1': size === 'small', 'px-2 py-3': size === 'medium', 'px-2 py-4': size === 'large' },
                [
                    hasError
                        ? 'border-red-400 hover:border-red-500 focus:border-red-500 focus:shadow-red-100 focus:ring-red-500 dark:focus:shadow-red-900'
                        : 'border-gray-400 hover:border-blue-500 focus:outline-none focus:ring-blue-500 ',
                ],
                inputClass,
            ]"
        />
        <div
            v-else
            class="cursor-not-allowed rounded border border-gray-300 bg-white p-6 dark:border-gray-500 dark:bg-transparent dark:text-gray-100"
        ></div>
        <slot></slot>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { getId } from '@/util/Util'
import { defineEmits, defineProps, type PropType } from 'vue'
import InformationIcon from '@/icons/InformationIcon.vue'

defineProps({
    name: {
        type: String,
        default: () => getId(),
    },
    id: {
        type: String,
        default: () => getId(),
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
        default: '',
    },
    size: {
        type: String as PropType<'small' | 'medium' | 'large'>,
        default: 'medium',
        required: false,
    },
    modelValue: {
        default: null,
        required: true,
        type: null as unknown as PropType<string | null | number | bigint | boolean>,
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

const emit = defineEmits<{
    (e: 'update:modelValue', value: string | number | bigint | null): void
    (e: 'focus'): void
}>()

function handleInput(e: Event) {
    emit('update:modelValue', (e.target as HTMLInputElement).value)
}
</script>

<style scoped>
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

/* Firefox */
input[type='number'] {
    -moz-appearance: textfield;
}
</style>
