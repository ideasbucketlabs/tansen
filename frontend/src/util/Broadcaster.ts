/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import eventBus from '@/util/EventBus'

class Broadcaster {
    private readonly bc: BroadcastChannel
    constructor() {
        this.bc = new BroadcastChannel('tansen-application')
        this.bc.onmessage = (event) => {
            const applicationEvent = event.data as ApplicationEvent
            eventBus.emit(applicationEvent.eventType, applicationEvent.success, applicationEvent.data)
        }
    }

    post(eventName: ApplicationEventTypes, success: boolean, data?: unknown) {
        this.bc.postMessage({
            success: success,
            eventType: eventName,
            data,
        })
    }

    close() {
        this.bc.close()
    }
}

const broadcaster = new Broadcaster()

export default broadcaster
