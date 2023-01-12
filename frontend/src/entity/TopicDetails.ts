/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface TopicDetails {
    topic: string
    topicUuid: string
    numberOfPartition: number
    partitions: Partition[]
    configurations: Configuration[]
    replicationFactor: number
}

export interface Configuration {
    name: string
    value: string
    definition: string
    type: string
}

export interface Partition {
    topic: string
    partition: number
    leader: number
    replicas: number[]
    isr: number[]
    isOffline: boolean
    inSyncReplicaCount: number
    outOfSyncReplicaCount: number
    isUnderReplicated: boolean
    underReplicated: boolean
}
