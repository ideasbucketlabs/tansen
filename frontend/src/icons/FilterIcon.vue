<template>
    <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        stroke="currentColor"
        stroke-width="2"
        :data-trigger="trigger"
        aria-hidden="true"
        focusable="false"
        role="img"
    >
        <path
            stroke-linecap="round"
            stroke-linejoin="round"
            :data-trigger="trigger"
            d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"
        />
    </svg>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineProps, type PropType } from 'vue'
import { getId } from '@/util/Util'

defineProps({
    trigger: {
        type: String as PropType<string>,
        required: false,
        default() {
            return getId()
        },
    },
})
</script>
