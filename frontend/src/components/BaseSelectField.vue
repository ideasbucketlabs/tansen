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
        <select
            v-if="!disabled"
            :id="id"
            :name="name"
            @input="handleChange"
            @change="handleChange"
            class="block rounded border bg-white text-black transition-shadow duration-200 ease-linear focus:shadow-lg dark:bg-transparent dark:text-gray-100"
            :class="[
                inputClass,
                {
                    'px-2 py-1': size === 'small',
                    'px-2 py-3': size === 'medium',
                    'px-2 py-4': size === 'large',
                    '': size === 'custom',
                },
                [
                    hasError
                        ? 'border-red-400 hover:border-red-500 focus:border-red-500 focus:ring-red-500'
                        : 'border-gray-400 hover:border-blue-500 focus:outline-none focus:ring-blue-500 ',
                ],
            ]"
        >
            <option
                class="bg-white dark:bg-gray-800"
                v-for="(option, index) in options"
                :key="index"
                :value="option.value"
                :selected="option.value === modelValue"
            >
                {{ option.label }}
            </option>
        </select>
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
import { type PropType } from 'vue'
import InformationIcon from '@/icons/InformationIcon.vue'

const props = defineProps({
    name: {
        type: String as PropType<string>,
        default: () => getId(),
    },
    id: {
        type: String as PropType<string>,
        default: () => getId(),
    },
    label: {
        type: String as PropType<string>,
        required: true,
    },
    labelClass: {
        type: String as PropType<string>,
        default: '',
    },
    hasError: {
        type: Boolean as PropType<boolean>,
        default: false,
    },
    disabled: {
        type: Boolean as PropType<boolean>,
        default: false,
    },
    size: {
        type: String as PropType<'small' | 'medium' | 'large' | 'custom'>,
        default: 'medium',
        required: false,
    },
    hideLabel: {
        type: Boolean,
        required: false,
        default: false,
    },
    inputClass: {
        type: String as PropType<string>,
        default: 'border',
    },
    modelValue: {
        type: [String, Number, Boolean] as PropType<string | number | boolean | bigint>,
        required: false,
        default: null,
    },
    options: {
        type: Array as PropType<{ label: string; value: number | string | boolean }[]>,
        default() {
            return []
        },
    },
    infoTooltip: {
        default: '',
        required: false,
        type: String as PropType<string>,
    },
    castAs: {
        default: 'string',
        required: false,
        type: String as PropType<'string' | 'boolean' | 'integer' | 'bigint'>,
    },
})

const emit = defineEmits<{ (e: 'update:model-value', value: string | number | boolean | bigint): void }>()

function handleChange(e: Event) {
    if (props.castAs === 'boolean') {
        emit('update:model-value', ((e.target as HTMLInputElement).value as string) === 'true')
        return
    }

    if (props.castAs === 'integer') {
        emit('update:model-value', parseInt((e.target as HTMLInputElement).value as string, 10))
        return
    }

    if (props.castAs === 'bigint') {
        // eslint-disable-next-line no-undef
        emit('update:model-value', BigInt((e.target as HTMLInputElement).value as string))
        return
    }

    emit('update:model-value', (e.target as HTMLInputElement).value)
}
</script>
