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
            :sorters="sorters"
            :rowTitleRenderer="rowTitleRenderer"
            class="flex-1"
        ></DataGrid>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { computed, onMounted, onUnmounted, type PropType, ref } from 'vue'
import DataGrid from '@/components/DataGrid/DataGrid.vue'
import type { Column } from '@/entity/Column'
import type { Filter } from '@/entity/Filter'
import type { Sorter } from '@/entity/Sorter'
import type { PagedData } from '@/entity/PagedData'
import type { ConsumerGroup } from '@/entity/ConsumerGroup'
import { consumerGroupStore } from '@/stores/ConsumerGroupStore'
import MessageDialog from '@/components/MessageDialog.vue'
import { topicStore } from '@/stores/TopicStore'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import type { ErrorResponse } from '@/entity/ErrorResponse'
import { Buttons } from '@/entity/Buttons'
import { useRoute } from 'vue-router'

const props = defineProps({
    topic: {
        type: String as PropType<string>,
        required: true,
    },
})
const clusterId = useRoute().params.clusterId as string
const consumers = consumerGroupStore()
const topics = topicStore()
const columns = ref<Column[]>([
    {
        dataId: 'name',
        type: 'string',
        title: 'Consumer Group Name',
        sortable: true,
        align: 'left',
        width: 'auto',
        filterable: true,
    },
    {
        dataId: 'topic',
        type: 'string',
        title: 'Topic',
        sortable: false,
        align: 'left',
        width: 350,
        filterable: false,
    },
    {
        dataId: 'partition',
        type: 'string',
        title: 'Partition',
        sortable: true,
        align: 'center',
        width: 150,
        filterable: true,
    },
    {
        dataId: 'currentOffset',
        type: 'string',
        title: 'Current Offset',
        sortable: true,
        align: 'center',
        width: 200,
        filterable: true,
    },
    {
        dataId: 'logEndOffset',
        type: 'string',
        title: 'Log End Offset',
        sortable: true,
        align: 'center',
        width: 200,
        filterable: true,
    },
    {
        dataId: 'lag',
        type: 'string',
        title: 'Lag',
        sortable: true,
        align: 'center',
        width: 120,
        filterable: true,
    },
])

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

const data = computed<PagedData<ConsumerGroup>>(() => {
    return consumers.getPaginatedItems(
        gridCurrentPage.value,
        gridPageSize.value,
        consumers.getSortedItems(
            consumers.getFilteredItems(clusterId, filters.value, topics.getConsumerGroups(clusterId, props.topic)),
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

function loadData() {
    topics.loadTopicConsumers(clusterId, props.topic).then()
}

function rowTitleRenderer(row: ConsumerGroup) {
    return `Name: ${row.name}\u000ATopic: ${row.topic}`
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
    eventBus.on(ApplicationEventTypes.BEFORE_TOPIC_CONSUMER_DATA_LOADED, beforeDataLoad)
    eventBus.on(ApplicationEventTypes.TOPIC_CONSUMER_DATA_LOADED, afterDataLoad)
    await topics.loadTopicConsumersIfNotLoaded(clusterId, props.topic)
})

onUnmounted(() => {
    eventBus.off(ApplicationEventTypes.BEFORE_TOPIC_CONSUMER_DATA_LOADED, beforeDataLoad)
    eventBus.off(ApplicationEventTypes.TOPIC_CONSUMER_DATA_LOADED, afterDataLoad)
})
</script>
