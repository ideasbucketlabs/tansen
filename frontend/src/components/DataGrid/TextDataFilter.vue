<template>
    <div
        class="z-10 flex flex-col rounded-b border-b border-l border-r border-green-300 bg-green-100 p-2 shadow-lg dark:border-gray-800 dark:bg-gray-700"
        v-click-outside="clickedOutsideComponent"
        ref="root"
    >
        <form action="#" @submit.prevent="submitFilter" novalidate>
            <div class="mb-2">
                <base-select-field
                    label="Criteria"
                    :hide-label="true"
                    v-model="filterType"
                    input-class="flex-1"
                    :options="options"
                ></base-select-field>
            </div>
            <div class="mb-2">
                <BaseInputField label="Value" :hide-label="true" v-model="input"></BaseInputField>
            </div>
            <div class="my-2 flex content-center items-center">
                <button
                    class="mx-4 h-8 flex-1 rounded border border-green-500 text-green-500 transition duration-200 hover:shadow-lg dark:border-gray-100 dark:text-gray-100"
                    type="button"
                    @click="resetFilter"
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
import BaseSelectField from '@/components/BaseSelectField.vue'
import BaseInputField from '@/components/BaseInputField.vue'
import BaseButton from '@/components/BaseButton.vue'
import { defineEmits, defineProps, onMounted, onUnmounted, ref } from 'vue'
import type { Filter } from '@/entity/Filter'

interface OptionsInterface {
    value: 'ct' | 'sw' | 'ew' | 'eq'
    label: string
}

const props = defineProps<{ trigger: string; modelValue: Filter | null | undefined; field: string }>()
const emit = defineEmits<{
    (e: 'update:modelValue', value: Filter | null): void
    (e: 'mounted', value: Element | null): void
    (e: 'destroyed'): void
    (e: 'hide'): void
}>()
const root = ref<Element | null>(null)
const options = ref<OptionsInterface[]>([
    { value: 'ct', label: 'Contains' },
    { value: 'sw', label: 'Starts with' },
    { value: 'ew', label: 'Ends with' },
    { value: 'eq', label: 'Equals' },
])
const filterType = ref<'ct' | 'sw' | 'ew' | 'eq'>(
    props.modelValue === undefined || props.modelValue === null
        ? 'eq'
        : (props.modelValue?.operator as 'ct' | 'sw' | 'ew' | 'eq')
)
const input = ref<string | null>(
    props.modelValue === undefined || props.modelValue === null ? null : (props.modelValue?.value as string).slice()
)

function submitFilter() {
    if (input.value === null || input.value.trim().length === 0) {
        resetFilter()
    } else {
        emit('update:modelValue', {
            type: 'string',
            operator: filterType.value,
            value: input.value,
            property: props.field,
        })

        emit('hide')
    }
}

function resetFilter() {
    filterType.value = 'eq'
    input.value = null
    emit('update:modelValue', null)
    emit('hide')
}

function clickedOutsideComponent(event: Event) {
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
