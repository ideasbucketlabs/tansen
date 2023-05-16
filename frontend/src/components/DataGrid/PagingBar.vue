<template>
    <div
        class="rounded-b border border-t-0 border-green-500 bg-green-500 text-white dark:border-gray-800 dark:bg-gray-700"
    >
        <div class="flex items-center justify-between p-2">
            <button
                title="Refresh data"
                type="button"
                class="flex h-7 w-9 items-center justify-center rounded border border-green-300 p-1 text-sm hover:shadow-lg dark:border-gray-800"
                @click="$emit('refreshData')"
            >
                <refresh-icon class="block h-full w-full fill-current text-white dark:text-gray-100"></refresh-icon>
            </button>
            <div class="ml-4 flex items-center overflow-x-auto text-sm">
                <div class="truncate px-3 py-1 text-sm">
                    Displaying: {{ beginRecord }} - {{ endRecord }} of {{ totalItems }} records
                </div>
                <div class="flex flex-row-reverse overflow-x-auto">
                    <ul class="flex overflow-x-auto">
                        <li
                            v-if="renderPrevNextButtons"
                            @click="onPrevLinkClick"
                            class="mr-2 rounded border border-green-300 px-3 py-1 text-center font-bold dark:border-gray-800"
                            :class="[
                                isPreviousLinkDisabled
                                    ? 'cursor-default text-gray-500'
                                    : 'cursor-pointer hover:shadow-lg',
                            ]"
                        >
                            Prev
                        </li>
                        <template v-if="renderSimple">
                            <li
                                v-for="(page, index) in new Array(totalPages)"
                                @click="onLinkClick(index + 1)"
                                class="mr-2 rounded border px-3 py-1 text-center"
                                :class="[
                                    currentPage === index + 1
                                        ? 'cursor-default border-green-400 bg-green-600 shadow-inner'
                                        : 'cursor-pointer border-green-300 shadow-sm hover:shadow-lg dark:border-gray-800',
                                ]"
                                :key="index"
                            >
                                {{ index + 1 }}
                            </li>
                        </template>
                        <template v-else>
                            <li
                                @click="onLinkClick(1)"
                                class="mr-2 rounded border px-3 py-1 text-center"
                                :class="[
                                    currentPage === 1
                                        ? 'cursor-default border-green-400 bg-green-600 text-white'
                                        : 'cursor-pointer border-green-300 shadow-sm hover:shadow-lg dark:border-gray-800',
                                ]"
                            >
                                1
                            </li>
                            <li v-if="hasFirstEllipses" class="mr-2 cursor-default px-3 py-1 text-center">...</li>
                            <li
                                v-for="(page, index) in visiblePages"
                                @click="onLinkClick(page.page)"
                                class="mr-2 rounded border px-3 py-1 text-center"
                                :class="[
                                    currentPage === page.page
                                        ? 'cursor-default border-green-400 bg-green-600 text-white shadow-inner'
                                        : 'cursor-pointer border-green-300 shadow-sm hover:shadow-lg dark:border-gray-800',
                                ]"
                                :key="index"
                            >
                                {{ page.page }}
                            </li>
                            <li v-if="hasLastEllipses" class="mr-2 cursor-default px-3 py-1 text-center">...</li>
                            <li
                                @click="onLinkClick(totalPages)"
                                class="mr-2 rounded border px-3 py-1 text-center"
                                :class="[
                                    currentPage === totalPages
                                        ? 'cursor-default border-green-400 bg-green-600 text-white'
                                        : 'cursor-pointer border-green-300 shadow-sm hover:shadow-lg dark:border-gray-800',
                                ]"
                            >
                                {{ totalPages }}
                            </li>
                        </template>
                        <li
                            v-if="renderPrevNextButtons"
                            @click="onNextLinkClick"
                            class="mr-2 rounded border border-green-300 px-3 py-1 text-center font-bold dark:border-gray-800"
                            :class="[
                                isNextLinkDisabled ? 'cursor-default text-gray-500' : 'cursor-pointer hover:shadow-lg',
                            ]"
                        >
                            Next
                        </li>
                    </ul>
                </div>
            </div>
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
import { computed } from 'vue'
import RefreshIcon from '@/icons/RefreshIcon.vue'

const props = defineProps({
    currentPage: {
        type: Number,
        required: true,
    },
    totalItems: {
        type: Number,
        required: true,
    },
    totalPages: {
        type: Number,
        required: true,
    },
    pageSize: {
        type: Number,
        required: false,
        default: 50,
    },
    totalVisible: {
        type: Number,
        default: 5,
        required: false,
    },
    renderPrevNextButtons: {
        type: Boolean,
        default: false,
        required: false,
    },
})
const emit = defineEmits<{ (e: 'pageChange', value: number): void; (e: 'refreshData'): void }>()
const isPreviousLinkDisabled = computed(() => {
    return (props.currentPage as number) === 1
})
const isNextLinkDisabled = computed(() => {
    return (props.currentPage as number) === (props.totalPages as number)
})
const beginRecord = computed(() => {
    if ((props.totalItems as number) === 0) {
        return 0
    }

    return (props.currentPage as number) === 1 ? 1 : (props.currentPage - 1) * 50
})
const endRecord = computed(() => {
    return props.currentPage * props.pageSize > props.totalItems ? props.totalItems : props.currentPage * props.pageSize
})
const renderSimple = computed(() => {
    // If we have less than 6 pages then there is no need to hide anything or render ellipses.
    return props.totalPages < 6
})
const visiblePages = computed(() => {
    let left = 0
    let right = 0

    if ((props.currentPage as number) === 1) {
        // If we are at first page
        left = props.currentPage + 1
        right = props.currentPage + 3
    } else if (props.currentPage === props.totalPages) {
        // If we are at last page
        left = props.currentPage - 3
        right = props.totalPages - 1
    } else if (props.currentPage >= props.totalPages - 1) {
        // If we are at end region
        left = props.currentPage - 2
        right = props.currentPage
    } else if (props.currentPage <= 2) {
        // If we are at begin region
        left = props.currentPage
        right = props.currentPage + 2
    } else {
        // We will show one page before and after
        left = props.currentPage - 1
        right = props.currentPage + 1
    }

    const visiblePages = []

    for (let i = left; i <= right; i += 1) {
        visiblePages.push({ page: i })
    }

    return visiblePages
})
const hasFirstEllipses = computed(() => {
    return props.currentPage > 3
})
const hasLastEllipses = computed(() => {
    return props.currentPage <= props.totalPages - 3
})

function onLinkClick(desiredPage: number) {
    if ((props.currentPage as number) !== desiredPage) {
        emit('pageChange', desiredPage)
    }
}

function onPrevLinkClick() {
    if (!isPreviousLinkDisabled.value) {
        emit('pageChange', (props.currentPage as number) - 1)
    }
}

function onNextLinkClick() {
    if (!isNextLinkDisabled.value) {
        emit('pageChange', (props.currentPage as number) + 1)
    }
}
</script>
