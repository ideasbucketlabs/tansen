/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { defineStore } from 'pinia'
import { getFilterData, getPaginateItems, sortData, getTimestamp } from '@/util/Util'
import type { Topic } from '@/entity/Topic'
import type { Filter } from '@/entity/Filter'
import type { Sorter } from '@/entity/Sorter'
import type { PagedData } from '@/entity/PagedData'
import type { ConsumerGroup } from '@/entity/ConsumerGroup'
import eventBus from '@/util/EventBus'
import { deleteAction, getAction, postAction, putAction } from '@/util/HttpService'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ServerResponse } from '@/entity/ServerResponse'
import type { NewTopic } from '@/entity/NewTopic'
import type { ErrorResponse } from '@/entity/ErrorResponse'

export const topicStore = defineStore('topics', {
    state: () => ({
        isInitialized: new Map<string, boolean>(),
        dataInitializedAt: new Map<string, number>(),
        consumerGroupData: new Map<string, Map<string, ConsumerGroup[]>>(),
        consumerGroupDataLoadedTime: new Map<string, Map<string, number>>(),
        data: new Map<string, Topic[]>(),
    }),
    getters: {
        getData: (state): ((clusterId: string) => Topic[]) => {
            return (clusterId: string) => state.data.get(clusterId) ?? []
        },
        getPaginatedItems: (): ((currentPage: number, pageSize: number, items: Topic[]) => PagedData<Topic>) => {
            return (currentPage: number, pageSize = 50, items: Topic[]) =>
                getPaginateItems<Topic>(items, currentPage, pageSize)
        },
        getSortedItems: () => {
            return (data: Topic[], sorters?: Sorter[]): Topic[] => {
                if (!Array.isArray(data) || !data.length) {
                    return data
                }

                if (sorters === undefined || sorters === null) {
                    return data
                }

                return sortData<Topic>(data, sorters)
            }
        },
        getFilteredItems: (state): ((clusterId: string, filters: Filter[]) => Topic[]) => {
            return (clusterId: string, filters: Filter[]) =>
                getFilterData<Topic>(state.data.get(clusterId) ?? [], filters)
        },
        getConsumerGroups: (state): ((clusterId: string, topicName: string) => ConsumerGroup[]) => {
            return (clusterId: string, topicName: string) => {
                return state.consumerGroupData.get(clusterId)?.get(topicName) ?? []
            }
        },
    },
    actions: {
        async loadData(clusterId: string) {
            eventBus.emit(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, true)
            await getAction(
                clusterId + '/topics',
                (response: ServerResponse<Topic[]>) => {
                    this.data.set(clusterId, response.data)
                    this.isInitialized.set(clusterId, true)
                    this.dataInitializedAt.set(clusterId, getTimestamp())
                    eventBus.emit(ApplicationEventTypes.TOPIC_DATA_LOADED, true)
                },
                (errorResponse) => {
                    eventBus.emit(ApplicationEventTypes.TOPIC_DATA_LOADED, false, errorResponse)
                }
            )
        },
        async loadDataIfNotInitialized(clusterId: string) {
            if (!this.isInitialized || getTimestamp() - (this.dataInitializedAt.get(clusterId) ?? 0) > 300) {
                await this.loadData(clusterId)
            }
        },

        async loadTopicConsumers(clusterId: string, topicName: string) {
            eventBus.emit(ApplicationEventTypes.BEFORE_TOPIC_CONSUMER_DATA_LOADED, true)

            await getAction(
                `${clusterId}/topic-consumer-groups/${topicName}`,
                (response: ServerResponse<ConsumerGroup[]>) => {
                    const existingConsumerGroupData =
                        this.consumerGroupData.get(clusterId) ?? new Map<string, ConsumerGroup[]>()

                    existingConsumerGroupData.set(topicName, response.data)
                    this.consumerGroupData.set(clusterId, existingConsumerGroupData)

                    const existingConsumerGroupDataLoadedTime =
                        this.consumerGroupDataLoadedTime.get(clusterId) ?? new Map<string, number>()
                    existingConsumerGroupDataLoadedTime.set(topicName, getTimestamp())
                    this.consumerGroupDataLoadedTime.set(clusterId, existingConsumerGroupDataLoadedTime)

                    eventBus.emit(ApplicationEventTypes.TOPIC_CONSUMER_DATA_LOADED, true)
                },
                (errorResponse) => {
                    eventBus.emit(ApplicationEventTypes.TOPIC_CONSUMER_DATA_LOADED, false, errorResponse)
                }
            )
        },
        async loadTopicConsumersIfNotLoaded(clusterId: string, topic: string) {
            if (!(this.consumerGroupData.get(clusterId) ?? new Map<string, ConsumerGroup[]>()).has(topic)) {
                await this.loadTopicConsumers(clusterId, topic)
                return
            }
            const from = (this.consumerGroupDataLoadedTime.get(clusterId) ?? new Map()).get(topic) ?? getTimestamp()
            const delta = getTimestamp() - from
            if (delta > 300) {
                await this.loadTopicConsumers(clusterId, topic)
            }
        },
        async addTopic(clusterId: string, newTopic: NewTopic) {
            eventBus.emit(ApplicationEventTypes.BEFORE_TOPIC_ADDED, true)
            await postAction(
                `${clusterId}/topics`,
                newTopic,
                () => {
                    this.loadData(clusterId)
                    eventBus.emit(ApplicationEventTypes.TOPIC_ADDED, true)
                },
                (errorResponse: ErrorResponse) => {
                    eventBus.emit(ApplicationEventTypes.TOPIC_ADDED, false, errorResponse)
                }
            )
        },
        async editTopic(clusterId: string, data: { oldRecord: NewTopic; newRecord: NewTopic }) {
            eventBus.emit(ApplicationEventTypes.BEFORE_TOPIC_EDITED, true)
            await putAction(
                `${clusterId}/topics`,
                data,
                () => {
                    eventBus.emit(ApplicationEventTypes.TOPIC_EDITED, true)
                },
                (errorResponse: ErrorResponse) => {
                    eventBus.emit(ApplicationEventTypes.TOPIC_EDITED, false, errorResponse)
                }
            )
        },
        async deleteTopic(clusterId: string, topicName: string) {
            eventBus.emit(ApplicationEventTypes.BEFORE_TOPIC_DELETED, true)
            await deleteAction(
                `${clusterId}/topics/${topicName}`,
                () => {
                    this.loadData(clusterId)
                    eventBus.emit(ApplicationEventTypes.TOPIC_DELETED, true)
                    const existingConsumerGroupData =
                        this.consumerGroupData.get(clusterId) ?? new Map<string, ConsumerGroup[]>()

                    existingConsumerGroupData.delete(topicName)
                    this.consumerGroupData.set(clusterId, existingConsumerGroupData)

                    const existingConsumerGroupDataLoadedTime =
                        this.consumerGroupDataLoadedTime.get(clusterId) ?? new Map<string, number>()
                    existingConsumerGroupDataLoadedTime.delete(topicName)
                    this.consumerGroupDataLoadedTime.set(clusterId, existingConsumerGroupDataLoadedTime)
                },
                (errorResponse: ErrorResponse) => {
                    eventBus.emit(ApplicationEventTypes.TOPIC_DELETED, false, errorResponse)
                }
            )
        },
    },
})
