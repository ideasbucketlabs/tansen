/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface ClusterInformation {
    id: string
    name: string
    label?: string
    online: boolean
    nodes: Node[]
    configurations: Configuration[]
    totalNumberOfTopics: number
    totalNumberOfPartitions: number
    totalReplicas: number
    inSyncReplicas: number
    outOfSyncReplicas: number
}

export interface Node {
    id: number
    port: number
    host: string
    rack?: string
    configurations: Configuration[]
}

export interface Configuration {
    name: string
    value: string
    definition: string
    type: string
}
