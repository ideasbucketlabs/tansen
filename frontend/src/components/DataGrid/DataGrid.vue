<template>
    <div class="relative flex flex-col overflow-hidden">
        <Loading v-if="loading"></Loading>
        <div class="relative flex flex-grow flex-col overflow-hidden">
            <div
                :class="{ 'rounded-t': !embedded }"
                class="flex flex-grow flex-col overflow-x-auto bg-white will-change-scroll dark:border-gray-800 dark:bg-gray-900"
            >
                <div
                    class="data-grid flex flex-1 flex-col overflow-hidden"
                    :style="{ minWidth: gridMinWidth + 'px' }"
                    ref="grid"
                >
                    <div
                        :class="[
                            embedded
                                ? 'border-l border-r border-green-200 bg-green-100 dark:border-gray-800 dark:bg-gray-800'
                                : 'border-green-500 bg-green-500 dark:bg-gray-700',
                        ]"
                        class="header flex h-10 items-center border-b shadow dark:border-gray-900"
                    >
                        <div
                            v-for="(column, index) in columns"
                            :key="index"
                            :ref="
                                (el) => {
                                    filterTriggersDom.set(index, el as Element)
                                }
                            "
                            class="top-0 flex-shrink-0 border-r text-left uppercase last:border-r-0 dark:border-gray-900 dark:text-gray-100"
                            :class="{
                                'cursor-pointer': column.sortable,
                                'cursor-default': !column.sortable,
                                'border-green-200 text-green-500': embedded,
                                'border-green-900 text-white': !embedded,
                            }"
                            v-bind:style="getColumnStyle(column, columns, index)"
                        >
                            <div class="flex h-10 justify-center">
                                <template v-if="column.dataId !== 'actionColumn'">
                                    <div
                                        class="flex flex-1 select-none items-center justify-between p-2"
                                        :class="{
                                            'text-center': column.align === 'center',
                                            'text-right': column.align === 'right',
                                        }"
                                        @click="sortBy(column)"
                                    >
                                        <div class="inline-block flex-1">
                                            <div
                                                v-if="
                                                    column.sortable &&
                                                    isSorted(column.dataId) &&
                                                    column.align === 'right'
                                                "
                                                class="relative inline-block"
                                            >
                                                <UpArrowIcon
                                                    class="relative top-0.5 w-4 transform fill-current transition-all duration-200 ease-out"
                                                    :class="[
                                                        sortedDirection(column.dataId) === 'asc' ? '' : 'rotate-180',
                                                        embedded
                                                            ? 'text-green-500 dark:text-gray-100'
                                                            : 'text-white dark:text-gray-100',
                                                    ]"
                                                >
                                                </UpArrowIcon>
                                            </div>
                                            {{ column.title }}
                                            <div
                                                v-if="
                                                    column.sortable &&
                                                    isSorted(column.dataId) &&
                                                    column.align !== 'right'
                                                "
                                                class="relative inline-block"
                                            >
                                                <UpArrowIcon
                                                    class="relative top-0.5 w-4 transform fill-current transition-all duration-200 ease-out"
                                                    :class="[
                                                        sortedDirection(column.dataId) === 'asc' ? '' : 'rotate-180',
                                                        embedded
                                                            ? 'text-green-500 dark:text-gray-100'
                                                            : 'text-white dark:text-gray-100',
                                                    ]"
                                                >
                                                </UpArrowIcon>
                                            </div>
                                        </div>
                                    </div>
                                    <div
                                        v-if="column.filterable"
                                        class="mr-2 flex w-4 items-center justify-center"
                                        :data-trigger="uniqueId + '-column-' + index"
                                        @click="showHideFilter(column.dataId)"
                                        :title="'Filter ' + column.title"
                                    >
                                        <filter-icon
                                            v-if="!embedded"
                                            :trigger="uniqueId + '-column-' + index"
                                            class="fill-current"
                                            :class="[
                                                isFilterActive(column.dataId)
                                                    ? 'stroke-green-300 text-green-300'
                                                    : 'stroke-white text-green-500 dark:text-gray-700',
                                            ]"
                                        ></filter-icon>
                                        <filter-icon
                                            v-else
                                            :trigger="uniqueId + '-column-' + index"
                                            class="fill-current"
                                            :class="[
                                                isFilterActive(column.dataId)
                                                    ? 'stroke-green-500 text-green-500'
                                                    : 'stroke-green-300 text-green-200 dark:stroke-gray-500 dark:text-gray-700',
                                            ]"
                                        ></filter-icon>
                                    </div>
                                </template>
                                <template v-else>
                                    <div
                                        class="flex select-none items-center justify-between p-2"
                                        :class="{
                                            'text-center': column.align === 'center',
                                            'text-right': column.align === 'right',
                                        }"
                                    >
                                        <div
                                            :class="{
                                                'text-center': column.align === 'center',
                                                'text-right': column.align === 'right',
                                            }"
                                            class="flex-1"
                                        >
                                            {{ column.title }}
                                        </div>
                                    </div>
                                </template>
                                <TextDataFilter
                                    v-if="column.type === 'string' && isFilterVisible(column.dataId)"
                                    class="absolute"
                                    :trigger="uniqueId + '-column-' + index"
                                    @hide="showHideFilter(column.dataId)"
                                    :field="column.dataId"
                                    @mounted="createFilterPoppers(column.dataId, index, $event)"
                                    @destroyed="removeFilterPoppers(column.dataId, index)"
                                    v-model="appliedFilter[column.dataId]"
                                >
                                </TextDataFilter>
                                <BooleanDataFilter
                                    v-if="column.type === 'boolean' && isFilterVisible(column.dataId)"
                                    class="absolute w-64"
                                    :trigger="uniqueId + '-column-' + index"
                                    @hide="showHideFilter(column.dataId)"
                                    :field="column.dataId"
                                    @mounted="createFilterPoppers(column.dataId, index, $event)"
                                    @destroyed="removeFilterPoppers(column.dataId, index)"
                                    v-model="appliedFilter[column.dataId]"
                                    :configuration="column.booleanFilterConfiguration"
                                >
                                </BooleanDataFilter>
                                <NumberDataFilter
                                    v-if="column.type === 'number' && isFilterVisible(column.dataId)"
                                    class="absolute w-64"
                                    :trigger="uniqueId + '-column-' + index"
                                    @hide="showHideFilter(column.dataId)"
                                    :field="column.dataId"
                                    @mounted="createFilterPoppers(column.dataId, index, $event)"
                                    @destroyed="removeFilterPoppers(column.dataId, index)"
                                    v-model="appliedFilter[column.dataId]"
                                >
                                </NumberDataFilter>
                            </div>
                        </div>
                    </div>
                    <div class="flex flex-1 flex-col overflow-y-auto will-change-scroll">
                        <template v-if="virtual">
                            <VirtualScroll :items="data.data" :row-height="40" class="h-64 flex-grow">
                                <template v-slot:detail="{ items }">
                                    <div
                                        class="data-grid-row flex items-center border-b border-green-50 bg-white hover:cursor-default hover:bg-green-50 dark:border-gray-800 dark:bg-gray-900 dark:hover:bg-gray-800"
                                        :class="rowClass"
                                        v-for="(row, rowIndex) in items"
                                        :key="'row' + row[identifier]"
                                        :title="rowTitleRenderer(row)"
                                        @click="$emit('rowClick', row, rowIndex)"
                                    >
                                        <div
                                            v-for="(column, index) in columns"
                                            :key="uniqueId + index"
                                            class="flex flex-shrink-0 items-center px-2 py-2 text-left text-black hover:cursor-default hover:bg-green-50 dark:text-gray-100 dark:hover:bg-gray-800"
                                            style="height: 40px"
                                            v-bind:style="getColumnStyle(column, columns, index)"
                                        >
                                            <div
                                                v-if="column.renderCustom"
                                                class="flex flex-grow items-center truncate"
                                            >
                                                <slot
                                                    :name="column.dataId"
                                                    v-bind:column="column"
                                                    v-bind:row="row"
                                                    v-bind:rowIndex="rowIndex"
                                                ></slot>
                                            </div>
                                            <div
                                                v-else
                                                class="flex-grow truncate"
                                                :class="{
                                                    'text-center': column.align === 'center',
                                                    'text-right': column.align === 'right',
                                                }"
                                            >
                                                <template v-if="column.formatter !== undefined">
                                                    {{ column.formatter(row[column.dataId], column.dataId, row) }}
                                                </template>
                                                <template v-else>
                                                    {{ row[column.dataId] }}
                                                </template>
                                            </div>
                                        </div>
                                    </div>
                                </template>
                            </VirtualScroll>
                        </template>
                        <template v-else>
                            <div
                                class="data-grid-row flex items-center border-b border-green-50 bg-white hover:cursor-default hover:bg-green-50 dark:border-gray-800 dark:bg-gray-900 dark:bg-gray-900 dark:hover:bg-gray-800"
                                :class="rowClass"
                                v-for="(row, rowIndex) in data.data"
                                :key="'row' + row[identifier]"
                                :title="rowTitleRenderer(row)"
                                @click="$emit('rowClick', row, rowIndex)"
                            >
                                <div
                                    v-for="(column, index) in columns"
                                    :key="index"
                                    class="flex flex-shrink-0 items-center px-2 py-2 text-left text-black hover:cursor-default hover:bg-green-50 dark:text-gray-100 dark:hover:bg-gray-800"
                                    style="height: 40px"
                                    v-bind:style="getColumnStyle(column, columns, index)"
                                >
                                    <div v-if="column.renderCustom" class="flex flex-grow items-center truncate">
                                        <slot
                                            :name="column.dataId"
                                            v-bind:column="column"
                                            v-bind:row="row"
                                            v-bind:rowIndex="rowIndex"
                                        ></slot>
                                    </div>
                                    <div
                                        v-else
                                        class="flex-grow truncate"
                                        :class="{
                                            'text-center': column.align === 'center',
                                            'text-right': column.align === 'right',
                                        }"
                                    >
                                        <template v-if="column.formatter !== undefined">
                                            {{ column.formatter(row[column.dataId], column.dataId, row) }}
                                        </template>
                                        <template v-else>
                                            {{ row[column.dataId] }}
                                        </template>
                                    </div>
                                </div>
                            </div>
                        </template>
                    </div>
                </div>
            </div>
            <div v-if="showPager" style="box-shadow: 0 -3px 2px 0 rgba(0, 0, 0, 0.09)">
                <DataGridPagingBar
                    @refreshData="$emit('refreshData')"
                    @pageChange="$emit('pageChange', $event)"
                    :currentPage="data.currentPage"
                    :pageSize="data.pageSize"
                    :totalItems="data.total"
                    :totalPages="data.totalPages"
                >
                </DataGridPagingBar>
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
import {
    computed,
    defineEmits,
    type PropType,
    defineProps,
    onUnmounted,
    ref,
    watch,
    defineAsyncComponent,
    onMounted,
} from 'vue'
import { createPopperLite as createPopper, preventOverflow, flip, type Instance } from '@popperjs/core'
import type { StrictModifiers } from '@popperjs/core'
import { clone, getId } from '@/util/Util'
import type { Sorter } from '@/entity/Sorter'
import type { Column } from '@/entity/Column'
import type { Filter } from '@/entity/Filter'
import UpArrowIcon from '@/icons/UpArrowIcon.vue'
import FilterIcon from '@/icons/FilterIcon.vue'
import DataGridPagingBar from '@/components/DataGrid/PagingBar.vue'
import Loading from '@/components/Loading.vue'
import type { PagedData } from '@/entity/PagedData'

