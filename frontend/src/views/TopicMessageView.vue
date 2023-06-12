<template>
    <div class="relative mt-4 flex flex-1 flex-col overflow-hidden text-gray-800 dark:text-gray-100">
        <CodeViewer
            v-if="showExpandedContent"
            :value="expandedContent"
            :partition="expandPartition"
            :offset="expandOffset"
            @close="showExpandedContent = false"
        ></CodeViewer>
        <div class="flex items-center justify-between pr-4">
            <div class="space-y-3 lg:flex lg:h-20 lg:items-start lg:space-x-2 lg:space-y-1">
                <div class="flex items-center">
                    <div
                        class="mt-1 flex items-center rounded border border-green-500 text-green-500 hover:shadow-lg dark:border-gray-500 dark:text-gray-500 dark:hover:shadow-gray-900"
                    >
                        <button
                            class="block h-9 w-9 rounded-l border-r border-green-500 transition duration-200 ease-linear dark:border-gray-700"
                            title="Continue to display messages"
                            @click="startStream"
                            type="button"
                            :class="[
                                !isStreamPaused
                                    ? 'bg-green-500 text-white shadow-inner shadow-green-900 dark:bg-gray-500 dark:shadow-gray-900'
                                    : 'border-green-500 bg-green-100 hover:bg-green-200 dark:border-gray-500 dark:bg-gray-100 dark:hover:bg-gray-300',
                            ]"
                        >
                            <PlayIcon class="h-full w-full"></PlayIcon>
                        </button>
                        <button
                            @click="doHardPause"
                            type="button"
                            class="block h-9 w-9 rounded-r transition duration-200 ease-linear"
                            :class="[
                                isStreamPaused
                                    ? 'bg-green-500 text-white shadow-inner shadow-green-900 dark:bg-gray-500 dark:shadow-gray-900'
                                    : 'border-green-500 bg-green-100 hover:bg-green-200 dark:border-gray-500 dark:bg-gray-100 dark:hover:bg-gray-300',
                            ]"
                            title="Stop messages"
                        >
                            <PauseIcon class="h-full w-full"></PauseIcon>
                        </button>
                    </div>
                </div>
                <form @submit.prevent="onFilterSubmission" class="space-y-2 md:flex md:space-x-2 md:space-y-0">
                    <div class="action-menu w-auto" tabindex="-1">
                        <button
                            class="relative h-10 flex-1 truncate rounded border border-green-500 bg-green-50 px-2 dark:border-gray-400 dark:bg-gray-600"
                            type="button"
                        >
                            Deserialization Option
                        </button>
                        <div class="absolute z-10 flex w-52">
                            <div class="mt-2 flex space-y-2 rounded bg-white p-2 shadow-lg dark:bg-gray-600">
                                <div class="flex flex-1">
                                    <div>Key Deserialization</div>
                                    <div class="flex">
                                        <label
                                            class="inline-flex w-6/12 cursor-pointer items-center"
                                            title="This option uses schema registry for deserialization if configured otherwise will fallback to `string`."
                                        >
                                            <input
                                                type="radio"
                                                name="keyDeserialization"
                                                class="cursor-pointer"
                                                v-model="formData.keyDeserialization"
                                                value="auto"
                                            />
                                            <span class="relative ml-2 flex cursor-pointer items-center">
                                                <span>Auto</span>
                                                <span
                                                    class="-mt-2 ml-1 flex rounded-full border border-blue-500 text-blue-500"
                                                >
                                                    <InformationIcon class="w-3 fill-current"></InformationIcon></span
                                            ></span>
                                        </label>
                                        <label class="inline-flex w-6/12 cursor-pointer items-center">
                                            <input
                                                type="radio"
                                                v-model="formData.keyDeserialization"
                                                name="keyDeserialization"
                                                class="cursor-pointer"
                                                value="string"
                                            />
                                            <span class="ml-2 cursor-pointer">String</span>
                                        </label>
                                    </div>
                                </div>
                                <hr />
                                <div class="flex flex-1">
                                    <div>Value Deserialization</div>
                                    <div class="flex">
                                        <label
                                            class="inline-flex w-6/12 cursor-pointer items-center"
                                            title="This option uses schema registry for deserialization if configured otherwise will fallback to `string`."
                                        >
                                            <input
                                                type="radio"
                                                name="valueDeserialization"
                                                class="cursor-pointer"
                                                v-model="formData.valueDeserialization"
                                                value="auto"
                                            />
                                            <span class="relative ml-2 flex cursor-pointer items-center">
                                                <span>Auto</span>
                                                <span
                                                    class="-mt-2 ml-1 flex rounded-full border border-blue-500 text-blue-500"
                                                >
                                                    <InformationIcon class="w-3 fill-current"></InformationIcon></span
                                            ></span>
                                        </label>
                                        <label class="inline-flex w-6/12 cursor-pointer items-center">
                                            <input
                                                type="radio"
                                                name="valueDeserialization"
                                                v-model="formData.valueDeserialization"
                                                value="string"
                                                class="cursor-pointer"
                                            />
                                            <span class="ml-2 cursor-pointer">String</span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <BaseSelectField
                        label="Offset"
                        :hide-label="true"
                        class="w-48"
                        size="custom"
                        input-class="h-10 py-0"
                        v-model="formData.criteria"
                        :options="options"
                    ></BaseSelectField>
                    <BaseInputField
                        v-if="formData.criteria === 'offset'"
                        label="Filter"
                        class="w-32"
                        :hide-label="true"
                        size="small"
                        input-class="h-10"
                        v-model.number="formData.offset"
                        placeholder="Offset"
                        ><div class="pt-1 text-xs">Must be positive integer value.</div></BaseInputField
                    >
                    <DateTimeInputField
                        v-if="formData.criteria === 'time'"
                        label="Time"
                        class="w-44"
                        :hide-label="true"
                        size="small"
                        input-class="h-10"
                        v-model="rawDatetime"
                    ></DateTimeInputField>
                    <div class="flex space-x-2">
                        <BaseSelectField
                            v-if="formData.criteria !== ''"
                            label="Offset"
                            size="small"
                            input-class="h-10"
                            :hide-label="true"
                            class="w-32"
                            v-model.number="formData.partition"
                            :options="partitions"
                        ></BaseSelectField>
                        <BaseButton
                            class="h-10 w-24"
                            label="Apply"
                            type="submit"
                            :disabled="!isFormDirty || !isFormValid"
                        ></BaseButton>
                    </div>
                </form>
            </div>
            <div class="hidden items-center space-x-2 xl:flex" v-if="numberOfMessageReceived !== 0">
                <div>No. of message received</div>
                <div class="text-2xl font-bold" style="text-shadow: 0 0 3px rgba(0, 0, 0, 0.45)">
                    {{ numberOfMessageReceived }}
                </div>
            </div>
        </div>
        <div class="flex-1 overflow-auto" v-if="dataLength === 0">
            <div class="mt-6 text-center">
                <div class="text-3xl">No new message</div>
                <div>The message browser shows messages that have arrived since this page was opened.</div>
            </div>
        </div>
        <div class="mb-6 flex flex-col shadow-lg" v-if="showMessageKeyDecodingError">
            <div class="flex rounded-t bg-red-500 dark:bg-red-700">
                <div class="p-2 text-white">Message key decoding Error</div>
            </div>
            <div class="flex items-center space-x-2 rounded-b bg-white p-2 dark:bg-gray-600">
                <div class="h-10 w-10">
                    <ErrorIcon class="h-full w-full fill-current text-red-500 dark:text-red-700"></ErrorIcon>
                </div>
                <div>
                    Even though topic use schema registry it seems like some of message `key` do not conform to the
                    schema defined. Please try again with
                    <pre class="inline">String</pre>
                    as deserialization option for key and try again.
                </div>
            </div>
        </div>
        <div class="mb-6 flex flex-col shadow-lg" v-if="showMessageValueDecodingError">
            <div class="flex rounded-t bg-red-500 dark:bg-red-700">
                <div class="p-2 text-white">Message value decoding Error</div>
            </div>
            <div class="flex items-center space-x-2 rounded-b bg-white p-2 dark:bg-gray-600">
                <div class="h-10 w-10">
                    <ErrorIcon class="h-full w-full fill-current text-red-500 dark:text-red-700"></ErrorIcon>
                </div>
                <div>
                    Even though topic use schema registry it seems like some of message `value` do not conform to the
                    schema defined. Please try again with
                    <pre class="inline">String</pre>
                    as deserialization option for value and try again.
                </div>
            </div>
        </div>
        <MessageVirtualScroll
            :item-count="dataLength"
            :rowHeight="224 + 16"
            class="flex-1 pr-4"
            v-if="dataLength !== 0"
        >
            <template v-slot:detail="{ items }">
                <message-tabs
                    v-for="d in sliceData(items.start, items.end)"
                    :key="getKey(d)"
                    class="mb-4"
                    @tabClicked="doHardPause"
                    @formatClicked="doHardPause"
                    @expand-clicked="expandClicked"
                    @onSelect="selectMessage"
                    :selected="isDataSelected(d)"
                    :data="d"
                ></message-tabs>
            </template>
        </MessageVirtualScroll>
        <transition name="fade">
            <div
                v-if="isAnyDataSelected"
                class="absolute bottom-0 flex w-full items-center justify-between rounded border border-green-200 bg-green-100 px-5 py-1 shadow dark:border-black dark:bg-gray-700 dark:shadow-black"
            >
                <p class="px-1">As JSON format</p>
                <BaseButton label="Download" class="w-32" @click="downloadSelectedMessages"></BaseButton>
            </div>
        </transition>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { computed, defineAsyncComponent, nextTick, onMounted, onUnmounted, type PropType, ref, watch } from 'vue'
