/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface Topic {
    topic: string
    uuid: string
    isInternal: boolean
    totalPartitions: number
    inSyncReplicas: number
    offlineReplicas: number
    totalReplicas: number
    replicationFactor: number
}
