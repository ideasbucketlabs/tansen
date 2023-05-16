<template>
    <div class="z-5 absolute flex h-full w-full cursor-wait content-center items-center justify-center rounded">
        <div
            class="z-20 flex items-center rounded border border-green-500 bg-green-100 px-4 py-2 text-green-500 shadow dark:border-gray-800 dark:bg-gray-700 dark:text-gray-100"
        >
            <svg class="mr-3 h-4 w-4 animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
            </svg>
            <div class="select-none text-sm"><slot>Loading...</slot></div>
        </div>
        <div class="absolute z-10 h-full w-full opacity-50" :class="layoverClass"></div>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { type PropType } from 'vue'

defineProps({
    layoverClass: {
        type: String as PropType<string>,
        required: false,
        default: 'bg-green-100 dark:bg-gray-700 rounded',
    },
})
</script>
