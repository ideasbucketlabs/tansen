<template>
    <Teleport to="body">
        <transition name="fade" @after-enter="displayOffCanvasInner = true">
            <div
                id="offCanvas"
                ref="offCanvas"
                class="fixed inset-x-0 top-0 z-20 flex h-full w-full"
                v-if="displayOffCanvas"
            >
                <div
                    id="offCanvasOverlay"
                    class="fixed absolute inset-x-0 top-0 flex h-full w-full flex-col bg-green-100 opacity-50 dark:bg-black"
                    @click="hideOffCanvas"
                ></div>
                <transition name="slide-right" @after-leave="displayOffCanvas = false">
                    <div
                        id="offCanvasMenu"
                        class="absolute top-0 flex h-full max-h-full w-4/5 flex-col overflow-hidden bg-white shadow shadow-lg dark:bg-gray-800"
                        v-if="displayOffCanvasInner"
                    >
                        <div
                            class="flex items-center justify-between border-b-2 border-green-500 p-2 dark:border-gray-800 dark:bg-gray-900"
                            style="height: 65px"
                        >
                            <div class="block">
                                <router-link
                                    :to="{ name: 'dashboard' }"
                                    @click="hideOffCanvas"
                                    class="block text-5xl text-green-500 dark:text-gray-100"
                                    ><Logo class="w-7/12 fill-current stroke-current text-green-500 dark:text-white" />
                                </router-link>
                            </div>
                            <div class="cursor-pointer px-2 py-1" @click="hideOffCanvas">
                                <svg
                                    class="ml-2 h-6 w-6 fill-current text-green-500 dark:text-gray-100"
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 320 512"
                                >
                                    <path
                                        d="M207.6 256l107.72-107.72c6.23-6.23 6.23-16.34 0-22.58l-25.03-25.03c-6.23-6.23-16.34-6.23-22.58 0L160 208.4 52.28 100.68c-6.23-6.23-16.34-6.23-22.58 0L4.68 125.7c-6.23 6.23-6.23 16.34 0 22.58L112.4 256 4.68 363.72c-6.23 6.23-6.23 16.34 0 22.58l25.03 25.03c6.23 6.23 16.34 6.23 22.58 0L160 303.6l107.72 107.72c6.23 6.23 16.34 6.23 22.58 0l25.03-25.03c6.23-6.23 6.23-16.34 0-22.58L207.6 256z"
                                    ></path>
                                </svg>
                            </div>
                        </div>
                        <div class="overflow-scrolling-touch flex h-full flex-col justify-between overflow-auto">
                            <div>
                                <ul class="m-2">
                                    <li class="">
                                        <router-link
                                            @click="hideOffCanvas"
                                            :class="
                                                $route.name === 'dashboard'
                                                    ? 'bg-green-500 text-white shadow-inner dark:bg-gray-700'
                                                    : 'text-green-500 hover:cursor-pointer hover:bg-green-50 dark:text-gray-100 dark:text-white dark:hover:bg-gray-600 dark:hover:bg-gray-600'
                                            "
                                            class="mb-2 block flex w-full content-center items-center rounded p-2 transition duration-200 ease-linear"
                                            :to="{ name: 'dashboard' }"
                                        >
                                            <span class="mr-2 block h-6 w-6">
                                                <dashboard-icon class="h-full w-full fill-current"></dashboard-icon>
                                            </span>
                                            Dashboard
                                        </router-link>
                                    </li>
                                    <li
                                        v-if="clusterInformation.length !== 0"
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
                                            @click="hideOffCanvas"
                                            :name="cluster.name"
                                            :online="cluster.online"
                                        >
                                        </cluster-block>
                                    </li>
                                </ul>
                            </div>
                            <!--                            <div class="">-->
                            <!--                                <ul class="text-sm">-->
                            <!--                                    <li class="m-4 rounded bg-green py-2 text-center font-bold text-white">Logout</li>-->
                            <!--                                </ul>-->
                            <!--                            </div>-->
                        </div>
                    </div>
                </transition>
            </div>
        </transition>
    </Teleport>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineProps, defineEmits, withDefaults, ref, watch, toRefs, onUnmounted, computed } from 'vue'
import DashboardIcon from '@/icons/DashboardIcon.vue'
import { clusterInformationStore } from '@/stores/ClusterInformationStore'
import type { ClusterInformation } from '@/entity/ClusterInformation'
import ClusterBlock from '@/components/ClusterBlock.vue'
import Logo from '@/icons/Logo.vue'

const store = clusterInformationStore()
const props = withDefaults(defineProps<{ show: boolean }>(), { show: true })
const { show } = toRefs(props)
const emit = defineEmits<{ (e: 'update', value: boolean): void }>()

let displayOffCanvas = ref(false)
let displayOffCanvasInner = ref(false)
let allElements: NodeList | null = null

const clusterInformation = computed<ClusterInformation[]>(() => store.getClusters)
function suppressScroll(event: Event) {
    if ((event?.target as HTMLElement)?.id === 'offCanvasOverlay') {
        hideOffCanvas()
    } else {
        event.cancelable && event.preventDefault()
    }
}

function showOffCanvas() {
    displayOffCanvas.value = true
    allElements = document.querySelectorAll('*')
    allElements.forEach((element) => {
        element.addEventListener('touchmove', suppressScroll, { passive: false })
    })

    emit('update', true)
}

function hideOffCanvas() {
    displayOffCanvasInner.value = false
    allElements?.forEach((element) => {
        element.removeEventListener('touchmove', suppressScroll)
    })
    allElements = null
    emit('update', false)
}

watch(show, (newValue) => {
    if (newValue) {
        showOffCanvas()
    }
})

onUnmounted(() => {
    if (allElements !== null) {
        allElements.forEach((element) => {
            element.removeEventListener('touchmove', suppressScroll)
        })
    }

    allElements = null
})
</script>
