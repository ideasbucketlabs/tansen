<template>
    <div class="relative mt-4 flex flex-1 flex-col overflow-hidden text-gray-800 dark:text-gray-100">
        <div class="space-y-3 md:flex md:h-20 md:items-start md:space-x-2 md:space-y-1">
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
                        @click="pauseStream"
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
            <form @submit.prevent="onFilterSubmission" class="space-y-2 md:flex md:space-y-0 md:space-x-2">
                <BaseSelectField
                    label="Offset"
                    :hide-label="true"
                    class="w-36"
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
                    v-model="formData.offset"
                    placeholder="Offset"
                    ><div class="pt-1 text-xs">Must be positive integer value.</div></BaseInputField
                >
                <BaseInputField
                    v-if="formData.criteria === 'time'"
                    label="Time"
                    class="w-[19rem]"
                    :hide-label="true"
                    size="small"
                    input-class="h-10"
                    v-model="rawDatetime"
                    placeholder="Time must be in (mm/dd/YYYY HH:mm:ss)"
                    ><div class="pt-1 text-xs">
                        Must be in (<span class="font-bold italic">mm/dd/YYYY HH:mm:ss</span>) like
                        <span class="font-bold italic">02/22/2002 16:17:18</span>
                        format and <span class="font-bold italic">cannot be future date</span>.
                    </div></BaseInputField
                >
                <div class="flex space-x-2">
                    <BaseSelectField
                        v-if="formData.criteria !== ''"
                        label="Offset"
                        size="small"
                        input-class="h-10"
                        :hide-label="true"
                        class="w-32"
                        v-model="formData.partition"
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
            <div class="flex items-center space-x-4 pt-2">
                <div>
                    <label class="inline-flex items-center">
                        <input class="form-radio" type="radio" name="position" v-model="position" value="latest" />
                        <span class="ml-1">Latest first</span>
                    </label>
                </div>
                <div>
                    <label class="inline-flex items-center">
                        <input class="form-radio" type="radio" name="position" v-model="position" value="earliest" />
                        <span class="ml-1">Latest last</span>
                    </label>
                </div>
            </div>
        </div>
        <div class="flex-1 overflow-auto" v-if="dataLength === 0">
            <div class="mt-6 text-center">
                <div class="text-3xl">No new message</div>
                <div>The message browser shows messages that have arrived since this page was opened.</div>
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
                    :key="getKey(d) + position"
                    class="mb-4"
                    @tabClicked="pauseStream"
                    @formatClicked="pauseStream"
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
import { defineProps, nextTick, onMounted, onUnmounted, type PropType, ref, watch, computed } from 'vue'
import PlayIcon from '@/icons/PlayIcon.vue'
import { getEventSource } from '@/util/HttpService'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import BaseInputField from '@/components/BaseInputField.vue'
import BaseSelectField from '@/components/BaseSelectField.vue'
import BaseButton from '@/components/BaseButton.vue'
import { downloadFile, parseDate } from '@/util/Util'
import type { TopicMessage } from '@/entity/TopicMessage'
import MessageTabs from '@/components/MessageTabs.vue'
import MessageVirtualScroll from '@/components/MessageVirtualScroll.vue'
import PauseIcon from '@/icons/PauseIcon.vue'
import { useRoute } from 'vue-router'

const clusterId = useRoute().params.clusterId as string
let eventSource: EventSource
const isStreamPaused = ref(false)
const isFormDirty = ref<boolean>(false)
let data: TopicMessage[] = []
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
const rawDatetime = ref<string>('')
const formData = ref<{
    keyword: string
    criteria: '' | 'offset' | 'time'
    offset: number | null
    partition: number
    time: Date | null
}>({
    keyword: '',
    criteria: '',
    offset: null,
    partition: 0,
    time: null,
})

const tailingOffset = ref<number>(0)
const tailingTimestamp = ref<number>(0)
const isAnyDataSelected = ref<boolean>(false)
const position = ref<string>('latest')
let selectedData: Map<string, TopicMessage> = new Map()

const partitions = computed<{ value: number; label: string }[]>(() => {
    return Array.from(Array(props.numberOfPartition).keys()).map((it) => {
        return { value: it, label: 'Partition ' + it }
    })
})

