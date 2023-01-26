<template>
    <div class="flex flex-1 flex-col bg-gray-100 dark:bg-gray-800">
        <Loading v-if="clusterInformationLoading"></Loading>
        <header
            role="banner"
            class="border-b border-gray-100 bg-white shadow dark:border-gray-800 dark:bg-gray-900 dark:shadow-gray-600/40"
        >
            <div class="flex h-16 justify-between p-2">
                <div class="flex content-center items-center">
                    <div class="w-36">
                        <router-link :to="{ name: 'dashboard' }" class="text-5xl text-green-500 dark:text-gray-100">
                            <Logo class="fill-current stroke-current text-green-500 dark:text-white"></Logo>
                        </router-link>
                    </div>
                </div>
                <div
                    class="flex cursor-pointer content-center px-1 lg:hidden"
                    @click="displayOffCanvas = !displayOffCanvas"
                >
                    <svg
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 448 512"
                        class="w-4 fill-current text-black dark:text-gray-100"
                    >
                        <path
                            d="M442 114H6a6 6 0 0 1-6-6V84a6 6 0 0 1 6-6h436a6 6 0 0 1 6 6v24a6 6 0 0 1-6 6zm0 160H6a6 6 0 0 1-6-6v-24a6 6 0 0 1 6-6h436a6 6 0 0 1 6 6v24a6 6 0 0 1-6 6zm0 160H6a6 6 0 0 1-6-6v-24a6 6 0 0 1 6-6h436a6 6 0 0 1 6 6v24a6 6 0 0 1-6 6z"
                        />
                    </svg>
                </div>
            </div>
        </header>
        <OffCanvas :show="displayOffCanvas" @update="displayOffCanvas = $event"></OffCanvas>
        <main class="relative flex h-12 flex-grow flex-row" role="main">
            <transition name="slide-right" mode="out-in">
                <aside
                    v-if="sideBarCollapsed"
                    @click="sideBarCollapsed = false"
                    title="Click to expand"
                    class="relative my-2 flex cursor-pointer items-center rounded-r bg-white px-2 pt-2 shadow hover:bg-gray-200 dark:bg-gray-900 dark:hover:bg-gray-700 lg:block lg:w-6"
                >
                    <div class="dark:text-gray-100">
                        <div class="rotate-90 transform-gpu whitespace-nowrap">Menu (click to expand)</div>
                    </div>
                </aside>
                <aside
                    class="relative my-2 hidden shrink-0 rounded bg-white px-2 pt-2 shadow dark:bg-gray-900 lg:block lg:w-56"
                    v-else
                >
                    <div
                        class="absolute bottom-0 right-0 flex h-8 w-8 cursor-pointer items-center justify-center rounded-br rounded-tl p-1 text-green-500 shadow-inner shadow-gray-300 transition duration-200 ease-linear hover:bg-green-500 hover:text-white hover:shadow hover:shadow-green-500 dark:border-green-500 dark:bg-green-700 dark:text-white dark:shadow-green-800"
                        @click="sideBarCollapsed = true"
                        title="Click to Collapse"
                    >
                        <LeftArrowIcon class="fill-current"></LeftArrowIcon>
                    </div>
                    <ul>
                        <li class="">
                            <router-link
                                :class="
                                    $route.name === 'dashboard'
                                        ? 'bg-green-500 text-white shadow-inner dark:bg-gray-700'
                                        : 'text-green-500 hover:cursor-pointer hover:bg-green-50 dark:text-gray-100 dark:hover:bg-gray-600'
                                "
                                class="mb-2 flex w-full content-center items-center rounded p-2 transition duration-200 ease-linear"
                                :to="{ name: 'dashboard' }"
                            >
                                <span class="mr-2 block h-6 w-6"
                                    ><DashboardIcon class="h-full w-full fill-current"></DashboardIcon
                                ></span>
                                Dashboard
                            </router-link>
                        </li>
                        <li
                            v-if="!clusterInformationLoading && clusterInformation.length !== 0"
                            class="flex cursor-default flex-col text-gray-400"
                        >
                            <span
                                class="select-none border-b uppercase text-gray-400 dark:border-gray-600 dark:text-gray-500"
                                >Cluster(s)</span
                            >
                            <cluster-block
                                class="my-2 block flex flex-col"
                                v-for="cluster in clusterInformation"
                                :key="cluster.id"
                                :label="cluster.label"
                                :name="cluster.name"
                                :online="cluster.online"
                                :good-health="cluster.outOfSyncReplicas === 0"
                            >
                            </cluster-block>
                        </li>
                    </ul>
                </aside>
            </transition>
            <router-view
                v-slot="slotProps"
                v-if="!clusterInformationLoading"
                class="mx-2 mt-2 mb-2 flex-grow overflow-y-auto bg-gray-100 text-black dark:border-gray-800 dark:bg-gray-800 dark:text-gray-100"
                :key="clusterId"
            >
                <transition name="fade" mode="out-in">
                    <component :is="slotProps.Component"></component>
                </transition>
            </router-view>
        </main>
        <footer
            role="contentinfo"
            class="flex h-8 items-center justify-center border-t border-gray-200 bg-gray-100 p-2 text-center shadow-xl dark:border-gray-800 dark:bg-gray-900"
        >
            <div class="flex space-x-2 text-green-500 dark:text-gray-100">
                <div>&#169;</div>
                <div class="block w-12">
                    <Logo class="h-full w-full fill-current stroke-current text-green-500 dark:text-white"></Logo>
                </div>
                <div>-</div>
                <div>{{ currentYear }}</div>
            </div>
        </footer>
    </div>
