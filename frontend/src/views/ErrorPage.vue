<template>
    <Basic>
        <div class="flex flex-1 items-center justify-center">
            <div class="text-center">
                <div class="text-8xl font-bold text-green-500 [text-shadow:0_4px_8px_rgba(0,0,0,0.12)]">
                    {{ derivedError }}
                </div>
                <div class="text-4xl text-green-500 dark:text-gray-100" style="font-family: 'Satisfy', cursive">
                    Error
                </div>
                <div class="dark:text-gray-100">{{ derivedMessage }}</div>

                <router-link
                    to="/"
                    class="relative mt-4 flex h-full w-full items-center justify-center overflow-hidden rounded border border-transparent bg-green-500 py-2 px-4 text-white transition duration-200 ease-linear hover:shadow-lg dark:border-gray-600 dark:bg-gray-700 dark:hover:shadow-black"
                    ><Ripple></Ripple> <span class="block">Go back home</span></router-link
                >
            </div>
        </div>
    </Basic>
</template>

<script lang="ts" setup>
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { type LocationQueryValue, useRoute } from 'vue-router'
import Basic from '@/layouts/Basic.vue'
import { ref, computed } from 'vue'
import Ripple from '@/components/Ripple.vue'

const error = ref<LocationQueryValue | LocationQueryValue[] | undefined>(useRoute().query.code)
const meta = useRoute().meta

if (error.value === undefined) {
    error.value = '404'
}

const derivedError = computed(() => {
    return parseInt(error?.value?.toString() ?? '0', 10)
})

const derivedMessage = computed(() => {
    if (derivedError.value === 404) {
        return 'The page that you are looking for is not found.'
    }

    return 'Error has occurred'
})
</script>
