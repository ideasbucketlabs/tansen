<template>
    <div class="relative flex flex-1 flex-col overflow-y-auto pt-2">
        <Loading v-if="!isConfigurationCheckDone"></Loading>
        <div v-if="isConfigurationCheckDone && isConfigured" class="flex flex-1 flex-col">
            <div class="space-x-4 pt-2" v-if="isConfigurationCheckDone">
                <router-link
                    class="relative inline-block overflow-hidden rounded border border-green-500 px-4 py-1 transition duration-200 ease-linear"
                    :class="
                        $route.name === 'topicSchemaValue'
                            ? 'cursor-default bg-green-500 text-white shadow-inner shadow-green-800/50 dark:border-gray-700 dark:bg-gray-700 dark:shadow-black/50'
                            : 'hover:bg-green-50 hover:shadow-lg dark:border-gray-600 dark:hover:bg-gray-600 dark:hover:shadow-black'
                    "
                    :to="{ name: 'topicSchemaValue' }"
                >
                    Value
                </router-link>
                <router-link
                    class="relative inline-block overflow-hidden rounded border border-green-500 px-4 py-1 transition duration-200 ease-linear"
                    :class="
                        $route.name === 'topicSchemaKey'
                            ? 'cursor-default bg-green-500 text-white shadow-inner shadow-green-800/50 dark:border-gray-700 dark:bg-gray-700 dark:shadow-black/50'
                            : 'hover:bg-green-50 hover:shadow-lg dark:border-gray-600 dark:hover:bg-gray-600 dark:hover:shadow-black'
                    "
                    :to="{ name: 'topicSchemaKey' }"
                >
                    Key
                </router-link>
            </div>
            <router-view v-slot="slotProps">
                <transition name="fade" mode="out-in">
                    <component
                        :is="slotProps.Component"
                        :subject="subject"
                        :type="type"
                        :key="$route.fullPath"
                        class=""
                    ></component>
                </transition>
            </router-view>
        </div>
        <div class="mt-4 text-xl" v-if="isConfigurationCheckDone && !isConfigured">Schema registry not configured.</div>
    </div>
</template>
<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { computed, onMounted } from 'vue'
import Loading from '@/components/Loading.vue'
import { schemaStore } from '@/stores/SchemaStore'
import { useRoute } from 'vue-router'

const route = useRoute()
const store = schemaStore()
const topic = route.params.name
const clusterId = route.params.clusterId as string
const isConfigurationCheckDone = computed<boolean>(() => {
    return store.isSchemaRegistryConfigurationChecked.get(clusterId) ?? false
})

const isConfigured = computed<boolean>(() => {
    return store.isSchemaRegistryConfigured.get(clusterId) ?? false
})

const subject = computed(() => {
    return route.name === 'topicSchemaValue' ? topic + '-value' : topic + '-key'
})

const type = computed(() => {
    return route.name === 'topicSchemaValue' ? 'value' : 'key'
})

onMounted(async () => {
    await store.checkIfSchemaRegistryIsConfigured(clusterId)
})
</script>
