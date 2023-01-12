<template>
    <div class="relative flex flex-col text-gray-800 shadow dark:border-gray-700 dark:text-gray-100">
        <FlyInPanel
            :loading="isDetailPanelLoading"
            :title="detailPanelTitle"
            v-if="showDetailPanel"
            @close="hideDetailPanel"
        >
            <div class="relative flex-1 overflow-auto rounded-b bg-white dark:bg-gray-900" v-if="!isDetailPanelLoading">
                <table
                    class="absolute table-fixed border-separate border-spacing-0 border border-b-0 bg-white dark:border-gray-700 dark:bg-gray-900"
                >
                    <thead
                        class="sticky top-0 bg-white p-2 text-green-500 shadow-sm shadow-green-50 dark:bg-gray-900 dark:text-gray-400 dark:shadow-black"
                    >
                        <tr class="font-normal uppercase">
                            <th class="w-64 border-b border-green-100 p-2 text-left font-normal dark:border-gray-700">
                                Topic
                            </th>
                            <th class="w-32 border-b border-green-100 p-2 text-center font-normal dark:border-gray-700">
                                Partition
                            </th>
                            <th class="w-48 border-b border-green-100 p-2 text-center font-normal dark:border-gray-700">
                                Current offset
                            </th>
                            <th class="w-32 border-b border-green-100 p-2 text-center font-normal dark:border-gray-700">
                                End offset
                            </th>
                            <th class="w-32 border-b border-green-100 p-2 text-center font-normal dark:border-gray-700">
                                Lag
                            </th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="groupDetail.length === 0">
                            <td colspan="5" class="border-b border-green-50 p-2 text-center dark:border-gray-700">
                                No data available...
                            </td>
                        </tr>
                        <tr
                            v-for="group in groupDetail"
                            :key="'consumer-group-' + group.name + group.partition"
                            class="hover:bg-green-50 dark:hover:bg-gray-800"
                        >
                            <td class="w-64 border-b border-green-50 p-2 text-left dark:border-gray-700">
                                {{ group?.topic }}
                            </td>
                            <td class="w-32 border-b border-green-50 p-2 text-center dark:border-gray-700">
                                {{ group?.partition }}
                            </td>
                            <td class="w-32 border-b border-green-50 p-2 text-center dark:border-gray-700">
                                {{ group?.currentOffset }}
                            </td>
                            <td class="w-32 border-b border-green-50 p-2 text-center dark:border-gray-700">
                                {{ group?.logEndOffset }}
                            </td>
                            <td class="w-32 border-b border-green-50 p-2 text-center dark:border-gray-700">
                                {{ group?.lag }}
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </FlyInPanel>
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
            :sorters="sorters"
            :embedded="embedded"
            :rowTitleRenderer="rowTitleRenderer"
            class="h-64 flex-grow"
        >
            <template v-if="!embedded" v-slot:actionColumn="{ row }">
                <div class="flex flex-1 justify-around">
                    <span
                        class="h-7 w-7 cursor-pointer rounded transition-all duration-100 ease-linear hover:h-8 hover:w-8"
                        :title="'View detail of Consumer Group: ' + row.name"
                        @click="loadDetailAndStartTicker(row.name)"
                    >
                        <SearchIcon class="h-full w-full p-1 text-green-500 dark:text-white"></SearchIcon>
                    </span>
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
import { consumerGroupStore } from '@/stores/ConsumerGroupStore'
import { computed, defineProps, onMounted, onUnmounted, type PropType, ref } from 'vue'
import DataGrid from '@/components/DataGrid/DataGrid.vue'
import SearchIcon from '@/icons/SearchIcon.vue'
import type { Column } from '@/entity/Column'
import type { Filter } from '@/entity/Filter'
import type { Sorter } from '@/entity/Sorter'
import type { PagedData } from '@/entity/PagedData'
import type { ConsumerGroup } from '@/entity/ConsumerGroup'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import eventBus from '@/util/EventBus'
import MessageDialog from '@/components/MessageDialog.vue'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import { Buttons } from '@/entity/Buttons'
import type { ErrorResponse } from '@/entity/ErrorResponse'
import FlyInPanel from '@/components/FlyInPanel.vue'
import { getAction } from '@/util/HttpService'
import type { ServerResponse } from '@/entity/ServerResponse'
import { useRoute } from 'vue-router'