</template>

<script lang="ts" setup>
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { clusterInformationStore } from '@/stores/ClusterInformationStore'
import OffCanvas from '@/components/OffCanvas.vue'
import DashboardIcon from '@/icons/DashboardIcon.vue'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import LeftArrowIcon from '@/icons/LeftArrowIcon.vue'
import Logo from '@/icons/Logo.vue'
import Loading from '@/components/Loading.vue'
import type { ClusterInformation } from '@/entity/ClusterInformation'
import ClusterBlock from '@/components/ClusterBlock.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const seed = (Math.random() + 1).toString(36).substring(7) + performance.now().toString(10)
const clusterId = ref<string>((route.params?.clusterId ?? seed) as string)
const store = clusterInformationStore()
const displayOffCanvas = ref(false)
const currentYear = ref(new Date().getFullYear())
const sideBarCollapsed = ref<boolean>(false)
const clusterInformationLoading = ref<boolean>(true)
const clusterInformation = computed<ClusterInformation[]>(() => store.getClusters)
watch(
    () => route.params?.clusterId ?? seed,
    async (newClusterId) => {
        clusterId.value = newClusterId as string
    }
)

watch(
    () => route.fullPath,
    async () => {
        const givenClusterId = route.params?.clusterId ?? seed
        if (givenClusterId !== seed && !store.isValidCluster(givenClusterId as string)) {
            window.location.replace('/')
        }
    }
)

function onDocumentVisibilityChange(event: Event) {
    if (document.hidden) {
        eventBus.emit(ApplicationEventTypes.APPLICATION_DEACTIVATED, true, event)
    } else {
        eventBus.emit(ApplicationEventTypes.APPLICATION_ACTIVATED, true, event)
    }
}

function afterDataLoad() {
    clusterInformationLoading.value = false
}

function beforeDataLoad() {
    clusterInformationLoading.value = true
}

onMounted(async () => {
    document.addEventListener('visibilitychange', onDocumentVisibilityChange)
    eventBus.on(ApplicationEventTypes.BEFORE_CLUSTER_INFORMATION_DATA_LOADED, beforeDataLoad)
    eventBus.on(ApplicationEventTypes.CLUSTER_INFORMATION_DATA_LOADED, afterDataLoad)
    await store.loadDataIfNotInitialized()
    const givenClusterId = route.params?.clusterId ?? seed
    if (givenClusterId !== seed && !store.isValidCluster(givenClusterId as string)) {
        window.location.replace('/')
    }
})

onUnmounted(() => {
    document.removeEventListener('visibilitychange', onDocumentVisibilityChange)
    eventBus.off(ApplicationEventTypes.BEFORE_CLUSTER_INFORMATION_DATA_LOADED, beforeDataLoad)
    eventBus.off(ApplicationEventTypes.CLUSTER_INFORMATION_DATA_LOADED, afterDataLoad)
})
</script>

<style scoped>
.action-main-menu div.leaf {
    @apply hidden;
}
.action-main-menu:hover div.leaf,
.action-main-menu:focus div.leaf {
    @apply block;
}
</style>
