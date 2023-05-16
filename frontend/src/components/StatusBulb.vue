<template>
    <span
        v-if="!goodHealth && online"
        class="block h-4 w-4 rounded-full border border-yellow-500 bg-gradient-to-b from-yellow-200 to-yellow-500 shadow-sm shadow-yellow-500 dark:border-yellow-500 dark:from-yellow-400 dark:to-yellow-500"
        title="Cluster is online but some of the replicas are not in sync."
    ></span>
    <span
        v-else-if="online"
        class="block h-4 w-4 rounded-full border border-green-400 bg-gradient-to-b from-green-200 to-green-300 shadow-sm shadow-green-500 dark:border-green-500 dark:from-green-400 dark:to-green-500"
        :title="onlineTitle"
    ></span>
    <span
        v-else
        class="block h-4 w-4 rounded-full border border-red-400 bg-gradient-to-b from-red-200 to-red-400 dark:border-red-600 dark:from-red-500 dark:to-red-600"
        :title="offlineTitle"
    ></span>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { PropType } from 'vue'

defineProps({
    online: {
        type: Boolean as PropType<boolean>,
    },
    onlineTitle: {
        type: String as PropType<string>,
        default: 'Cluster is online',
        required: false,
    },
    offlineTitle: {
        type: String as PropType<string>,
        default: 'Cluster is offline',
        required: false,
    },
    goodHealth: {
        type: Boolean as PropType<boolean>,
        default: false,
        required: false,
    },
})
</script>
