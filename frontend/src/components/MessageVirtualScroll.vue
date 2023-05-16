<template>
    <div class="overflow-y-auto" ref="componentEl" @scroll="onScroll">
        <div class="relative w-full overflow-hidden" :style="viewportStyle">
            <div :style="spacerStyle">
                <slot name="detail" v-bind:items="visibleItems" />
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
import { nextTick, onBeforeUnmount, onMounted, type PropType, ref, computed } from 'vue'

const props = defineProps({
    tag: {
        type: String as PropType<string>,
        required: false,
        default: 'div',
    },
    itemCount: {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        type: Number as PropType<number>,
        required: true,
    },
    rowHeight: {
        type: Number as PropType<number>,
        default: 30,
        required: false,
    },
    keep: {
        type: Number as PropType<number>,
        default: 1,
        required: false,
    },
})

const scrollTop = ref<number>(0)
let requestId: number | null = null
const renderAhead = ref<number>(props.keep)
const componentHeight = ref<number>(400)
const componentEl = ref<Element | null>(null)

const totalHeight = computed(() => {
    return rowCount.value ? getChildPosition(rowCount.value - 1) + props.rowHeight : 0
})

const firstVisibleNode = computed(() => {
    return findStartNode()
})

const startNode = computed(() => {
    return Math.max(0, firstVisibleNode.value - renderAhead.value)
})

const lastVisibleNode = computed(() => {
    return findEndNode()
})

const endNode = computed(() => {
    return Math.min(rowCount.value - 1, lastVisibleNode.value + renderAhead.value)
})

const visibleNodeCount = computed(() => {
    return endNode.value - startNode.value + 1
})

const offsetY = computed(() => {
    return getChildPosition(startNode.value)
})

const visibleItems = computed<{ start: number; end: number }>(() => {
    return { start: startNode.value, end: startNode.value + visibleNodeCount.value }
})

const rowCount = computed<number>(() => {
    return props.itemCount
})

const spacerStyle = computed(() => {
    return {
        transform: `translateY(${offsetY.value}px)`,
    }
})

const viewportStyle = computed(() => {
    return {
        height: `${totalHeight.value + 4}px`,
    }
})

const windowResizeObserver = new ResizeObserver(() => {
    componentHeight.value = componentEl.value?.clientHeight ?? 100
})

function getChildPosition(index: number) {
    return index * props.rowHeight
}

function onScroll() {
    if (requestId !== null) {
        window.cancelAnimationFrame(requestId)
    }

    if (componentEl.value !== null) {
        requestId = window.requestAnimationFrame(() => {
            scrollTop.value = componentEl.value?.scrollTop ?? 0
        })
    }
}

function findStartNode(): number {
    let startRange = 0
    let endRange = rowCount.value ? rowCount.value - 1 : rowCount.value

    while (endRange !== startRange) {
        const middle = Math.floor((endRange - startRange) / 2 + startRange)

        if (getChildPosition(middle) <= scrollTop.value && getChildPosition(middle + 1) > scrollTop.value) {
            return middle
        }

        if (middle === startRange) {
            // edge case - start and end range are consecutive
            return endRange
        }

        if (getChildPosition(middle) <= scrollTop.value) {
            startRange = middle
        } else {
            endRange = middle
        }
    }

    return rowCount.value
}

function findEndNode() {
    let endNode
    for (endNode = firstVisibleNode.value; endNode < rowCount.value; endNode += 1) {
        if (getChildPosition(endNode) > getChildPosition(firstVisibleNode.value) + componentHeight.value) {
            return endNode
        }
    }

    return endNode
}

onMounted(() => {
    nextTick().then(() => {
        componentHeight.value = componentEl.value?.clientHeight ?? 100
        windowResizeObserver.observe(document.body)
    })
})

onBeforeUnmount(() => {
    if (requestId !== null) {
        window.cancelAnimationFrame(requestId)
    }
    windowResizeObserver.unobserve(document.body)
    windowResizeObserver.disconnect()
})
</script>
