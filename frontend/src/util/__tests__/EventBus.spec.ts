/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { describe, it, expect } from 'vitest'
import eventBus from '@/util/EventBus'
import type { ApplicationEvent } from '@/entity/ApplicationEvent'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'

describe('Event bus can remove, add, fire events properly', () => {
    it('can fire event properly', () => {
        let counter = 0
        eventBus.on(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, (data: ApplicationEvent) => {
            counter += 1
            expect(data.success).toBe(true)
        })

        eventBus.emit(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, true)
        expect(counter).toBe(1)
    })

    it('can fire event properly multiple times', () => {
        let counter = 0
        eventBus.on(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, (data: ApplicationEvent) => {
            counter += 1
            expect(data.success).toBe(true)
        })

        eventBus.emit(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, true)
        eventBus.fire(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, true)
        expect(counter).toBe(2)
    })

    it('can remove event properly', () => {
        let counter = 0
        eventBus.on(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, (data: ApplicationEvent) => {
            counter += 1
            expect(data.success).toBe(true)
        })

        function fn(data: ApplicationEvent) {
            counter += 2
            expect(data.success).toBe(true)
        }

        eventBus.on(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, fn)
        eventBus.off(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, fn)
        eventBus.emit(ApplicationEventTypes.BEFORE_TOPIC_DATA_LOADED, true)
        expect(counter).toBe(1)
    })

    it('if no name is provided it will remove all handlers', () => {
        let counter = 0
        eventBus.on(ApplicationEventTypes.TOPIC_DATA_LOADED, (data: ApplicationEvent) => {
            counter += 1
            expect(data.success).toBe(true)
        })

        function fn(data: ApplicationEvent) {
            counter += 2
            expect(data.success).toBe(true)
        }

        eventBus.on(ApplicationEventTypes.TOPIC_DATA_LOADED, fn)
        eventBus.off(ApplicationEventTypes.TOPIC_DATA_LOADED)
        eventBus.emit(ApplicationEventTypes.TOPIC_DATA_LOADED, true)
        expect(counter).toBe(0)
    })

    it('firing event just once works', () => {
        let counter = 0
        eventBus.once(ApplicationEventTypes.TOPIC_DATA_LOADED, (data: ApplicationEvent) => {
            counter += 1
            expect(data.success).toBe(true)
        })

        eventBus.emit(ApplicationEventTypes.TOPIC_DATA_LOADED, true)
        eventBus.emit(ApplicationEventTypes.TOPIC_DATA_LOADED, true)
        expect(counter).toBe(1)
    })
})
