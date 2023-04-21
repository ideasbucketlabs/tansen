<template>
    <div
        class="flex h-56 flex-col rounded border-2 transition-all duration-200 ease-linear"
        :class="[isDataSelected ? 'border-blue-500 shadow-lg' : 'border-transparent']"
    >
        <div class="flex justify-between border-b border-green-100 p-0 dark:border-gray-500">
            <ul class="flex h-8 items-center">
                <li
                    @click="activateTab(0)"
                    :class="[
                        currentIndex === 0
                            ? 'cursor-default after:w-full after:bg-green-500 dark:after:bg-gray-200'
                            : 'cursor-pointer after:w-1/3 hover:after:bg-green-300 dark:hover:after:bg-gray-400',
                    ]"
                    class="relative flex h-8 w-16 items-end justify-center py-1 text-center outline-none after:absolute after:bottom-0 after:mx-auto after:block after:h-0.5 after:transition-all after:duration-200 after:ease-linear"
                >
                    <span class="block">Data</span>
                </li>
                <li
                    @click="activateTab(1)"
                    :class="[
                        currentIndex === 1
                            ? 'cursor-default after:w-full after:bg-green-500 dark:after:bg-gray-200'
                            : 'cursor-pointer after:w-1/3 hover:after:bg-green-300 dark:hover:after:bg-gray-400',
                    ]"
                    class="relative flex h-8 w-14 items-end justify-center py-1 text-center outline-none after:absolute after:bottom-0 after:mx-auto after:block after:h-0.5 after:transition-all after:duration-200 after:ease-linear"
                >
                    <span class="">Key</span>
                </li>
                <li
                    @click="activateTab(2)"
                    :class="[
                        currentIndex === 2
                            ? 'cursor-default after:w-full after:bg-green-500 dark:after:bg-gray-200'
                            : 'cursor-pointer after:w-1/3 hover:after:bg-green-300 dark:hover:after:bg-gray-400',
                    ]"
                    class="relative flex h-8 w-24 items-end justify-center py-1 text-center outline-none after:absolute after:bottom-0 after:mx-auto after:block after:h-0.5 after:transition-all after:duration-200 after:ease-linear"
                >
                    <span class="">Header(s)</span>
                </li>
                <li
                    @click="activateTab(3)"
                    :class="[
                        currentIndex === 3
                            ? 'cursor-default after:w-full after:bg-green-500 dark:after:bg-gray-200'
                            : 'cursor-pointer after:w-1/3 hover:after:bg-green-300 dark:hover:after:bg-gray-400',
                    ]"
                    class="relative flex h-8 w-24 items-end justify-center py-1 text-center outline-none after:absolute after:bottom-0 after:mx-auto after:block after:h-0.5 after:transition-all after:duration-200 after:ease-linear"
                >
                    <span class="">Metadata</span>
                </li>
            </ul>
            <div class="flex items-center">
                <label class="flex cursor-pointer items-center">
                    <input class="mr-2" type="checkbox" :checked="selected" @click="emitSelection" />
                    <span class="mr-2 hidden md:block">Select</span>
                </label>
            </div>
        </div>
        <div
            class="flex flex-1 flex-col overflow-auto rounded-b rounded-tr bg-white shadow dark:bg-gray-700"
            v-if="currentIndex === 0"
        >
            <div
                class="flex items-center border-b border-green-100 bg-green-50 p-2 dark:border-gray-500 dark:bg-gray-600"
            >
                <button
                    type="button"
                    @click="handleExpand(computedValueData as String | Object, data.partition, data.offset)"
                    class="relative overflow-hidden rounded border border-green-500 bg-green-100 px-2 text-sm text-green-500 transition duration-200 ease-linear hover:shadow-lg dark:border-gray-900 dark:bg-gray-700 dark:text-gray-100"
                >
                    <ripple></ripple>
                    Expand
                </button>
            </div>
            <div class="relative flex flex-1 flex-nowrap overflow-auto whitespace-nowrap">
                <pre class="absolute flex-1 px-4 py-2">{{ computedValueData }}</pre>
            </div>
        </div>
        <div class="flex-1 overflow-auto rounded-b bg-white dark:bg-gray-700" v-else-if="currentIndex === 1">
            <div
                class="flex items-center border-b border-green-100 bg-green-50 p-2 dark:border-gray-500 dark:bg-gray-600"
            >
                <button
                    type="button"
                    @click="handleExpand(computedKeyData as String | Object, data.partition, data.offset)"
                    class="relative overflow-hidden rounded border border-green-500 bg-green-100 px-2 text-sm text-green-500 transition duration-200 ease-linear hover:shadow-lg dark:border-gray-900 dark:bg-gray-700 dark:text-gray-100"
                >
                    <ripple></ripple>
                    Expand
                </button>
            </div>
            <pre class="flex-1 overflow-auto px-4 py-2">{{ computedKeyData }}</pre>
        </div>
        <div class="flex-1 overflow-auto rounded-b bg-white px-4 py-2 dark:bg-gray-700" v-else-if="currentIndex === 2">
            <pre>{{ data.headerData }}</pre>
        </div>
        <div class="flex-1 overflow-auto rounded-b bg-white px-4 py-2 dark:bg-gray-700" v-else>
            <div>
                <pre>Timestamp: {{ data.timestamp }}</pre>
            </div>
            <div>
                <pre>Timestamp type: {{ data.timestampType }}</pre>
            </div>
            <div>
                <pre>Offset: {{ data.offset }}</pre>
            </div>
            <div>
                <pre>Partition: {{ data.partition }}</pre>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { computed, defineEmits, defineProps, onMounted, onUnmounted, type PropType, ref, watch } from 'vue'
