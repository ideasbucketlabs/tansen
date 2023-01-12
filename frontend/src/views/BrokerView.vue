<template>
    <div class="flex flex-1 flex-col">
        <div class="flex items-center text-green-500 dark:text-gray-100">
            <div class="mr-2 h-6 w-6" aria-hidden="true" aria-label="Dashboard">
                <BrokerIcon class="h-full w-full fill-current"></BrokerIcon>
            </div>
            <h1 class="block text-xl md:text-3xl">Broker Information</h1>
        </div>
        <div class="overflow-y-auto md:flex md:flex-1 md:flex-col">
            <div v-if="cluster === null" class="mt-2 rounded shadow">
                <div class="rounded-t bg-red-500 p-2 text-white">Error</div>
                <div class="rounded-b bg-white p-2">Invalid Broker. Please go back.</div>
            </div>
            <ClusterInformation v-else :cluster="cluster" class="mt-2 md:flex-1"></ClusterInformation>
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
import { useRoute } from 'vue-router'
import BrokerIcon from '@/icons/BrokerIcon.vue'
import { clusterInformationStore } from '@/stores/ClusterInformationStore'
import type { ClusterInformation as ClusterInformationEntity } from '@/entity/ClusterInformation'
import { computed } from 'vue'
import ClusterInformation from '@/components/ClusterInformation.vue'

const store = clusterInformationStore()
const clusterId = useRoute().params.clusterId as string

const cluster = computed<ClusterInformationEntity | null>(() => store.getClusterById(clusterId))
</script>
