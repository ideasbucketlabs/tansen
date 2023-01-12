<template>
    <ul>
        <li
            @click="collapsed = !collapsed"
            class="mt-2 mb-2 flex cursor-pointer items-center justify-between uppercase dark:border-dotted dark:border-gray-400"
            :title="collapsed ? 'Click to expand the panel.' : 'Click to collapse the panel.'"
        >
            <span class="block flex cursor-pointer items-center space-x-2 text-green-500 dark:text-gray-100">
                <span class="block truncate">{{ name }}</span>
                <status-bulb :online="online"></status-bulb>
            </span>
            <span class="w-6">
                <PlusIcon v-if="collapsed" class="fill-current text-green-500 dark:text-gray-200"></PlusIcon>
                <MinusIcon v-else class="fill-current text-green-500 dark:text-gray-200"></MinusIcon>
            </span>
        </li>
        <transition name="slide-up-down">
            <li v-if="!collapsed">
                <ul>
                    <template v-if="online">
                        <li class="block flex w-full content-center items-center">
                            <router-link
                                class="mb-2 flex w-full content-center items-center rounded p-2 transition duration-200 ease-linear"
                                :class="
                                    $route.name === 'broker' && $route.params.clusterId === name
                                        ? 'bg-green-500 text-white shadow-inner hover:cursor-default dark:bg-gray-700'
                                        : 'text-green-500 hover:cursor-pointer hover:bg-green-50 dark:text-gray-100 dark:hover:bg-gray-600'
                                "
                                :to="{ name: 'broker', params: { clusterId: name } }"
                            >
                                <span class="mr-2 block h-6 w-6">
                                    <BrokerIcon class="h-full w-full fill-current"></BrokerIcon></span
                                >Broker(s) Information
                            </router-link>
                        </li>
                        <li class="block flex w-full content-center items-center">
                            <router-link
                                class="mb-2 flex w-full content-center items-center rounded p-2 transition duration-200 ease-linear"
                                :class="[
                                    ($route.name === 'topics' && $route.params.clusterId === name) ||
                                    ($route.name === 'topicOverview' && $route.params.clusterId === name) ||
                                    ($route.name === 'topicSchemaValue' && $route.params.clusterId === name) ||
                                    ($route.name === 'topicSchemaKey' && $route.params.clusterId === name) ||
                                    ($route.name === 'topicMessages' && $route.params.clusterId === name)
                                        ? 'bg-green-500 text-white shadow-inner dark:bg-gray-700'
                                        : 'text-green-500 hover:cursor-pointer hover:bg-green-50 dark:text-gray-100 dark:hover:bg-gray-600',
                                    { 'cursor-default': $route.name === 'topics' && $route.params.clusterId === name },
                                ]"
                                :to="{ name: 'topics', params: { clusterId: name } }"
                            >
                                <span class="mr-2 block h-6 w-6">
                                    <TopicsIcon class="h-full w-full fill-current"></TopicsIcon></span
                                >Topic(s)
                            </router-link>
                        </li>
                        <li class="block flex w-full content-center items-center">
                            <router-link
                                class="mb-2 flex w-full content-center items-center rounded p-2 transition duration-200 ease-linear"
                                :class="
                                    $route.name === 'consumersGroup' && $route.params.clusterId === name
                                        ? 'bg-green-500 text-white shadow-inner hover:cursor-default dark:bg-gray-700'
                                        : 'text-green-500 hover:cursor-pointer hover:bg-green-50 dark:text-gray-100 dark:hover:bg-gray-600'
                                "
                                :to="{ name: 'consumersGroup', params: { clusterId: name } }"
                            >
                                <span class="mr-2 block h-6 w-6">
                                    <ConsumerGroupsIcon class="h-full w-full fill-current"></ConsumerGroupsIcon>
                                </span>
                                Consumer Group(s)
                            </router-link>
                        </li>
                    </template>
                    <template v-else>
                        <li
                            class="mb-2 block flex w-full content-center items-center rounded p-2 text-gray-500 hover:cursor-not-allowed dark:text-gray-400"
                            title="Cluster is not online to view any topics."
                        >
                            <span class="mr-2 block h-6 w-6">
                                <TopicsIcon class="h-full w-full fill-current"></TopicsIcon
                            ></span>
                            Topic(s)
                        </li>
                        <li
                            class="mb-2 block flex w-full content-center items-center rounded p-2 text-gray-500 hover:cursor-not-allowed dark:text-gray-400"
                            title="Cluster is not online to view any consumer groups."
                        >
                            <span class="mr-2 block h-6 w-6">
                                <ConsumerGroupsIcon class="h-full w-full fill-current"></ConsumerGroupsIcon>
                            </span>
                            Consumer Group(s)
                        </li>
                    </template>
                </ul>
            </li>
        </transition>
    </ul>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineProps, ref } from 'vue'
import type { PropType } from 'vue'
import ConsumerGroupsIcon from '@/icons/ConsumerGroupsIcon.vue'
import TopicsIcon from '@/icons/TopicsIcon.vue'
import PlusIcon from '@/icons/PlusIcon.vue'
import MinusIcon from '@/icons/MinusIcon.vue'
import BrokerIcon from '@/icons/BrokerIcon.vue'
import StatusBulb from '@/components/StatusBulb.vue'

defineProps({
    label: {
        type: String as PropType<string>,
    },
    name: {
        type: String as PropType<string>,
    },
    online: {
        type: Boolean as PropType<boolean>,
    },
})

const collapsed = ref<boolean>(false)
</script>

<style scoped>
.slide-up-down-enter-active,
.slide-up-down-leave-active {
    transition-property: height, opacity;
    transition-timing-function: ease-in-out;
    transition-duration: 0.5s;
}

.slide-up-down-enter-to,
.slide-up-down-leave-from {
    overflow: hidden;
    opacity: 1;
    height: 144px;
}

.slide-up-down-enter-from,
.slide-up-down-leave-to {
    overflow: hidden;
    opacity: 0;
    height: 0;
}
</style>
