<template>
    <div
        class="z-10 flex flex-col rounded-b border-b border-l border-r border-green-300 bg-green-100 p-2 shadow-lg dark:border-gray-800 dark:bg-gray-700"
        v-click-outside="clickedOutsideComponent"
        ref="root"
    >
        <form action="#" @submit.prevent="submitFilter" novalidate v-on:keyup.enter="submitFilter">
            <div class="mb-2 flex">
                <label class="inline-flex cursor-pointer items-center transition-all duration-500">
                    <input type="radio" class="h-4 w-4 text-green-500" v-model="input" :value="true" />
                    <span class="ml-2 font-normal text-green-500 dark:text-gray-100">{{
                        configuration.trueLabel
                    }}</span>
                </label>
            </div>
            <div class="mb-2 flex">
                <label class="inline-flex cursor-pointer items-center transition-all duration-500">
                    <input type="radio" class="h-4 w-4 text-green-500" v-model="input" :value="false" />
                    <span class="ml-2 font-normal text-green-500 dark:text-gray-100">{{
                        configuration.falseLabel
                    }}</span>
                </label>
            </div>
            <div class="my-2 flex content-center items-center">
                <button
                    class="mx-4 h-8 flex-1 rounded border border-green-500 text-green-500 transition duration-200 hover:shadow-lg dark:border-gray-100 dark:text-gray-100"
                    @click="resetFilter"
                    type="button"
                >
                    Reset
                </button>
                <BaseButton class="mx-4 h-8 flex-1" type="submit" @click="submitFilter" label="Apply">Apply</BaseButton>
            </div>
        </form>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import vClickOutside from '@/directives/ClickOutside'
import BaseButton from '@/components/BaseButton.vue'
import { onMounted, onUnmounted, type PropType, ref } from 'vue'
import type { Filter } from '@/entity/Filter'

const props = defineProps({
    trigger: {
        type: String as PropType<string>,
        required: true,
        default: '',
    },
    field: {
        type: String,
        required: true,
    },
    modelValue: {
        type: Object as PropType<Filter | null | undefined>,
    },
    configuration: {
        type: Object as PropType<Record<'trueLabel' | 'falseLabel', string>>,
        default() {
            return {
                trueLabel: 'True',
                falseLabel: 'False',
            } as Record<'trueLabel' | 'falseLabel', string>
        },
    },
})

const emit = defineEmits<{
    (e: 'update:modelValue', value: Filter | null): void
    (e: 'mounted', value: Element | null): void
    (e: 'destroyed'): void
    (e: 'hide'): void
}>()

const input = ref<boolean>(
    props.modelValue === undefined || props.modelValue === null ? true : (props.modelValue?.value as boolean)
)
const root = ref<Element | null>(null)

function submitFilter() {
    emit('update:modelValue', {
        type: 'boolean',
        operator: 'eq',
        value: input.value,
        property: props.field,
    })

    emit('hide')
}

function resetFilter() {
    input.value = true
    emit('update:modelValue', null)
    emit('hide')
}

function clickedOutsideComponent(event: MouseEvent) {
    const triggerValue = (event.target as Element).getAttribute('data-trigger') ?? ''
    // If trigger element is clicked then nothing to do.
    if (triggerValue === props.trigger) {
        return
    }

    emit('hide')
}

onMounted(() => {
    emit('mounted', root.value)
})

onUnmounted(() => {
    emit('destroyed')
})
</script>