const TextDataFilter = defineAsyncComponent(() => import('@/components/DataGrid/TextDataFilter.vue'))
const BooleanDataFilter = defineAsyncComponent(() => import('@/components/DataGrid/BooleanDataFilter.vue'))
const NumberDataFilter = defineAsyncComponent(() => import('@/components/DataGrid/NumberDataFilter.vue'))

const VirtualScroll = defineAsyncComponent(() => import('@/components/VirtualScroll.vue'))

const props = defineProps({
    columns: {
        type: Array as PropType<Column[]>,
        required: true,
    },
    embedded: {
        type: Boolean as PropType<boolean>,
        required: false,
        default: false,
    },
    identifier: {
        type: String as PropType<string>,
        required: false,
        default: 'id',
    },
    data: {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        type: Object as PropType<PagedData<any>>,
        required: true,
    },
    sorters: {
        type: Object as PropType<Sorter | null>,
        required: false,
        default() {
            return {
                property: 'id',
                direction: 'asc',
            } as Sorter
        },
    },
    loading: {
        type: Boolean as PropType<boolean>,
        required: false,
        default() {
            return false
        },
    },
    rowTitleRenderer: {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        type: Function as PropType<(data: any) => string>,
        default() {
            return ''
        },
    },
    showPager: {
        type: Boolean as PropType<boolean>,
        required: false,
        default: true,
    },
    rowClass: {
        type: String as PropType<string>,
        default: 'cursor-default',
        required: false,
    },
    virtual: {
        type: Boolean as PropType<boolean>,
        default: false,
        required: false,
    },
})
const uniqueId = ref(getId())
const filterPoppers = ref<Map<string, Instance>>(new Map())
const visibleFilter = ref<Map<string, boolean>>(new Map())
const appliedFilter = ref<Record<string, Filter | null>>({})
const filterTriggersDom = ref<Map<number, Element>>(new Map()) // This is HTML component that triggers filter show/hide
const filtersDom = ref<Map<number, HTMLElement>>(new Map()) // This is HTML component that holds the filter component
const sorters = ref<Map<string, Sorter>>(new Map())
const emit = defineEmits<{
    (e: 'filter', value: Filter[]): void
    (e: 'sort', value: Sorter | null): void
    (e: 'pageChange', value: number): void
    (e: 'refreshData'): void
    (e: 'rowClick', row: unknown, rowId: number): void
}>()