const props = defineProps({
    embedded: {
        type: Boolean as PropType<boolean>,
        required: false,
        default: false,
    },
})

const store = consumerGroupStore()
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
])

if (!props.embedded) {
    columns.value.push({
        dataId: 'actionColumn',
        type: 'number',
        width: 100,
        renderCustom: true,
        align: 'center',
        title: 'Action',
    })
}
const clusterId = useRoute().params.clusterId as string
const filters = ref<Filter[]>([])
const sorters = ref<Sorter | null>(null)
const gridLoading = ref<boolean>()
const gridPageSize = ref<number>(50)
const gridCurrentPage = ref<number>(1)
const messageDialogTitle = ref<string>('')
const messageDialogIcon = ref<'info' | 'question' | 'alert' | 'error' | 'success'>('error')
const messageDialogButtons = ref<Buttons>(Buttons.ok)
const messageDialogMessage = ref<string>('')
const showMessageDialog = ref<boolean>(false)
const showDetailPanel = ref<boolean>(false)
const isDetailPanelLoading = ref<boolean>(false)
const detailPanelTitle = ref<string>('')
const groupDetail = ref<ConsumerGroup[]>([])
const data = computed<PagedData<ConsumerGroup>>(() => {
    return store.getPaginatedItems(
        gridCurrentPage.value,
        gridPageSize.value,
        store.getSortedItems(
            store.getFilteredItems(clusterId, filters.value, null),
            sorters.value === null ? [] : [sorters.value]
        )
    )
})
let refreshInterval = -1
let detailRefreshInterval = -1

function onSortClick(sorter: Sorter | null): void {
    sorters.value = sorter
}

function hideDetailPanel() {
    if (detailRefreshInterval !== -1) {
        window.clearInterval(detailRefreshInterval)
    }
    showDetailPanel.value = false
}
function onFilterClick(appliedFilter: Filter[]) {
    gridCurrentPage.value = 1
    filters.value = appliedFilter
}

function onPageChange(page: number) {
    gridCurrentPage.value = page
}

async function loadData() {
    await store.loadData(clusterId)
}

function rowTitleRenderer(row: ConsumerGroup) {
    return `Name: ${row.name}`
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
        if (refreshInterval !== -1) {
            window.clearInterval(refreshInterval)
        }
    }
}

async function loadDetailAndStartTicker(groupName: string) {
    await loadDetail(groupName)
    detailRefreshInterval = window.setInterval(async () => {
        await loadDetail(groupName)
    }, 120000)
}

async function loadDetail(groupName: string) {
    isDetailPanelLoading.value = true
    showDetailPanel.value = true
    detailPanelTitle.value = 'Detail of Consumer Group: ' + groupName
    await getAction(
        `${clusterId}/consumer-groups/${groupName}`,
        (response: ServerResponse<ConsumerGroup[]>) => {
            isDetailPanelLoading.value = false
            groupDetail.value = response.data
        },
        (error: ErrorResponse) => {
            isDetailPanelLoading.value = false
            messageDialogMessage.value = error.response
            messageDialogTitle.value = error.httpCode + ' - ' + error.httpStatus
            messageDialogIcon.value = 'error'
            showMessageDialog.value = true
            if (detailRefreshInterval !== -1) {
                window.clearInterval(detailRefreshInterval)
            }
        }
    )
}

onMounted(async () => {
    eventBus.on(ApplicationEventTypes.BEFORE_CONSUMER_GROUP_DATA_LOADED, beforeDataLoad)
    eventBus.on(ApplicationEventTypes.CONSUMER_GROUP_DATA_LOADED, afterDataLoad)
    await store.loadDataIfNotInitialized(clusterId)
    refreshInterval = window.setInterval(() => {
        store.loadData(clusterId)
    }, 120000)
})

onUnmounted(() => {
    eventBus.off(ApplicationEventTypes.BEFORE_CONSUMER_GROUP_DATA_LOADED, beforeDataLoad)
    eventBus.off(ApplicationEventTypes.CONSUMER_GROUP_DATA_LOADED, afterDataLoad)
    window.clearInterval(refreshInterval)
    if (detailRefreshInterval !== -1) {
        window.clearInterval(detailRefreshInterval)
    }
})
</script>