const isFormValid = computed<boolean>(() => {
    if (formData.value.criteria === '') {
        return false
    }
    if (formData.value.criteria === 'offset') {
        if (formData.value.offset === null) {
            return false
        }
        if (!/^[0-9]+$/.test(formData.value.offset.toString())) {
            return false
        }
        if (parseInt(formData.value.offset.toString(), 10) > 200000) {
            return false
        }
    }

    if (formData.value.criteria === 'time') {
        if (formData.value.time === null) {
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
        pauseStream()
    },
    { deep: true, immediate: false }
)

watch(rawDatetime, async (newRawDatetime: string | null) => {
    formData.value.time = newRawDatetime === null ? null : parseDate(newRawDatetime)
    if (formData.value.time !== null && formData.value.time.getTime() > new Date().getTime()) {
        formData.value.time = null
    }
})

watch(position, () => {
    data = data.reverse()
})

function isDataSelected(message: TopicMessage): boolean {
    return selectedData.has(getKey(message))
}

function downloadSelectedMessages() {
    downloadFile(JSON.stringify(Array.from(selectedData.values())), 'value.json', 'JSON')
}

function processMessage(event: MessageEvent) {
    const parsedData = JSON.parse(event.data) as TopicMessage
    if (position.value === 'latest') {
        data.unshift(parsedData)
    } else {
        data.push(parsedData)
    }

    if (parsedData.partition === -1 && parsedData.offset === -1) {
        // Error returned from server during message tailing.
        pauseStream()
    } else {
        tailingOffset.value = parsedData.offset
        tailingTimestamp.value = parsedData.timestamp
    }
    // Only keep 400 message in memory. TODO make this limit dynamic based on message size.
    if (dataLength.value === 400) {
        data.pop()
    } else {
        dataLength.value += 1
    }
}

function startStream() {
    if (!isStreamPlayable.value) {
        return
    }
    if (isSpecificOffsetBeingPeeked.value) {
        const urlSearchParams = new URLSearchParams()
        urlSearchParams.set(
            'parameters',
            JSON.stringify({ offset: tailingOffset.value + 1, partition: formData.value.partition })
        )

        const url = `${clusterId}/messages/${props.topic}?${urlSearchParams.toString()}`
        eventSource = getEventSource(url)
        eventSource.addEventListener('message', processMessage)
        eventSource.addEventListener('error', processError)
        isStreamPaused.value = false
    } else if (isSpecificTimestampBeingPeeked.value) {
        const urlSearchParams = new URLSearchParams()
        urlSearchParams.set(
            'parameters',
            JSON.stringify({ timestamp: new Date(tailingTimestamp.value + 1).toISOString() })
        )

        const url = `${clusterId}/messages/${props.topic}?${urlSearchParams.toString()}`
        eventSource = getEventSource(url)
        eventSource.addEventListener('message', processMessage)
        isStreamPaused.value = false
    } else {
        eventSource = getEventSource(`messages/${props.topic}`)
        eventSource.addEventListener('message', processMessage)
        isStreamPaused.value = false
    }
}

function processError(event: Event) {
    try {
        pauseStream()
    } catch (e) {
        // Show error message
    }
}

function pauseStream() {
    if (isStreamPaused.value) {
        return
    }
    eventSource.removeEventListener('message', processMessage)
    eventSource.removeEventListener('error', processError)
    eventSource.close()
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
        urlSearchParams.set('parameters', JSON.stringify({ timestamp: formData?.value?.time?.toISOString() }))
        tailingTimestamp.value = formData?.value?.time?.valueOf() ?? 0
        parameter = urlSearchParams.toString()
    }

    data = []
    dataLength.value = 0
    const url =
        parameter === '' ? `${clusterId}/messages/${props.topic}` : `${clusterId}/messages/${props.topic}?${parameter}`
    eventSource = getEventSource(url)
    eventSource.addEventListener('message', processMessage)
    selectedData = new Map()
    isAnyDataSelected.value = false
    isStreamPaused.value = false
    isFormDirty.value = false
}

onMounted(() => {
    eventSource = getEventSource(`${clusterId}/messages/${props.topic}`)
    eventSource.addEventListener('message', processMessage)
    eventSource.addEventListener('error', processError)
    nextTick(() => {
        eventBus.on(ApplicationEventTypes.APPLICATION_ACTIVATED, startStream)
        eventBus.on(ApplicationEventTypes.APPLICATION_DEACTIVATED, pauseStream)
    })
})

onUnmounted(() => {
    pauseStream()
    eventBus.off(ApplicationEventTypes.APPLICATION_ACTIVATED, startStream)
    eventBus.off(ApplicationEventTypes.APPLICATION_DEACTIVATED, pauseStream)
})
</script>