import type { TopicMessage } from '@/entity/TopicMessage'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import Ripple from '@/components/Ripple.vue'

const props = defineProps({
    selected: {
        type: Boolean as PropType<boolean>,
        default: false,
    },
    data: {
        type: Object as PropType<TopicMessage>,
        required: true,
    },
})

const emit = defineEmits<{
    (e: 'tabClicked', value: number): void
    (e: 'expandClicked', content: string, partition: number, offset: number): void
    (e: 'onSelect', selected: boolean, value: TopicMessage): void
}>()

const currentIndex = ref<number>(0)
const isDataSelected = ref<boolean>(props.selected || false)
const computedValueData = computed(() => {
    return (props.data?.valueSchemaFormat ?? null) === null
        ? prettifyContent(props.data.valueData as string)
        : props.data.valueData
})

const computedKeyData = computed(() => {
    return (props.data?.keySchemaFormat ?? null) === null
        ? prettifyContent(props.data.keyData as string)
        : props.data.keyData
})

function emitSelection() {
    const data = {
        headerData: props.data.headerData,
        valueData: props.data.valueData,
        keyData: props.data.keyData,
        timestamp: props.data.timestamp,
        timestampType: props.data.timestampType,
        partition: props.data.partition,
        valueSchemaFormat: props.data.valueSchemaFormat,
        keySchemaFormat: props.data.keySchemaFormat,
        offset: props.data.offset,
    }

    emit('onSelect', !isDataSelected.value, data)
}

function activateTab(tabIndex: number) {
    currentIndex.value = tabIndex
    emit('tabClicked', tabIndex)
}

function prettifyContent(input: string): string {
    try {
        const parsedContent = JSON.parse(input)
        if (parsedContent && typeof parsedContent === 'object') {
            return JSON.stringify(parsedContent, null, 4)
        }
        return input
    } catch (e) {
        return input
    }
}

function getKey(message: TopicMessage): string {
    return message.partition.toString(10) + message.offset.toString(10) + message.timestamp.toString(10)
}

function messageSelected(applicationEvent: ApplicationEvent) {
    if (getKey(props.data) === getKey(applicationEvent.data as TopicMessage)) {
        isDataSelected.value = true
    }
}

function messageUnselected(applicationEvent: ApplicationEvent) {
    if (getKey(props.data) === getKey(applicationEvent.data as TopicMessage)) {
        isDataSelected.value = false
    }
}

function handleExpand(input: string | object, partition: number, offset: number) {
    if (typeof input === 'object') {
        emit('expandClicked', JSON.stringify(input, null, 4) as string, partition, offset)
    } else {
        emit('expandClicked', input as string, partition, offset)
    }
}

onMounted(() => {
    eventBus.on(ApplicationEventTypes.MESSAGE_SELECTED, messageSelected)
    eventBus.on(ApplicationEventTypes.MESSAGE_UNSELECTED, messageUnselected)
})

onUnmounted(() => {
    eventBus.off(ApplicationEventTypes.MESSAGE_SELECTED, messageSelected)
    eventBus.off(ApplicationEventTypes.MESSAGE_UNSELECTED, messageUnselected)
})
</script>