import PlayIcon from '@/icons/PlayIcon.vue'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import BaseInputField from '@/components/BaseInputField.vue'
import BaseSelectField from '@/components/BaseSelectField.vue'
import BaseButton from '@/components/BaseButton.vue'
import { downloadFile } from '@/util/Util'
import type { TopicMessage } from '@/entity/TopicMessage'
import MessageTabs from '@/components/MessageTabs.vue'
import MessageVirtualScroll from '@/components/MessageVirtualScroll.vue'
import PauseIcon from '@/icons/PauseIcon.vue'
import { useRoute } from 'vue-router'
import StreamProcessor from '@/util/StreamProcessor'
import type { ErrorResponse } from '@/entity/ErrorResponse'
import InformationIcon from '@/icons/InformationIcon.vue'
import ErrorIcon from '@/icons/ErrorIcon.vue'
import AppComponentLoader from '@/components/AppComponentLoader.vue'
import DateTimeInputField from '@/components/DateTimeInputField.vue'

const clusterId = useRoute().params.clusterId as string
const streamProcessor = new StreamProcessor<TopicMessage>(processTopicMessage, handleError)
const isStreamPaused = ref(false)
const isFormDirty = ref<boolean>(false)
let data: TopicMessage[] = []
const CodeViewer = defineAsyncComponent({
    loader: () => import('@/components/CodeViewer.vue'),
    loadingComponent: AppComponentLoader,
    delay: 200,
})
const props = defineProps({
    topic: {
        type: String as PropType<string>,
        required: true,
    },
    numberOfPartition: {
        type: Number as PropType<number>,
        required: true,
    },
})
const dataLength = ref<number>(0)
const options = ref<{ value: 'offset' | '' | 'time'; label: string }[]>([
    { value: '', label: '' },
    { value: 'offset', label: 'Jump to offset' },
    { value: 'time', label: 'Jump to time' },
])
const rawDatetime = ref<Date | null>(null)
const formData = ref<{
    keyword: string
    criteria: '' | 'offset' | 'time'
    offset: number | null
    partition: number
    time: Date | null
    keyDeserialization: 'auto' | 'string'
    valueDeserialization: 'auto' | 'string'
}>({
    keyword: '',
    criteria: '',
    offset: null,
    partition: 0,
    time: null,
    keyDeserialization: 'auto',
    valueDeserialization: 'auto',
})

