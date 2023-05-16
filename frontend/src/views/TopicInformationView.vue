<template>
    <div class="relative flex flex-1 flex-col overflow-auto" v-if="currentSelectedIndex === 0">
        <div class="pt-2">
            <div class="mt-2 flex flex-col rounded shadow" v-if="topicDetails.configurations.length !== 0">
                <div class="block flex items-center space-x-2 rounded-t bg-green-500 text-white dark:bg-gray-700">
                    <div class="px-2 py-2 uppercase">Detail Configurations</div>
                    <div class="transform-none py-2 text-xs text-green-200 dark:text-gray-400">
                        Hover over the row for more detail
                    </div>
                </div>
                <div>
                    <DataGrid
                        :columns="topicConfigurationsColumns"
                        :show-pager="false"
                        :embedded="true"
                        identifier="name"
                        :key="topicConfigurationViewRenderId"
                        :data="topicConfigurationData"
                        :sorters="topicConfigurationSorters"
                        :virtual="true"
                        :row-title-renderer="topicConfigurationRowTitleRenderer"
                        @filter="onTopicConfigurationsFilterApply"
                        @sort="onTopicConfigurationsSortClick"
                        class="h-[13.5rem] flex-grow rounded-b"
                    >
                    </DataGrid>
                </div>
            </div>
        </div>
        <div class="flex h-[13rem] flex-col py-2">
            <div class="flex space-x-2 md:text-xl">
                <div>
                    Total Partition(s): <span class="font-bold">{{ topicDetails.numberOfPartition }}</span>
                </div>
                <div>|</div>
                <div>
                    Replication factor: <span class="font-bold">{{ topicDetails.replicationFactor }}</span>
                </div>
            </div>
            <data-grid
                :columns="topicDetailColumns"
                :data="pagedTopicPartitionInformation"
                :rowTitleRenderer="rowTitleRenderer"
                :show-pager="false"
                :virtual="true"
                @filter="onPartitionInformationFilterApply"
                class="flex-1 rounded shadow"
            ></data-grid>
        </div>
        <h2 class="text-lg md:text-xl" v-if="!showConsumerGroupDetail">Consumer Group(s)</h2>
        <button
            v-if="!showConsumerGroupDetail"
            @click="showConsumerGroupDetail = true"
            type="button"
            title="Load consumer(s) group for this topic"
            class="w-32 rounded border border-green-500 bg-transparent p-2 uppercase text-green-500 transition duration-200 ease-linear hover:bg-green-100 hover:shadow-lg dark:border-gray-100 dark:text-gray-100 dark:hover:bg-gray-700 dark:hover:shadow-black"
        >
            Show
        </button>
        <topic-consumer-group-grid
            v-if="showConsumerGroupDetail"
            class="h-10 flex-1"
            :topic="$route.params.name.toString()"
        ></topic-consumer-group-grid>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import TopicConsumerGroupGrid from '@/components/TopicConsumerGroupGrid.vue'
import type { Partition, TopicDetails } from '@/entity/TopicDetails'
import DataGrid from '@/components/DataGrid/DataGrid.vue'
import type { Column } from '@/entity/Column'
import type { PagedData } from '@/entity/PagedData'
import { computed, onActivated, type PropType, ref } from 'vue'
import type { Filter } from '@/entity/Filter'
import type { Configuration } from '@/entity/TopicDetails'
import type { Sorter } from '@/entity/Sorter'
import { getFilterData, getId, getPaginateItems, sortData } from '@/util/Util'

const props = defineProps({
    topicDetails: {
        type: Object as PropType<TopicDetails>,
        required: true,
    },
    topicPartitionInformation: {
        type: Array as PropType<Partition[]>,
        required: true,
    },
})

const topicConfigurationViewRenderId = ref<string>(getId())
const topicDetailColumns = ref<Column[]>([
    {
        dataId: 'partition',
        type: 'number',
        title: 'Partition No.',
        sortable: false,
        align: 'center',
        width: 130,
        filterable: false,
    },
    {
        dataId: 'leader',
        type: 'number',
        title: 'Leader',
        sortable: false,
        align: 'center',
        width: 100,
        filterable: false,
    },
    {
        dataId: 'replicas',
        type: 'string',
        title: 'Replica',
        sortable: false,
        align: 'left',
        width: 'auto',
        filterable: false,
    },
    {
        dataId: 'isr',
        type: 'number',
        title: 'ISR',
        sortable: false,
        align: 'left',
        width: 260,
        filterable: false,
    },
    {
        dataId: 'isOffline',
        type: 'boolean',
        title: 'Offline',
        sortable: false,
        align: 'left',
        width: 100,
        filterable: true,
    },
    {
        dataId: 'inSyncReplicaCount',
        type: 'number',
        title: 'In Sync Replica Count',
        sortable: false,
        align: 'center',
        width: 210,
        filterable: false,
    },
    {
        dataId: 'outOfSyncReplicaCount',
        type: 'number',
        title: 'Out of Sync Replica Count',
        sortable: false,
        align: 'center',
        width: 260,
        filterable: false,
    },
    {
        dataId: 'isUnderReplicated',
        type: 'boolean',
        title: 'Under Replicated',
        sortable: false,
        align: 'left',
        width: 200,
        filterable: true,
    },
])

const topicConfigurationsColumns = ref<Column[]>([
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
        width: 430,
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
const currentSelectedIndex = ref<number>(0)
const showConsumerGroupDetail = ref<boolean>(false)
const topicConfigurationFilters = ref<Filter[]>([])
const partitionInformationFilters = ref<Filter[]>([])
const topicConfigurationSorters = ref<Sorter | null>({ property: 'name', direction: 'asc' })

const topicConfigurationData = computed<PagedData<Configuration>>(() => {
    let filteredSortedData = getSortedItems(
        getFilterData<Configuration>(props.topicDetails.configurations ?? [], topicConfigurationFilters.value)
    )

    return getPaginateItems<Configuration>(filteredSortedData, 1, filteredSortedData.length ?? 0)
})

const pagedTopicPartitionInformation = computed<PagedData<Partition>>(() => {
    const data = getFilterData<Partition>(props.topicPartitionInformation ?? [], partitionInformationFilters.value)
    return getPaginateItems<Partition>(data, 1, data.length ?? 0)
})

function getSortedItems(data: Configuration[]) {
    if (!Array.isArray(data) || !data.length) {
        return data
    }

    if (topicConfigurationSorters.value === null) {
        return data
    }

    return sortData<Configuration>(data, [topicConfigurationSorters.value])
}

function rowTitleRenderer(row: Partition) {
    return `Partition: ${row.partition}`
}

function topicConfigurationRowTitleRenderer(row: Configuration) {
    if (row.definition === '') {
        return `Name: ${row.name} \u000A\u000AType: ${row.type}\u000A\u000AValue: ${row.value}`
    }

    const definition = row.definition.replace(/<code>/g, '').replace(/<\/code>/g, '')
    // eslint-disable-next-line vue/max-len
    return `Name: ${row.name} \u000A\u000AType: ${row.type}\u000A\u000AValue: ${row.value}\u000A\u000ADefinition: ${definition}`
}

function onTopicConfigurationsFilterApply(appliedFilters: Filter[]) {
    topicConfigurationFilters.value = appliedFilters
}

function onPartitionInformationFilterApply(appliedFilters: Filter[]) {
    partitionInformationFilters.value = appliedFilters
}

function onTopicConfigurationsSortClick(sorter: Sorter | null): void {
    topicConfigurationSorters.value = sorter
}

onActivated(() => {
    topicConfigurationViewRenderId.value = getId()
})
</script>
