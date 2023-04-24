<template>
    <div
        class="z-10 flex flex-col rounded-b border-b border-l border-r border-green-300 bg-green-100 p-2 shadow-lg dark:border-gray-800 dark:bg-gray-700"
        v-click-outside="clickedOutsideComponent"
        ref="root"
    >
        <form action="#" @submit.prevent="submitFilter" novalidate>
            <div class="mb-2">
                <BaseSelectField
                    label="Criteria"
                    :hide-label="true"
                    v-model="filterType"
                    input-class="flex-1"
                    :options="options"
                ></BaseSelectField>
            </div>
            <div class="mb-2">
                <BaseInputField label="Value" :hide-label="true" v-model.number="input">
                    <div class="form-error text-sm normal-case text-red-500" v-if="errors">
                        {{ errors['input'] }}
                    </div>
                </BaseInputField>
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
import { computed, defineEmits, defineProps, onMounted, onUnmounted, ref, watch } from 'vue'
import type { Filter } from '@/entity/Filter'
import { isInteger } from '@/util/Util'

interface OptionsInterface {
    value: 'eq' | 'gt' | 'lt' | 'lte' | 'gte'
    label: string
}

const props = defineProps<{ trigger: string; modelValue: Filter | null | undefined; field: string }>()
const emit = defineEmits<{
    (e: 'update:modelValue', value: Filter | null): void
    (e: 'mounted', value: Element | null): void
    (e: 'destroyed'): void
    (e: 'hide'): void
}>()

const options = ref<OptionsInterface[]>([
    { value: 'eq', label: 'Equal to' },
    { value: 'gt', label: 'Greater than' },
    { value: 'gte', label: 'Greater than or equal to' },
    { value: 'lt', label: 'Less than' },
    { value: 'lte', label: 'Less than or equal to' },
])
const root = ref<Element | null>(null)
const isDirty = ref<boolean>(false)
const filterType = ref<'eq' | 'gt' | 'lt' | 'lte' | 'gte'>(
    props.modelValue === undefined || props.modelValue === null
        ? 'eq'
        : (props.modelValue?.operator as 'eq' | 'gt' | 'lt' | 'lte' | 'gte')
)
const input = ref<number | null>(
    props.modelValue === undefined || props.modelValue === null ? null : (props.modelValue?.value as number)
)
const errors = ref<Record<'input', string> | null>(null)

const haveErrors = computed(() => {
    return errors.value !== null
})

watch(input, () => {
    isDirty.value = true
})

function validate(): Record<'input', string> | null {
    if (input.value === null) {
        return { input: 'Input cannot be null.' }
    }

    if (!isInteger(input.value)) {
        return { input: 'Invalid integer.' }
    }

    return null
}

function submitFilter() {
    if (input.value === null) {
        resetFilter()
    } else {
        isDirty.value = true
        errors.value = validate()

        if (!haveErrors.value) {
            emit('update:modelValue', {
                type: 'number',
                operator: filterType.value,
                value: input.value,
                property: props.field,
            })

            emit('hide')
        }
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
