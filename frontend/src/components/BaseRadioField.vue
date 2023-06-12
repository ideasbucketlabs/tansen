<template>
    <label
        :for="id"
        class="block"
        :class="[
            modelValue === value ? selectedClass : unSelectedClass,
            { 'hover:cursor-pointer': modelValue !== value },
        ]"
    >
        <span class="flex items-center space-x-2">
            <input
                :id="id"
                :name="name"
                type="radio"
                @focus="$emit('focus')"
                @input="handleInput"
                :value="value"
                :checked="modelValue === value"
                class="h-5 w-5 border-gray-300 text-blue-500 hover:border-blue-500 focus:ring-blue-500"
            />
            <span class="block flex items-center space-x-2">
                <span :class="labelClass">{{ label }}</span>
                <span
                    v-if="infoTooltip !== ''"
                    class="block w-5 items-center rounded-full border-2 border-blue-500 hover:cursor-help"
                    :title="infoTooltip"
                >
                    <InformationIcon class="w-full fill-current text-blue-500"></InformationIcon>
                </span>
            </span>
        </span>
        <span class="ml-7 block">
            <slot></slot>
        </span>
    </label>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { getId } from '@/util/Util'
import { type PropType } from 'vue'
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
    selectedClass: {
        type: String,
        default: 'df',
    },
    unSelectedClass: {
        type: String,
        default: '',
    },
    modelValue: {
        default: null,
        type: null as unknown as PropType<string | null | number>,
    },
    value: {
        default: null,
        required: true,
        type: null as unknown as PropType<string | null | number>,
    },
    infoTooltip: {
        default: '',
        required: false,
        type: String as PropType<string>,
    },
})

const emit = defineEmits<{ (e: 'update:model-value', value: string | number): void; (e: 'focus'): void }>()

function handleInput(e: Event) {
    emit('update:model-value', (e.target as HTMLInputElement).value)
}
</script>
