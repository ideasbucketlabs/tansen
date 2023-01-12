/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineStore } from 'pinia'
import { getFilterData, getPaginateItems, sortData } from '@/util/Util'
import type { ConsumerGroup } from '@/entity/ConsumerGroup'
import type { Filter } from '@/entity/Filter'
import type { Sorter } from '@/entity/Sorter'
import type { PagedData } from '@/entity/PagedData'
import eventBus from '@/util/EventBus'
import { getAction } from '@/util/HttpService'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ServerResponse } from '@/entity/ServerResponse'

export const consumerGroupStore = defineStore('consumerGroups', {
    state: () => ({
        isInitialized: new Map<string, boolean>(),
        data: new Map<string, ConsumerGroup[]>(),
    }),
    getters: {
        getData: (state): ((clusterId: string) => ConsumerGroup[]) => {
            return (clusterId: string) => state.data.get(clusterId) ?? []
        },
        getPaginatedItems: (): ((
            currentPage: number,
            pageSize: number,
            items: ConsumerGroup[]
        ) => PagedData<ConsumerGroup>) => {
            return (currentPage: number, pageSize = 50, items: ConsumerGroup[]) =>
                getPaginateItems<ConsumerGroup>(items, currentPage, pageSize)
        },
        getSortedItems: () => {
            return (data: ConsumerGroup[], sorters?: Sorter[]): ConsumerGroup[] => {
                if (!Array.isArray(data) || !data.length) {
                    return data
                }

                if (sorters === undefined || sorters === null) {
                    return data
                }

                return sortData<ConsumerGroup>(data, sorters)
            }
        },
        getFilteredItems: (
            state
        ): ((clusterId: string, filters: Filter[], data: ConsumerGroup[] | null) => ConsumerGroup[]) => {
            return (clusterId: string, filters: Filter[], data: ConsumerGroup[] | null) =>
                getFilterData<ConsumerGroup>(data ?? state.data.get(clusterId) ?? [], filters)
        },
    },
    actions: {
        async loadData(clusterId: string) {
            eventBus.emit(ApplicationEventTypes.BEFORE_CONSUMER_GROUP_DATA_LOADED, true)
            await getAction(
                `${clusterId}/consumer-groups`,
                (response: ServerResponse<ConsumerGroup[]>) => {
                    this.data.set(clusterId, response.data)
                    this.isInitialized.set(clusterId, true)
                    eventBus.emit(ApplicationEventTypes.CONSUMER_GROUP_DATA_LOADED, true)
                },
                (errorResponse) => {
                    eventBus.emit(ApplicationEventTypes.CONSUMER_GROUP_DATA_LOADED, false, errorResponse)
                }
            )
        },
        async loadDataIfNotInitialized(clusterId: string) {
            if (!this.isInitialized.get(clusterId) ?? false) {
                await this.loadData(clusterId)
            }
        },
    },
})
