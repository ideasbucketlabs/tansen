<template>
    <div class="relative flex flex-col text-gray-800 shadow dark:border-gray-700 dark:text-gray-100">
        <MessageDialog
            v-if="showMessageDialog"
            :title="messageDialogTitle"
            :icon="messageDialogIcon"
            :buttons="messageDialogButtons"
            @close="showMessageDialog = false"
        >
            {{ messageDialogMessage }}
        </MessageDialog>
        <DataGrid
            :columns="columns"
            :data="data"
            @sort="onSortClick"
            @pageChange="onPageChange"
            :loading="gridLoading"
            @refreshData="loadData"
            @filter="onFilterClick"
            :embedded="embedded"
            :sorters="sorters"
            :rowTitleRenderer="rowTitleRenderer"
            class="h-64 flex-grow shadow"
        >
            <template v-slot:actionColumn="{ row }">
                <div class="flex flex-1 justify-around">
                    <router-link
                        class="h-7 w-7 rounded transition-all duration-100 ease-linear hover:h-8 hover:w-8"
                        :to="{ name: 'topicOverview', params: { clusterId: clusterId, name: row.topic } }"
                        :title="'View detail of Topic: ' + row.topic"
                    >
                        <SearchIcon class="h-full w-full p-1 text-green-500 dark:text-white"></SearchIcon>
                    </router-link>
                </div>
            </template>
        </DataGrid>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { useRoute } from 'vue-router'
import { computed, defineProps, onMounted, onUnmounted, type PropType, ref } from 'vue'
import { topicStore } from '@/stores/TopicStore'
import DataGrid from '@/components/DataGrid/DataGrid.vue'
import MessageDialog from '@/components/MessageDialog.vue'
import SearchIcon from '@/icons/SearchIcon.vue'
import type { Column } from '@/entity/Column'
import type { Filter } from '@/entity/Filter'
import type { Sorter } from '@/entity/Sorter'
import type { PagedData } from '@/entity/PagedData'
import type { Topic } from '@/entity/Topic'
import eventBus from '@/util/EventBus'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import { Buttons } from '@/entity/Buttons'
import type { ErrorResponse } from '@/entity/ErrorResponse'

const clusterId = ref<string>(useRoute().params.clusterId as string)
const store = topicStore()
defineProps({
    embedded: {
        type: Boolean as PropType<boolean>,
        required: false,
        default: false,
    },
})

const columns = ref<Column[]>([
    {
        dataId: 'topic',
        type: 'string',
        title: 'Topic',
        sortable: true,
        align: 'left',
        width: 'auto',
        filterable: true,
    },
    {
        dataId: 'totalPartitions',
        type: 'number',
        title: 'Total Partition(s)',
        sortable: true,
        align: 'center',
        width: 200,
        filterable: true,
    },
    {
        dataId: 'offlineReplicas',
        type: 'number',
        title: 'Out of sync replicas',
        sortable: true,
        align: 'center',
        width: 220,
        filterable: true,
    },
    {
        dataId: 'replicationFactor',
        type: 'number',
        title: 'Replication factor',
        sortable: true,
        align: 'center',
        width: 220,
        filterable: true,
    },
    {
        dataId: 'isInternal',
        type: 'boolean',
        title: 'Is internal',
        sortable: false,
        align: 'left',
        width: 120,
        filterable: false,
    },
    {
        dataId: 'actionColumn',
        type: 'number',
        width: 100,
        renderCustom: true,
        align: 'center',
        title: 'Actions',
    },
])

const filters = ref<Filter[]>([])
const sorters = ref<Sorter | null>({ property: 'name', direction: 'asc' })
const gridLoading = ref<boolean>(false)
const gridPageSize = ref<number>(50)
const gridCurrentPage = ref<number>(1)
const messageDialogTitle = ref<string>('')
const messageDialogIcon = ref<'info' | 'question' | 'alert' | 'error' | 'success'>('error')
const messageDialogButtons = ref<Buttons>(Buttons.ok)
const messageDialogMessage = ref<string>('')
const showMessageDialog = ref<boolean>(false)

const data = computed<PagedData<Topic>>(() => {
    return store.getPaginatedItems(
        gridCurrentPage.value,
        gridPageSize.value,
        store.getSortedItems(
            store.getFilteredItems(clusterId.value, filters.value),
            sorters.value === null ? [] : [sorters.value]
        )
    )
})

function onSortClick(sorter: Sorter | null): void {
    sorters.value = sorter
}

function onFilterClick(appliedFilter: Filter[]) {
    gridCurrentPage.value = 1
    filters.value = appliedFilter
}

function onPageChange(page: number) {
    gridCurrentPage.value = page
}

async function loadData() {
    await store.loadData(clusterId.value)
}

function rowTitleRenderer(row: Topic) {
    return `Topic: ${row.topic} \u000AUUID: ${row.uuid}`
}

function beforeDataLoad() {
    gridLoading.value = true
}

function afterDataLoad(applicationEvent: ApplicationEvent) {
    gridLoading.value = false
    if (applicationEvent.success) {
        return
    }
    if (applicationEvent.data !== undefined) {
        const error = applicationEvent.data as ErrorResponse
        messageDialogMessage.value = error.response
        messageDialogTitle.value = error.httpCode + ' - ' + error.httpStatus
        messageDialogIcon.value = 'error'
        showMessageDialog.value = true
    }
}

onMounted(async () => {
    eventBus.on(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, beforeDataLoad)
    eventBus.on(ApplicationEventTypes.TOPIC_DATA_LOADED, afterDataLoad)
    await store.loadDataIfNotInitialized(clusterId.value)
})

onUnmounted(() => {
    eventBus.off(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, beforeDataLoad)
    eventBus.off(ApplicationEventTypes.TOPIC_DATA_LOADED, afterDataLoad)
})
</script>