const gridMinWidth = computed(() => {
    // +10 is for scrollbars at the end.
    return (
        (props.columns
            .map((column) => (column.width === 'auto' ? 400 : column.width))
            .reduce((left, right) => (left as number) + (right as number), 0) as number) + 10
    )
})

watch(
    appliedFilter,
    (newValue) => {
        const clonedValues: Filter[] = []

        Object.keys(newValue).forEach((key: string) => {
            if (newValue[key] !== null) {
                const clonedValue = clone(newValue[key] as Filter)
                clonedValue.property = key
                clonedValues.push(clonedValue)
            }
        })

        emit('filter', clonedValues)
    },
    { deep: true }
)

function createFilterPoppers(fieldId: string, columnIndex: number, element: Element | null) {
    if (element == null) {
        return
    }

    if (!filtersDom.value.has(columnIndex)) {
        filtersDom.value.set(columnIndex, element as HTMLElement)
    }

    filterPoppers.value.set(
        fieldId,
        createPopper<StrictModifiers>(
            filterTriggersDom.value.get(columnIndex) as Element,
            filtersDom.value.get(columnIndex) as HTMLElement,
            {
                placement: 'bottom-end',
                modifiers: [preventOverflow, flip],
            }
        )
    )
}

function removeFilterPoppers(fieldId: string, index: number) {
    filterPoppers.value.get(fieldId)?.destroy()
    filterPoppers.value.delete(fieldId)
    filtersDom.value.delete(index)
}

