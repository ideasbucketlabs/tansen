/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface ConsumerGroup {
    name: string
    topic: string
    partition: number
    currentOffset: number
    logEndOffset: number
    lag: number
}
