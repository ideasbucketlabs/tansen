/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import type { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'

class EventBus {
    private readonly eventMap: Map<string, Array<(event: ApplicationEvent) => void>>
    constructor() {
        this.eventMap = new Map()
    }

    on(eventName: ApplicationEventTypes, fn: (event: ApplicationEvent) => void) {
        const handlers = this.eventMap.get(eventName)
        const added = handlers && handlers.push(fn)
        if (!added) {
            this.eventMap.set(eventName, [fn])
        }
    }

    once(eventName: ApplicationEventTypes, fn: (event: ApplicationEvent) => void) {
        const removeFn = (event: ApplicationEvent) => {
            fn(event)
            this.off(eventName, removeFn)
        }
        const handlers = this.eventMap.get(eventName)
        const added = handlers && handlers.push(removeFn)
        if (!added) {
            this.eventMap.set(eventName, [removeFn])
        }
    }

    off(eventName: ApplicationEventTypes, fn?: (event: ApplicationEvent) => void) {
        if (!this.eventMap.has(eventName)) {
            return
        }

        if (fn === undefined || fn === null) {
            this.eventMap.delete(eventName)

            return
        }

        const handlers = this.eventMap.get(eventName)

        if (handlers === undefined) {
            return
        }

        handlers.splice(handlers.indexOf(fn) >>> 0, 1)

        if (handlers.length === 0) {
            this.eventMap.delete(eventName)
        } else {
            this.eventMap.set(eventName, handlers)
        }
    }

    emit(eventName: ApplicationEventTypes, success: boolean, data?: unknown) {
        ;(this.eventMap.get(eventName) || []).slice().forEach((handler) => {
            handler({
                success,
                eventType: eventName,
                data,
            })
        })
    }

    fire(eventName: ApplicationEventTypes, success: boolean, data?: unknown) {
        this.emit(eventName, success, data)
    }
}

const eventBus = new EventBus()

export default eventBus
