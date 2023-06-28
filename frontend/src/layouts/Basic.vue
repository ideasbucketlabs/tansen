<template>
    <div class="flex flex-1 flex-col bg-gray-100 dark:bg-gray-800">
        <div class="flex h-12 flex-grow flex-col lg:flex-row">
            <div class="flex items-center justify-center bg-green-500 dark:bg-green-800 lg:flex-1">
                <div class="w-8/12 p-2 lg:p-0">
                    <Logo class="h-full w-full fill-current stroke-current text-white"></Logo>
                </div>
            </div>
            <div class="relative flex flex-1 bg-white dark:bg-gray-700">
                <div class="absolute top-0 hidden h-full w-20 xl:block" style="left: -80px">
                    <svg
                        class="min-h-screen w-20 transition-all duration-500"
                        filter="drop-shadow(-15px 0 5px var(--angle-shadow))"
                        viewBox="0 0 100 100"
                        xmlns="http://www.w3.org/2000/svg"
                        preserveAspectRatio="none"
                    >
                        <polygon
                            class="text-white dark:text-gray-700"
                            fill="currentColor"
                            points="100,100 0,100 100,0"
                        />
                    </svg>
                </div>
                <slot></slot>
            </div>
        </div>
        <div
            class="z-10 flex h-8 items-center justify-center border-t border-gray-200 bg-gray-100 p-2 text-center shadow-xl dark:border-gray-800 dark:bg-gray-900"
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
