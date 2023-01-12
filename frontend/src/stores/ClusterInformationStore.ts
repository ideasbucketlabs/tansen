/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineStore } from 'pinia'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import { getAction } from '@/util/HttpService'
import type { ServerResponse } from '@/entity/ServerResponse'
import type { ClusterInformation } from '@/entity/ClusterInformation'
import * as cluster from 'cluster'

export const clusterInformationStore = defineStore('clusterInformation', {
    state: () => ({
        isInitialized: false,
        data: [] as ClusterInformation[],
        names: new Set<string>(),
    }),
    getters: {
        getClusters: (state): ClusterInformation[] => {
            return state.data
        },
        getOnlineClusters: (state): ClusterInformation[] => {
            return state.data.filter((cluster) => cluster.online)
        },
        getOfflineClusters: (state): ClusterInformation[] => {
            return state.data.filter((cluster) => !cluster.online)
        },
        getClusterById: (state): ((clusterId: string) => ClusterInformation | null) => {
            return (clusterId: string) => {
                const d = state.data.filter((it) => it.name === clusterId)
                if (d.length === 0) {
                    return null
                }
                return d[0]
            }
        },
        isDeleteEnabledInCluster: function (): (clusterId: string) => Boolean {
            const store = this
            return function (clusterId: string) {
                const clusterInformation = store.getClusterById(clusterId)
                if (clusterInformation === null) {
                    return false
                }
                const config = clusterInformation.configurations.filter((it) => it.name === 'delete.topic.enable')
                return config.length === 0 ? false : config[0].value.toLowerCase() === 'true'
            }
        },
        getClusterNodesNumbersById: (state): ((clusterId: string) => number) => {
            return (clusterId: string) => {
                const d = state.data.filter((it) => it.name === clusterId)
                if (d.length === 0) {
                    return 0
                }
                return d[0].nodes.length
            }
        },
        isValidCluster: (state): ((clusterId: string) => boolean) => {
            return (clusterId: string) => state.names.has(clusterId)
        },
    },
    actions: {
        async loadData() {
            eventBus.emit(ApplicationEventTypes.BEFORE_CLUSTER_INFORMATION_DATA_LOADED, true)
            await getAction(
                'cluster-information',
                (response: ServerResponse<ClusterInformation[]>) => {
                    this.data = response.data
                    this.isInitialized = true
                    response.data.forEach((it) => {
                        this.names.add(it.name)
                    })
                    eventBus.emit(ApplicationEventTypes.CLUSTER_INFORMATION_DATA_LOADED, true)
                },
                (errorResponse) => {
                    eventBus.emit(ApplicationEventTypes.CLUSTER_INFORMATION_DATA_LOADED, false, errorResponse)
                }
            )
        },
        async loadDataIfNotInitialized() {
            if (!this.isInitialized) {
                await this.loadData()
            }
        },
    },
})
