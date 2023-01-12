<template>
    <div>
        <div class="space-y-4 md:flex md:space-x-4 md:space-y-0">
            <div class="md:flex md:flex-1 md:items-center">
                <div class="font-bold md:mr-2">Compare</div>
                <BaseSelectField
                    label="Schema version"
                    :hide-label="true"
                    class="md:flex-1"
                    v-model.number="leftSide"
                    :options="reversedSchemaVersions"
                ></BaseSelectField>
            </div>
            <div class="md:flex md:flex-1 md:items-center">
                <div class="font-bold md:mr-2">With</div>
                <BaseSelectField
                    label="Schema version"
                    :hide-label="true"
                    class="md:flex-1"
                    :options="schemaVersions"
                    v-model.number="rightSide"
                ></BaseSelectField>
            </div>
            <!--            <div class="flex flex-1 items-center space-x-2">-->
            <!--                <label class="inline-flex items-center">-->
            <!--                    <input type="radio" value="unified" v-model="mode" />-->
            <!--                    <span class="ml-2">Unified view</span>-->
            <!--                </label>-->
            <!--                <label class="inline-flex items-center">-->
            <!--                    <input type="radio" value="split" v-model="mode" />-->
            <!--                    <span class="ml-2">Split view</span>-->
            <!--                </label>-->
            <!--            </div>-->
        </div>
        <div class="relative md:flex md:flex-1">
            <Loading v-if="leftContent === '' || rightContent === ''"></Loading>
            <DiffViewer
                :left-version="rightContent"
                :right-version="leftContent"
                :left-version-name="leftSelectVersionName"
                :right-version-name="rightSelectVersionName"
                :mode="mode"
                class="mt-2 min-h-80 w-[22.4rem] overflow-auto rounded border bg-white shadow dark:border-gray-900 dark:shadow-black md:flex md:w-auto md:flex-1"
            ></DiffViewer>
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
import { type PropType, defineProps, computed, ref, onMounted, watch } from 'vue'
import { schemaStore } from '@/stores/SchemaStore'
import BaseSelectField from '@/components/BaseSelectField.vue'
import DiffViewer from './DiffViewer.vue'
import Loading from '@/components/Loading.vue'
import { useRoute } from 'vue-router'

const clusterId = useRoute().params.clusterId as string
const store = schemaStore()
const props = defineProps({
    schemaVersions: {
        type: Array as PropType<{ label: string; value: number }[]>,
        required: true,
    },
    subject: {
        type: String as PropType<string>,
        required: true,
    },
})

const reversedSchemaVersions = computed<{ label: string; value: number }[]>(() => {
    return props.schemaVersions.slice().reverse()
})

const leftSide = ref<number>(reversedSchemaVersions.value[0].value)
const rightSide = ref<number>(props.schemaVersions[0].value)
const mode = ref<'split' | 'unified'>('split')

const leftContent = computed<string>(() => {
    return store.getSchemaBySubjectAndVersion(clusterId, props.subject, leftSide.value)?.schema ?? ''
})

const leftSelectVersionName = computed<string>(() => {
    return props.schemaVersions.filter((item) => item.value === leftSide.value)[0].label
})

const rightContent = computed<string>(() => {
    return store.getSchemaBySubjectAndVersion(clusterId, props.subject, rightSide.value)?.schema ?? ''
})

const rightSelectVersionName = computed<string>(() => {
    return props.schemaVersions.filter((item) => item.value === rightSide.value)[0].label
})

watch(rightSide, async (newRightSide) => {
    await store.loadSchemaIfNotLoaded(clusterId, props.subject, newRightSide)
})

watch(leftSide, async (newLeftSide) => {
    await store.loadSchemaIfNotLoaded(clusterId, props.subject, newLeftSide)
})

onMounted(async () => {
    await Promise.all([
        store.loadSchemaIfNotLoaded(clusterId, props.subject, leftSide.value),
        store.loadSchemaIfNotLoaded(clusterId, props.subject, rightSide.value),
    ])
})
</script>