const tailingOffset = ref<number>(0)
const tailingTimestamp = ref<number>(0)
const isAnyDataSelected = ref<boolean>(false)
const hardPause = ref<boolean>(false)
let selectedData: Map<string, TopicMessage> = new Map()

const partitions = computed<{ value: number; label: string }[]>(() => {
    return Array.from(Array(props.numberOfPartition).keys()).map((it) => {
        return { value: it, label: 'Partition ' + it }
    })
})
const showMessageValueDecodingError = ref<boolean>(false)
const showMessageKeyDecodingError = ref<boolean>(false)
const numberOfMessageReceived = ref<number>(0)
const expandedContent = ref<string>('')
const showExpandedContent = ref<boolean>(false)
const expandPartition = ref<number>(0)
const expandOffset = ref<number>(0)

const isFormValid = computed<boolean>(() => {
    if (formData.value.criteria === '') {
        return true
    }

    if (formData.value.criteria === 'offset') {
        if (formData.value.offset === null) {
            return false
        }
        if (!/^[0-9]+$/.test(formData.value.offset.toString())) {
            return false
        }
    }

    if (formData.value.criteria === 'time') {
        if (formData.value.time === null) {
            return false
        }
        if (formData.value.partition === null) {
            return false
        }
    }

    return true
})