function showHideFilter(fieldId: string) {
    visibleFilter.value.set(fieldId, !(visibleFilter.value.get(fieldId) ?? false))
}

function isFilterActive(fieldId: string): boolean {
    if (Object.prototype.hasOwnProperty.call(appliedFilter.value, fieldId)) {
        return appliedFilter.value[fieldId] !== null
    }
    return false
}

function isFilterVisible(fieldId: string) {
    return visibleFilter.value.get(fieldId) ?? false
}

function isSorted(fieldId: string): boolean {
    return sorters.value.has(fieldId)
}

function sortedDirection(fieldId: string): string {
    return sorters.value.get(fieldId)?.direction ?? 'asc'
}

function sortBy(column: Column) {
    if (!column.sortable) {
        return
    }

    if (!sorters.value.has(column.dataId)) {
        sorters.value.clear() // Currently, only supporting single column sorter.
        sorters.value.set(column.dataId, { property: column.dataId, direction: 'asc' })
        emit('sort', { property: column.dataId, direction: 'asc' })
    } else if (sorters.value.has(column.dataId) && sorters.value.get(column.dataId)?.direction === 'desc') {
        sorters.value.clear() // Currently, only supporting single column sorter.
        emit('sort', null)
    } else {
        sorters.value.set(column.dataId, { property: column.dataId, direction: 'desc' })
        emit('sort', { property: column.dataId, direction: 'desc' })
    }
}

function getColumnStyle(column: Column, columns: Column[], index: number) {
    let styles: Record<string, string> = {}

    if (column.width === 'auto') {
        styles.minWidth = '400px'
        styles.flex = '1 1 0%'
        styles.whiteSpace = 'nowrap'
    } else {
        styles.width = `${(column.width as number) + (columns.length - 1 === index ? 10 : 0)}px` // Last column gets extra width for scrollbars
    }

    return styles
}

onMounted(() => {
    const sortedField = props.sorters?.property ?? null
    const sortedDirection = props.sorters?.direction ?? null

    if (sortedField !== null && sortedDirection !== null) {
        if (props.columns?.filter((it) => it.dataId === sortedField && it.sortable).length !== 0) {
            sorters.value.set(sortedField, { property: sortedField, direction: sortedDirection })
        }
    }
})

onUnmounted(() => {
    filterPoppers.value.forEach((value: Instance) => {
        value.destroy()
    })
    filterPoppers.value = new Map()
    filtersDom.value = new Map()
})
</script>
