<template>
    <div class="flex flex-1 flex-col">
        <div
            class="flex items-center justify-between rounded-t bg-green-500 p-2 text-white dark:bg-gray-700 dark:text-gray-100"
            :class="{ 'rounded-b': collapsed }"
        >
            <div>ID: {{ node?.id }}</div>
            <div>
                <span v-if="collapsed" @click="collapsed = !collapsed" class="block" title="Click to expand the panel.">
                    <PlusIcon class="w-6 cursor-pointer fill-current text-white dark:text-gray-200"></PlusIcon>
                </span>
                <span v-else @click="collapsed = !collapsed" class="block" title="Click to collapse the panel.">
                    <MinusIcon
                        title="Collapse the panel"
                        class="w-6 cursor-pointer fill-current text-white dark:text-gray-200"
                    ></MinusIcon>
                </span>
            </div>
        </div>
        <div v-show="!collapsed" class="flex flex-1 flex-col rounded-b p-2 shadow dark:shadow-gray-700">
            <div class="flex flex-wrap space-x-2">
                <div
                    class="rounded border border-green-100 bg-green-50 px-3 py-1 uppercase shadow dark:border-gray-700 dark:bg-gray-700 dark:shadow-gray-700"
                >
                    Id <span class="text-green-400">{{ node?.id }}</span>
                </div>
                <div
                    class="rounded border border-green-100 bg-green-50 px-3 py-1 uppercase shadow dark:border-gray-700 dark:bg-gray-700 dark:shadow-gray-700"
                >
                    Port <span class="text-green-400">{{ node?.port }}</span>
                </div>
                <div
                    class="rounded border border-green-100 bg-green-50 px-3 py-1 uppercase shadow dark:border-gray-700 dark:bg-gray-700 dark:shadow-gray-700"
                >
                    Host <span class="text-green-400">{{ node?.host }}</span>
                </div>
                <div
                    v-if="node.rack !== null"
                    class="rounded border border-green-100 bg-green-50 px-3 py-1 uppercase shadow dark:border-gray-700 dark:bg-gray-700 dark:shadow-gray-700"
                >
                    Rack: <span class="text-green-400">{{ node?.rack }}</span>
                </div>
            </div>
            <div class="my-2 text-lg" v-if="haveData">
                Broker specific Configuration(s)
                <span class="text-xs text-gray-400">Hover over the row for more detail</span>
            </div>
            <div class="flex flex-1 flex-col" v-if="haveData">
                <DataGrid
                    :columns="columns"
                    :show-pager="false"
                    identifier="name"
                    :data="data"
                    :sorters="sorters"
                    :virtual="true"
                    :row-title-renderer="rowTitleRenderer"
                    @filter="onFilterApply"
                    @sort="onSortClick"
                    class="h-40 flex-grow rounded border border-green-50 dark:border-gray-800"
                >
                </DataGrid>
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
import PlusIcon from '@/icons/PlusIcon.vue'
import { computed, defineProps, ref } from 'vue'
import type { PropType } from 'vue'
import type { Configuration, Node as NodeInformation } from '@/entity/ClusterInformation'
import type { Column } from '@/entity/Column'
import type { Sorter } from '@/entity/Sorter'
import type { PagedData } from '@/entity/PagedData'
import { getFilterData, getPaginateItems, sortData } from '@/util/Util'
import DataGrid from '@/components/DataGrid/DataGrid.vue'
import MinusIcon from '@/icons/MinusIcon.vue'
import type { Filter } from '@/entity/Filter'

const props = defineProps({
    node: {
        type: Object as PropType<NodeInformation>,
        required: true,
    },
})
const sorters = ref<Sorter | null>({ property: 'name', direction: 'asc' })
const columns = ref<Column[]>([
    {
        dataId: 'name',
        type: 'string',
        title: 'Name',
        sortable: true,
        align: 'left',
        width: 340,
        filterable: true,
    },
    {
        dataId: 'value',
        type: 'string',
        title: 'Value',
        sortable: false,
        align: 'left',
        width: 'auto',
        filterable: false,
    },
    {
        dataId: 'type',
        type: 'string',
        title: 'Type',
        sortable: false,
        align: 'left',
        width: 120,
        filterable: false,
    },
])
const filters = ref<Filter[]>([])
const collapsed = ref<boolean>(false)
const data = computed<PagedData<Configuration>>(() => {
    let filteredSortedData = getSortedItems(
        getFilterData<Configuration>(props.node?.configurations ?? [], filters.value)
    )

    return getPaginateItems<Configuration>(filteredSortedData, 1, filteredSortedData.length ?? 0)
})

const haveData = computed<boolean>(() => {
    return (props.node?.configurations ?? []).length !== 0
})

function getSortedItems(data: Configuration[]) {
    if (!Array.isArray(data) || !data.length) {
        return data
    }

    if (sorters.value === null) {
        return data
    }

    return sortData<Configuration>(data, [sorters.value])
}

function rowTitleRenderer(row: Configuration) {
    if (row.definition === '') {
        return `Name: ${row.name} \u000A\u000AType: ${row.type}\u000A\u000AValue: ${row.value}`
    }
    const definition = row.definition.replace(/<code>/g, '').replace(/<\/code>/g, '')
    // eslint-disable-next-line vue/max-len
    return `Name: ${row.name} \u000A\u000AType: ${row.type}\u000A\u000AValue: ${row.value}\u000A\u000ADefinition: ${definition}`
}

function onFilterApply(appliedFilters: Filter[]) {
    filters.value = appliedFilters
}

function onSortClick(sorter: Sorter | null): void {
    sorters.value = sorter
}
</script>
