<template>
    <div class="flex flex-col rounded">
        <div
            v-if="haveHeader"
            class="flex items-center justify-between rounded-t bg-green-500 px-2 py-2 text-white dark:bg-gray-700"
        >
            <div class="">{{ header }}</div>
            <div
                v-if="props.closable"
                class="h-5 w-5 transition-all duration-200 ease-linear hover:h-6 hover:w-6 hover:cursor-pointer"
            >
                <CloseIcon class="h-full w-full"></CloseIcon>
            </div>
        </div>
        <div :class="bodyClass">
            <slot></slot>
        </div>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { computed, type PropType } from 'vue'
import CloseIcon from '@/icons/CloseIcon.vue'

const props = defineProps({
    header: {
        type: String as PropType<string>,
        default: '',
        required: false,
    },
    closable: {
        type: Boolean as PropType<boolean>,
        default: false,
        required: false,
    },
    bodyClass: {
        type: String as PropType<string>,
        default: '',
        required: false,
    },
})

const haveHeader = computed(() => {
    return (props.header?.trim() ?? '').length !== 0
})
</script>