const isStreamPlayable = computed<boolean>(() => {
    return formData.value.criteria === '' ? true : isFormValid.value
})

const isSpecificOffsetBeingPeeked = computed<boolean>(() => {
    return isStreamPlayable.value && formData.value.criteria !== '' && formData.value.criteria === 'offset'
})

const isSpecificTimestampBeingPeeked = computed<boolean>(() => {
    return isStreamPlayable.value && formData.value.criteria !== '' && formData.value.criteria === 'time'
})

watch(
    formData,
    async () => {
        isFormDirty.value = true
        await pauseStream()
    },
    { deep: true, immediate: false }
)

watch(rawDatetime, async (newRawDatetime: Date | null) => {
    formData.value.time = newRawDatetime === null ? null : newRawDatetime
    // if (formData.value.time !== null && formData.value.time.getTime() > new Date().getTime()) {
    //     formData.value.time = null
    // }
})

function isDataSelected(message: TopicMessage): boolean {
    return selectedData.has(getKey(message))
}

function doHardPause() {
    hardPause.value = true
    pauseStream()
}

function downloadSelectedMessages() {
    downloadFile(JSON.stringify(Array.from(selectedData.values())), 'value', 'JSON')
}

function processTopicMessage(topicMessage: TopicMessage) {
    data.unshift(topicMessage)
    numberOfMessageReceived.value += 1
    if (topicMessage.partition === -1 && topicMessage.offset === -1) {
        // Error returned from server during message tailing.
        pauseStream()
        if (topicMessage.valueData === 'Unknown magic byte!') {
            showMessageValueDecodingError.value = true
        }
        if (topicMessage.keyData === 'Unknown magic byte!') {
            showMessageKeyDecodingError.value = true
        }
    } else {
        tailingOffset.value = topicMessage.offset
        tailingTimestamp.value = topicMessage.timestamp
    }
    // Only keep 600 message in memory. TODO make this limit dynamic based on message size.
    if (dataLength.value === 600) {
        data.pop()
    } else {
        dataLength.value += 1
    }
}

async function handleError(errorResponse: ErrorResponse) {
    if (errorResponse.httpCode === 0) {
        try {
            await startStream()
        } catch (error) {
            if ((error as Error).name !== 'StreamLogicError') {
                //
            }
        }
    }
}

async function startStream() {
    if (!isStreamPlayable.value || !isStreamPaused.value || !hardPause.value) {
        return
    }
    hardPause.value = false

    if (isSpecificOffsetBeingPeeked.value) {
        const urlSearchParams = new URLSearchParams()
        urlSearchParams.set(
            'parameters',
            JSON.stringify({ offset: tailingOffset.value + 1, partition: formData.value.partition })
        )

        const url = `${clusterId}/messages/${formData.value.keyDeserialization}/${
            formData.value.valueDeserialization
        }/${props.topic}?${urlSearchParams.toString()}`
        isStreamPaused.value = false
        await streamProcessor.start(url)
    } else if (isSpecificTimestampBeingPeeked.value) {
        const urlSearchParams = new URLSearchParams()
        urlSearchParams.set(
            'parameters',
            JSON.stringify({
                timestamp: new Date(tailingTimestamp.value + 1).toISOString(),
                partition: formData.value.partition,
            })
        )

        const url = `${clusterId}/messages/${formData.value.keyDeserialization}/${
            formData.value.valueDeserialization
        }/${props.topic}?${urlSearchParams.toString()}`
        isStreamPaused.value = false
        await streamProcessor.start(url)
    } else {
        isStreamPaused.value = false
        await streamProcessor.start(
            // eslint-disable-next-line vue/max-len
            `${clusterId}/messages/${formData.value.keyDeserialization}/${formData.value.valueDeserialization}/${props.topic}`
        )
    }
}

