<template>
    <div class="flex flex-1 flex-col" ref="root">
        <div class="flex flex-col rounded">
            <div
                class="flex items-center justify-between rounded-t bg-green-500 p-2 text-white shadow dark:bg-gray-700 dark:text-gray-100"
                :class="{ 'rounded-b': collapsed }"
            >
                <div class="flex items-center space-x-2">
                    <div>{{ cluster.name }}</div>
                    <template v-if="cluster.name !== ''">
                        <div>&mdash;</div>
                        <div>{{ cluster.label }}</div>
                    </template>
                </div>
                <div>
                    <span
                        v-if="collapsed"
                        @click="collapsed = !collapsed"
                        class="block"
                        title="Click to expand the panel."
                    >
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
            <div class="flex flex-1 flex-col rounded-b bg-white shadow dark:bg-gray-900" v-show="!collapsed">
                <div class="grid grid-cols-1 gap-4 p-2 sm:grid-cols-2 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-7">
                    <div
                        class="rounded border border-green-100 text-center shadow dark:border-gray-700 dark:shadow-gray-700"
                    >
                        <div class="rounded-t bg-green-50 py-2 px-2 text-green-500 dark:bg-gray-700 dark:text-gray-100">
                            Total topic(s)
                        </div>
                        <div class="p-2" v-if="cluster.totalNumberOfTopics !== -1">
                            {{ new Intl.NumberFormat().format(cluster.totalNumberOfTopics) }}
                        </div>
                        <div class="p-2" v-else>N/A</div>
                    </div>
                    <div
                        class="rounded border border-green-100 text-center shadow dark:border-gray-700 dark:shadow-gray-700"
                    >
                        <div class="rounded-t bg-green-50 py-2 px-2 text-green-500 dark:bg-gray-700 dark:text-gray-100">
                            Total partition(s)
                        </div>
                        <div class="p-2" v-if="cluster.totalNumberOfPartitions !== -1">
                            {{ new Intl.NumberFormat().format(cluster.totalNumberOfPartitions) }}
                        </div>
                        <div class="p-2" v-else>N/A</div>
                    </div>
                    <div
                        class="rounded border border-green-100 text-center shadow dark:border-gray-700 dark:shadow-gray-700"
                    >
                        <div class="rounded-t bg-green-50 py-2 px-2 text-green-500 dark:bg-gray-700 dark:text-gray-100">
                            Space consumed
                        </div>
                        <div class="p-2" v-if="cluster.clusterSize !== -1">
                            {{ new Intl.NumberFormat().format(cluster.clusterSize) }} MB
                        </div>
                        <div class="p-2" v-else>N/A</div>
                    </div>
                    <div
                        class="flex flex-col rounded border border-green-100 text-center shadow dark:border-gray-700 dark:shadow-gray-700"
                    >
                        <div
                            class="rounded-t bg-green-50 py-2 px-2 text-center text-green-500 text-green-500 dark:bg-gray-700 dark:text-gray-100"
                        >
                            Online
                        </div>
                        <div class="flex flex-1 items-center justify-center">
                            <status-bulb
                                v-if="cluster.outOfSyncReplicas !== -1"
                                :online="cluster.online"
                                :good-health="cluster.outOfSyncReplicas === 0"
                            ></status-bulb>
                            <status-bulb v-else :online="cluster.online"></status-bulb>
                        </div>
                    </div>
                    <div
                        class="rounded border border-green-100 text-center shadow dark:border-gray-700 dark:shadow-gray-700"
                    >
                        <div
                            class="rounded-t bg-green-50 py-2 px-2 text-green-500 text-green-500 dark:bg-gray-700 dark:text-gray-100"
                        >
                            Total replica(s)
                        </div>
                        <div class="p-2" v-if="cluster.totalReplicas !== -1">
                            {{ new Intl.NumberFormat().format(cluster.totalReplicas) }}
                        </div>
                        <div class="p-2" v-else>N/A</div>
                    </div>
                    <div
                        class="rounded border border-green-100 text-center shadow dark:border-gray-700 dark:shadow-gray-700"
                    >
                        <div
                            class="rounded-t bg-green-50 py-2 px-2 text-green-500 text-green-500 dark:bg-gray-700 dark:text-gray-100"
                        >
                            In Sync replica(s)
                        </div>
                        <div class="p-2" v-if="cluster.inSyncReplicas !== -1">
                            {{ new Intl.NumberFormat().format(cluster.inSyncReplicas) }}
                        </div>
                        <div class="p-2" v-else>N/A</div>
                    </div>
                    <div
                        class="rounded border border-green-100 text-center shadow dark:border-gray-700 dark:shadow-gray-700"
                    >
                        <div
                            class="truncate rounded-t bg-green-50 py-2 px-2 text-green-500 text-green-500 dark:bg-gray-700 dark:text-gray-100"
                        >
                            Out of Sync replica(s)
                        </div>
                        <div class="p-2" v-if="cluster.outOfSyncReplicas !== -1">
                            {{ cluster.outOfSyncReplicas }}
                        </div>
                        <div class="p-2" v-else>N/A</div>
                    </div>
                </div>
                <div class="p-2" v-if="cluster.configurations.length !== 0">
                    <div class="text-lg">Common configuration(s) across all Broker(s)</div>
                    <div>
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
                            class="h-96 flex-grow rounded border border-green-50 shadow dark:border-gray-800"
                        >
                        </DataGrid>
                    </div>
                </div>
                <div class="flex flex-col space-y-2 p-2">
                    <div class="text-xl">Broker(s)</div>
                    <BrokerInformation
                        v-for="node in cluster.nodes"
                        :key="'cluster-' + cluster.id + '-node-' + node.id"
                        class="flex-1 rounded"
                        :node="node"
                    ></BrokerInformation>
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
import { computed, defineProps, ref } from 'vue'
import type { PropType } from 'vue'
import type { ClusterInformation } from '@/entity/ClusterInformation'
import PlusIcon from '@/icons/PlusIcon.vue'
import MinusIcon from '@/icons/MinusIcon.vue'
import BrokerInformation from '@/components/BrokerInformation.vue'
import StatusBulb from '@/components/StatusBulb.vue'
import type { Column } from '@/entity/Column'
import type { Filter } from '@/entity/Filter'
import type { Configuration } from '@/entity/ClusterInformation'
import { getFilterData, getPaginateItems, sortData } from '@/util/Util'
import type { Sorter } from '@/entity/Sorter'
import type { PagedData } from '@/entity/PagedData'
import DataGrid from '@/components/DataGrid/DataGrid.vue'

const props = defineProps({
    cluster: {
        type: Object as PropType<ClusterInformation>,
        required: true,
    },
})
const collapsed = ref<boolean>(false)
const sorters = ref<Sorter | null>({ property: 'name', direction: 'asc' })
const columns = ref<Column[]>([
    {
        dataId: 'name',
        type: 'string',
        title: 'Name',
        sortable: true,
        align: 'left',
        width: 'auto',
        filterable: true,
    },
    {
        dataId: 'value',
        type: 'string',
        title: 'Value',
        sortable: false,
        align: 'left',
        width: 540,
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
const data = computed<PagedData<Configuration>>(() => {
    let filteredSortedData = getSortedItems(
        getFilterData<Configuration>(props.cluster?.configurations ?? [], filters.value)
    )

    return getPaginateItems<Configuration>(filteredSortedData, 1, filteredSortedData.length ?? 0)
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
