<template>
    <div class="flex flex-1 flex-col bg-gray-100 dark:bg-gray-800">
        <header
            class="border-b border-gray-100 bg-white shadow dark:border-gray-800 dark:bg-gray-900 dark:shadow-gray-600/40"
        >
            <div class="flex h-16 justify-between p-2">
                <div class="flex content-center items-center">
                    <div class="w-36">
                        <router-link
                            :to="{ name: 'dashboard' }"
                            class="text-5xl text-green-500 dark:text-gray-100"
                            style="font-family: 'Satisfy', cursive"
                        >
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
        <div class="flex h-12 flex-grow flex-row">
            <slot></slot>
        </div>
        <div
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
import { ref } from 'vue'
import OffCanvas from '@/components/OffCanvas.vue'
import Logo from '@/icons/Logo.vue'

const displayOffCanvas = ref(false)
const currentYear = ref(new Date().getFullYear())
</script>
