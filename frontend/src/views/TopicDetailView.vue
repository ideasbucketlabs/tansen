<template>
    <div class="relative flex flex-1 flex-col overflow-hidden">
        <Loading v-if="showDeletionProgress">Deletion in progress...please wait</Loading>
        <edit-topic-form
            :topic-details="topicDetails"
            v-if="showEditForm && topicDetails !== undefined"
            @close="showEditForm = false"
            :loading="isFormLoading"
            @submit="editTopic"
        ></edit-topic-form>
        <MessageDialog
            v-if="showMessageDialog"
            :title="messageDialogTitle"
            :icon="messageDialogIcon"
            :buttons="messageDialogButtons"
            @close="onCloseDialog"
        >
            <div v-html="messageDialogMessage"></div>
            <base-input-field
                v-if="messageDialogTitle === 'Confirm marked for deletion'"
                v-model="deleteTopicName"
                label="Please enter topic name"
                class="my-2"
            ></base-input-field>
        </MessageDialog>

        <div class="flex items-center justify-between text-green-500 dark:text-gray-100">
            <h1 class="text-xl md:text-3xl">Detail for topic: {{ $route.params.name }}</h1>
            <div class="flex items-center">
                <div class="action-menu relative lg:w-auto" tabindex="-1">
                    <button
                        type="button"
                        class="relative flex items-center overflow-hidden rounded border border-green-500 px-4 py-2 text-green-500 transition duration-200 ease-linear hover:bg-green-100 hover:shadow dark:border-gray-300 dark:text-gray-100 dark:shadow-black dark:hover:bg-gray-600"
                    >
                        <ActionIcon class="h-6 w-6 fill-current"></ActionIcon>
                    </button>
                    <div class="absolute right-0 z-10 w-52 pt-2">
                        <ul
                            class="cursor-pointer rounded border border-green-500 bg-white shadow-lg dark:border-gray-300 dark:bg-gray-800"
                        >
                            <li
                                class="rounded-t border-b border-t border-white transition duration-200 ease-linear hover:border-green-100 hover:bg-green-50 dark:border-gray-800 dark:hover:border-gray-700 dark:hover:border-gray-900 dark:hover:bg-gray-600"
                            >
                                <router-link
                                    class="block w-full px-4 py-2"
                                    :to="{ name: 'topics', params: { clusterId: clusterId } }"
                                    >Back to Topic(s)</router-link
                                >
                            </li>
                            <li
                                v-if="topicDetails !== undefined"
                                class="rounded-t border-b border-t border-white px-4 py-2 transition duration-200 ease-linear hover:border-green-100 hover:bg-green-50 dark:border-gray-800 dark:hover:border-gray-700 dark:hover:border-gray-900 dark:hover:bg-gray-600"
                                @click="showEditForm = true"
                            >
                                Edit Topic
                            </li>
                            <li
                                class="rounded-b rounded-t border-b border-t border-white px-4 py-2 text-red-500 transition duration-200 ease-linear hover:border-red-100 hover:bg-red-50 dark:border-gray-800 dark:hover:border-red-900 dark:hover:bg-red-600/30"
                                title="Be careful with this action."
                                @click="deleteTopicAction"
                            >
                                Delete Topic
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="mt-2 space-x-6 border-b-2 border-green-100 dark:border-gray-600">
            <router-link
                v-if="isDetailLoaded"
                class="relative top-1 inline-block w-24 py-1 text-center outline-none after:absolute after:inset-x-0 after:mx-auto after:block after:h-0.5 after:transition-all after:duration-200 after:ease-linear"
                :class="[
                    $route.name === 'topicOverview'
                        ? 'after:w-full after:bg-green-500 dark:border-gray-600 dark:after:bg-gray-200'
                        : 'after:w-1/3 hover:after:bg-green-300 dark:hover:after:bg-gray-400',
                ]"
                :to="{ name: 'topicOverview', params: { clusterId: clusterId } }"
                >Overview
            </router-link>
            <router-link
                v-if="isDetailLoaded"
                class="relative top-1 inline-block w-24 py-1 text-center outline-none after:absolute after:inset-x-0 after:mx-auto after:block after:h-0.5 after:transition-all after:duration-200 after:ease-linear"
                :class="[
                    $route.name === 'topicSchema' ||
                    $route.name === 'topicSchemaKey' ||
                    $route.name === 'topicSchemaValue'
                        ? 'after:w-full after:bg-green-500 dark:border-gray-600 dark:after:bg-gray-200'
                        : 'after:w-1/3 hover:after:bg-green-300 dark:hover:after:bg-gray-400',
                ]"
                :to="{ name: 'topicSchemaValue', params: { clusterId: clusterId } }"
                >Schema
            </router-link>
            <router-link
                v-if="isDetailLoaded"
                class="relative top-1 inline-block w-24 py-1 text-center outline-none after:absolute after:inset-x-0 after:mx-auto after:block after:h-0.5 after:transition-all after:duration-200 after:ease-linear"
                :class="[
                    $route.name === 'topicMessages'
                        ? 'after:w-full after:bg-green-500 dark:border-gray-600 dark:after:bg-gray-200'
                        : 'after:w-1/3 hover:after:bg-green-300 dark:hover:after:bg-gray-400',
                ]"
                :to="{ name: 'topicMessages', params: { clusterId: clusterId } }"
                >Messages
            </router-link>
        </div>
        <router-view v-slot="{ Component }" v-if="isDetailLoaded">
            <transition name="fade" mode="out-in">
                <KeepAlive include="TopicInformationView" :max="7">
                    <component
                        :is="Component"
                        :topic-details="topicDetails"
                        :topicPartitionInformation="topicPartitionInformation"
                        :topic="$route.params.name"
                        :number-of-partition="topicDetails?.numberOfPartition ?? 0"
                    />
                </KeepAlive>
            </transition>
        </router-view>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { onMounted, onUnmounted, ref, watch } from 'vue'
