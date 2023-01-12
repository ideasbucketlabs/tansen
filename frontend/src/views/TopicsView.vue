<template>
    <div class="relative flex flex-1 flex-col">
        <AddTopicForm
            v-if="showAddTopicForm"
            @close="showAddTopicForm = false"
            @submit="addTopic"
            :loading="isFormLoading"
        ></AddTopicForm>
        <MessageDialog
            v-if="showMessageDialog"
            :title="messageDialogTitle"
            :icon="messageDialogIcon"
            @close="showMessageDialog = false"
            :buttons="Buttons.ok"
        >
            <div v-html="messageDialogMessage"></div>
        </MessageDialog>
        <div class="flex items-center justify-between text-green-500 dark:text-gray-100">
            <div class="flex items-center">
                <div class="mr-2 flex h-6 w-6 items-center" aria-hidden="true" aria-label="Dashboard">
                    <TopicsIcon class="h-full w-full fill-current"></TopicsIcon>
                </div>
                <h1 class="text-xl md:text-3xl">Topics</h1>
            </div>
            <button
                type="button"
                title="Create topic"
                @click="showAddTopicForm = true"
                class="relative flex h-9 items-center space-x-2 overflow-hidden rounded border border-green-500 px-2 transition duration-200 ease-linear hover:bg-green-100 hover:shadow-lg dark:border-gray-600 dark:bg-gray-700 dark:hover:bg-gray-600"
            >
                <span class="block w-4"><PlusIcon class="w-full fill-current" :with-ring="false"></PlusIcon></span>
                <span class="block">Create topic</span>
                <ripple></ripple>
            </button>
        </div>
        <div class="flex-1 overflow-y-auto md:flex">
            <div class="h-full md:flex md:h-auto md:flex-1 md:flex-col">
                <div class="h-full min-h-80 w-full md:flex md:h-auto md:min-h-[40rem] md:flex-1">
                    <div class="mt-2 h-full rounded pb-4 shadow-sm md:flex md:h-auto md:flex-1 md:flex-col md:pb-0">
                        <topic-grid
                            class="h-full min-h-80 rounded border border-transparent dark:border-gray-800 md:h-auto md:flex-1"
                        ></topic-grid>
                    </div>
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
import TopicGrid from '@/components/TopicGrid.vue'
import TopicsIcon from '@/icons/TopicsIcon.vue'
import PlusIcon from '@/icons/PlusIcon.vue'
import Ripple from '@/components/Ripple.vue'
import { defineAsyncComponent, onMounted, onUnmounted, ref } from 'vue'
import AppComponentLoader from '@/components/AppComponentLoader.vue'
import type { NewTopic } from '@/entity/NewTopic'
import { topicStore } from '@/stores/TopicStore'
import { useRoute } from 'vue-router'
import MessageDialog from '@/components/MessageDialog.vue'
import { Buttons } from '@/entity/Buttons'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import type { ErrorResponse } from '@/entity/ErrorResponse'
import { filterOutCommonErrorAttributes } from '@/util/Util'

const clusterId = useRoute().params.clusterId as string
const showAddTopicForm = ref<boolean>(false)
const store = topicStore()
const isFormLoading = ref<boolean>(false)

const AddTopicForm = defineAsyncComponent({
    loader: () => import('@/forms/AddTopicForm.vue'),
    loadingComponent: AppComponentLoader,
    delay: 200,
})
const showMessageDialog = ref<boolean>(false)
const messageDialogTitle = ref<string>('')
const messageDialogIcon = ref<'info' | 'question' | 'alert' | 'error' | 'success'>('error')
const messageDialogMessage = ref<string>('')
async function addTopic(newTopic: NewTopic) {
    isFormLoading.value = true
    await store.addTopic(clusterId, newTopic)
}

function afterTopicAdded(applicationEvent: ApplicationEvent) {
    if (applicationEvent.success) {
        isFormLoading.value = false
        showAddTopicForm.value = false

        messageDialogIcon.value = 'success'
        messageDialogTitle.value = 'Success'
        messageDialogMessage.value = 'Topic added successfully.'
        showMessageDialog.value = true
    } else {
        isFormLoading.value = false
        const error = applicationEvent.data as ErrorResponse
        messageDialogIcon.value = 'error'
        messageDialogTitle.value = `Error ${error.httpCode} - ${error.httpStatus}`
        messageDialogMessage.value = Object.values(
            filterOutCommonErrorAttributes((error?.errors as Record<string, string>) ?? {})
        ).join('</br>')
        showMessageDialog.value = true
    }
}

onMounted(() => {
    eventBus.on(ApplicationEventTypes.TOPIC_ADDED, afterTopicAdded)
})

onUnmounted(() => {
    eventBus.off(ApplicationEventTypes.TOPIC_ADDED, afterTopicAdded)
})
</script>
