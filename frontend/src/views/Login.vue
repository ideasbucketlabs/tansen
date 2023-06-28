<template>
    <Basic>
        <div class="relative flex flex-1 items-center justify-center">
            <Loading v-if="isLoading" layover-class="bg-white dark:bg-gray-700"></Loading>

            <div v-else class="flex flex-col items-center space-y-4 text-center">
                <div
                    class="flex w-52"
                    v-for="(authOptions, index) in authenticationOptions?.loginOptions ?? []"
                    :key="index"
                >
                    <a
                        :href="authOptions.uri"
                        class="relative w-full rounded border border-green-500 px-3 py-2 transition duration-200 ease-linear hover:cursor-pointer hover:bg-green-100 hover:shadow-lg dark:border-gray-900 dark:bg-gray-800 dark:text-white dark:hover:bg-gray-900"
                    >
                        <Ripple></Ripple>
                        <span
                            >Login with <span class="font-semibold capitalize">{{ authOptions.name }}</span></span
                        ></a
                    >
                </div>
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
import Basic from '@/layouts/Basic.vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAction } from '@/util/HttpService'
import type { AuthenticationStatus } from '@/entity/AuthenticationStatus'
import Loading from '@/components/Loading.vue'
import type { ServerResponse } from '@/entity/ServerResponse'
import Ripple from '@/components/Ripple.vue'

const router = useRouter()
const isLoading = ref<Boolean>(false)
const authenticationOptions = ref<AuthenticationStatus>()
onMounted(async () => {
    await getAction('/authentication', (options: ServerResponse<AuthenticationStatus>) => {
        isLoading.value = false
        if (options.data?.loggedIn ?? false) {
            router.replace({ path: '/' })
        }

        const urlOptions = options.data.loginOptions ?? []

        if (urlOptions.length === 1) {
            window.location.replace(urlOptions[0].uri)
        }
        authenticationOptions.value = options.data
    })
})
</script>