async function pauseStream() {
    if (isStreamPaused.value) {
        return
    }
    await streamProcessor.stop()
    isStreamPaused.value = true
}

function sliceData(start: number, end: number): TopicMessage[] {
    return data.slice(start, end)
}

function selectMessage(selected: boolean, message: TopicMessage): void {
    if (selected) {
        selectedData.set(getKey(message), message)
        eventBus.emit(ApplicationEventTypes.MESSAGE_SELECTED, true, message)
        isAnyDataSelected.value = true
    } else {
        selectedData.delete(getKey(message))
        isAnyDataSelected.value = selectedData.size !== 0
        eventBus.emit(ApplicationEventTypes.MESSAGE_UNSELECTED, true, message)
    }
}

function getKey(message: TopicMessage): string {
    return message.partition.toString(10) + message.offset.toString(10) + message.timestamp.toString(10)
}

function onFilterSubmission() {
    showMessageValueDecodingError.value = false
    showMessageKeyDecodingError.value = false
    pauseStream()
    let parameter = ''

    if (formData.value.criteria === 'offset') {
        const urlSearchParams = new URLSearchParams()
        tailingOffset.value = formData.value.offset ?? 0
        urlSearchParams.set(
            'parameters',
            JSON.stringify({ offset: formData.value.offset, partition: formData.value.partition })
        )

        parameter = urlSearchParams.toString()
    } else if (formData.value.criteria === 'time') {
        const urlSearchParams = new URLSearchParams()
        urlSearchParams.set(
            'parameters',
            JSON.stringify({ timestamp: formData?.value?.time?.toISOString(), partition: formData.value.partition })
        )
        tailingTimestamp.value = formData?.value?.time?.valueOf() ?? 0
        parameter = urlSearchParams.toString()
    }

    data = []
    dataLength.value = 0
    const url = parameter === '' ? `${props.topic}` : `${props.topic}?${parameter}`
    numberOfMessageReceived.value = 0
    hardPause.value = false
    streamProcessor.start(
        `${clusterId}/messages/${formData.value.keyDeserialization}/${formData.value.valueDeserialization}/${url}`
    )
    selectedData = new Map()
    isAnyDataSelected.value = false
    isStreamPaused.value = false
    isFormDirty.value = false
}

function pauseStreamIfNotSeeked() {
    if (isStreamPlayable.value && (isSpecificOffsetBeingPeeked.value || isSpecificTimestampBeingPeeked.value)) {
        pauseStream()
    }
}

function startStreamIfNotPaused() {
    if (isStreamPlayable.value && !hardPause.value && isStreamPaused.value) {
        startStream()
    }
}

function expandClicked(content: string, partition: number, offset: number) {
    pauseStream()
    hardPause.value = true
    expandedContent.value = content
    expandPartition.value = partition
    expandOffset.value = offset
    showExpandedContent.value = true
}

onMounted(() => {
    nextTick().then(() => {
        streamProcessor.start(
            // eslint-disable-next-line vue/max-len
            `${clusterId}/messages/${formData.value.keyDeserialization}/${formData.value.valueDeserialization}/${props.topic}`
        )
        eventBus.on(ApplicationEventTypes.APPLICATION_ACTIVATED, startStreamIfNotPaused)
        eventBus.on(ApplicationEventTypes.APPLICATION_DEACTIVATED, pauseStreamIfNotSeeked)
    })
})

onUnmounted(() => {
    pauseStream()
    eventBus.off(ApplicationEventTypes.APPLICATION_ACTIVATED, startStreamIfNotPaused)
    eventBus.off(ApplicationEventTypes.APPLICATION_DEACTIVATED, pauseStreamIfNotSeeked)
    data = []
    dataLength.value = 0
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
    @apply bg-green-100 shadow-inner dark:border-gray-400 dark:bg-gray-700 dark:text-gray-100 dark:shadow-none dark:shadow-black;
}
</style>