import { getAction } from '@/util/HttpService'
import { useRoute } from 'vue-router'
import type { ServerResponse } from '@/entity/ServerResponse'
import type { Partition, TopicDetails } from '@/entity/TopicDetails'
import MessageDialog from '@/components/MessageDialog.vue'
import { Buttons } from '@/entity/Buttons'
import type { ErrorResponse } from '@/entity/ErrorResponse'
import { useRouter } from 'vue-router'
import ActionIcon from '@/icons/ActionIcon.vue'
import BaseInputField from '@/components/BaseInputField.vue'
import type { MessageDialogEvent } from '@/entity/MessageDialogEvent'
import Loading from '@/components/Loading.vue'
import { topicStore as store } from '@/stores/TopicStore'
import { clusterInformationStore } from '@/stores/ClusterInformationStore'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import { filterOutCommonErrorAttributes } from '@/util/Util'
import EditTopicForm from '@/forms/EditTopicForm.vue'
import type { NewTopic } from '@/entity/NewTopic'

const router = useRouter()
const route = useRoute()
const clusterId = ref<string>(useRoute().params.clusterId as string)
const topicStore = store()
const cluster = clusterInformationStore()
const isDetailLoaded = ref<boolean>(false)
const topicDetails = ref<TopicDetails>()
const deleteTopicName = ref<string>('')
const topicPartitionInformation = ref<Partition[]>()
const messageDialogTitle = ref<string>('')
const messageDialogIcon = ref<'info' | 'question' | 'alert' | 'error' | 'success'>('error')
const messageDialogButtons = ref<Buttons>(Buttons.ok)
const messageDialogMessage = ref<string>('')
const showMessageDialog = ref<boolean>(false)
const showDeletionProgress = ref<boolean>(false)
const showEditForm = ref<boolean>(false)
const isFormLoading = ref<boolean>(false)

watch(deleteTopicName, (newDeleteTopicName) => {
    if (newDeleteTopicName === route.params.name) {
        messageDialogButtons.value = Buttons.delete | Buttons.cancel
    } else {
        messageDialogButtons.value = Buttons.deleteDisabled | Buttons.cancel
    }
})
function deleteTopicAction() {
    if (!cluster.isDeleteEnabledInCluster(clusterId.value)) {
        // eslint-disable-next-line vue/max-len
        messageDialogMessage.value = `<div>Topic deletion is not allowed in this cluster. <pre class="inline-block">delete.topic.enable</pre> value is false in cluster settings.</div>`
        messageDialogTitle.value = 'Not allowed'
        messageDialogIcon.value = 'error'
        messageDialogButtons.value = Buttons.ok
        showMessageDialog.value = true

        return
    }

    const topicName = route.params.name
    // eslint-disable-next-line vue/max-len
    messageDialogMessage.value = `<div>Are you sure you want to mark <b>${topicName}</b> for deletion?</div><div>The topic and all its associated data will be deleted. Deleting a topic is an asynchronous operation that may take a while. In the interim, you will not be able to create a topic with the same name as the one being deleted.</div>`
    messageDialogTitle.value = 'Confirm marked for deletion'
    messageDialogIcon.value = 'alert'
    messageDialogButtons.value = Buttons.deleteDisabled | Buttons.cancel
    showMessageDialog.value = true
}

