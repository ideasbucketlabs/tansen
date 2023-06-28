<template>
    <div class="flex flex-col">
        <div class="flex items-center text-green-500 dark:text-gray-100">
            <div class="mr-2 h-6 w-6" aria-hidden="true" aria-label="Dashboard">
                <DashboardIcon class="h-full w-full fill-current"></DashboardIcon>
            </div>
            <h1 class="text-xl md:text-3xl">Dashboard</h1>
        </div>
        <div class="space-y-2 overflow-y-auto md:flex md:flex-1 md:flex-col">
            <div class="text-2xl" v-if="clusters.length !== 0">Cluster(s)</div>
            <ClusterInformation v-for="cluster in clusters" :key="cluster.id" :cluster="cluster"></ClusterInformation>
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
import DashboardIcon from '@/icons/DashboardIcon.vue'
import { clusterInformationStore } from '@/stores/ClusterInformationStore'
import { computed } from 'vue'
import type { ClusterInformation as ClusterInformationEntity } from '@/entity/ClusterInformation'
import ClusterInformation from '@/components/ClusterInformation.vue'

const store = clusterInformationStore()
const clusters = computed<ClusterInformationEntity[]>(() => store.getClusters)
</script>
