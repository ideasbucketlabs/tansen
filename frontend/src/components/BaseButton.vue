<template>
    <div class="flex content-center items-center">
        <div
            v-if="loading"
            class="flex h-full w-full cursor-wait items-center justify-center rounded bg-gray-400 text-center text-white dark:bg-gray-600"
        >
            <div
                class="h-4 w-4 animate-spin border-2 border-l-0 border-t-0 border-white"
                style="border-radius: 100%"
            ></div>
        </div>
        <div
            v-else-if="!loading && disabled"
            class="flex h-full w-full cursor-not-allowed select-none items-center justify-center rounded border border-transparent bg-gray-400 px-4 py-2 text-center text-white dark:border-gray-600 dark:bg-gray-600"
        >
            <div class="block text-center dark:text-gray-500">{{ label }}</div>
        </div>
        <button
            v-else
            @mousedown="$emit('click')"
            class="relative flex h-full w-full items-center justify-center overflow-hidden rounded border border-green-600 bg-green-500 px-4 py-2 text-white transition duration-200 ease-linear hover:bg-green-600 hover:shadow-lg dark:hover:shadow-black"
            @click="$emit('click')"
            :type="type"
        >
            <Ripple></Ripple>
            <span class="block">{{ label }}</span>
        </button>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineEmits, defineProps, type PropType } from 'vue'
import Ripple from './Ripple.vue'

defineProps({
    tag: {
        type: String as PropType<string>,
        default: 'button',
        required: false,
    },
    disabled: {
        type: Boolean as PropType<boolean>,
        default: false,
    },
    loading: {
        type: Boolean as PropType<boolean>,
        default: false,
    },
    label: {
        type: String as PropType<string>,
        default: '',
    },
    type: {
        type: String as PropType<'submit' | 'button' | 'reset'>,
        default: 'submit',
    },
    value: {
        type: [Object, Array] as PropType<unknown>,
        default: null,
    },
})

defineEmits<{ (e: 'click'): void }>()
</script>