async function onCloseDialog(messageDialogEvent: MessageDialogEvent) {
    showMessageDialog.value = false
    deleteTopicName.value = ''

    if (messageDialogEvent.condition === 'delete') {
        showDeletionProgress.value = true
        await topicStore.deleteTopic(clusterId.value, route.params.name as string)
    }
}

async function afterTopicDeletion(applicationEvent: ApplicationEvent) {
    showDeletionProgress.value = false

    if (applicationEvent.success) {
        await router.replace({ name: 'topics', query: { topic: 'delete' } })
    } else {
        const error = applicationEvent.data as ErrorResponse
        if (error.httpCode === 404) {
            await router.replace({ name: 'topics' })
        } else {
            messageDialogMessage.value = Object.values(
                filterOutCommonErrorAttributes((error?.errors as Record<string, string>) ?? {})
            ).join('</br>')
            messageDialogTitle.value = 'Error'
            messageDialogIcon.value = 'error'
            messageDialogButtons.value = Buttons.ok
            showMessageDialog.value = true
        }
    }
}

async function afterTopicEdit(applicationEvent: ApplicationEvent) {
    if (applicationEvent.success) {
        await getAction(
            clusterId.value + '/topics/' + route.params.name,
            (response: ServerResponse<TopicDetails>) => {
                topicDetails.value = response.data
                topicPartitionInformation.value = response.data.partitions
                isDetailLoaded.value = true
                isFormLoading.value = false
                showEditForm.value = false

                messageDialogIcon.value = 'success'
                messageDialogTitle.value = 'Success'
                messageDialogMessage.value = 'Topic edited successfully.'
                messageDialogButtons.value = Buttons.ok
                showMessageDialog.value = true
            },
            (error: ErrorResponse) => {
                messageDialogMessage.value =
                    'Topic was edited but we were unable to load recent changes. ' +
                    (error?.errors as Record<string, string>)?.response
                messageDialogTitle.value = error.httpCode + ' - ' + error.httpStatus
                messageDialogIcon.value = 'error'
                showMessageDialog.value = true
                messageDialogButtons.value = Buttons.ok
                isFormLoading.value = false
            }
        )
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

async function editTopic(data: { oldRecord: NewTopic; newRecord: NewTopic }) {
    isFormLoading.value = true
    await topicStore.editTopic(clusterId.value, data, route.params.name as string)
}

onMounted(async () => {
    eventBus.on(ApplicationEventTypes.TOPIC_DELETED, afterTopicDeletion)
    eventBus.on(ApplicationEventTypes.TOPIC_EDITED, afterTopicEdit)
    await getAction(
        clusterId.value + '/topics/' + route.params.name,
        (response: ServerResponse<TopicDetails>) => {
            topicDetails.value = response.data
            topicPartitionInformation.value = response.data.partitions
            isDetailLoaded.value = true

            if (route.name === 'topicDetail') {
                router.push({
                    name: 'topicOverview',
                    params: { name: route.params.name },
                })
            }
        },
        (error: ErrorResponse) => {
            if (error.httpCode === 404) {
                router.push({
                    name: 'topics',
                })
                return
            }
            messageDialogMessage.value = (error?.errors as Record<string, string>)?.response
            messageDialogTitle.value = error.httpCode + ' - ' + error.httpStatus
            messageDialogIcon.value = 'error'
            showMessageDialog.value = true
        }
    )
})

onUnmounted(() => {
    eventBus.off(ApplicationEventTypes.TOPIC_DELETED, afterTopicDeletion)
    eventBus.off(ApplicationEventTypes.TOPIC_EDITED, afterTopicEdit)
})
</script>
<style scoped>
.action-menu div {
    display: none;
}

.action-menu:hover div,
.action-menu button:focus + div {
    display: block;
}

.action-menu:hover button {
    @apply bg-green-100 dark:border-gray-300 dark:bg-gray-600 dark:text-gray-100 dark:shadow-black;
}
</style>
