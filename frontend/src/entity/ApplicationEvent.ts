/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'

export type ApplicationEvent = {
    success: boolean
    eventType: ApplicationEventTypes
    data?: unknown
}
