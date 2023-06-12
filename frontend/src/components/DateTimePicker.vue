<template>
    <div
        class="overflow-hidden rounded border-2 border-blue-500 bg-white text-black dark:bg-gray-800"
        :style="containerStyle"
        v-click-outside="clickedOutsideComponent"
    >
        <button
            aria-label="Previous month"
            class="absolute left-0 top-0 mx-2 my-3 overflow-hidden rounded px-2 py-1 transition duration-300"
            data-role-navigation="true"
            v-bind:class="[
                !isPrevAllowed
                    ? 'cursor-default text-gray-300 dark:text-gray-500'
                    : 'cursor-pointer border-black text-black hover:bg-gray-100 hover:shadow-xl dark:text-gray-100 dark:hover:bg-gray-200 hover:dark:text-gray-800',
            ]"
            @click="prevMonth"
            type="button"
        >
            <Ripple v-show="isPrevAllowed" ripple-class="bg-gray-300 dark:bg-gray-500" class="z-10"></Ripple>
            <LeftArrowIcon class="h-6 w-6 fill-current"></LeftArrowIcon>
        </button>
        <button
            aria-label="Next month"
            data-role-navigation="true"
            class="absolute right-0 top-0 mx-2 my-3 overflow-hidden rounded px-2 py-1 transition duration-300"
            v-bind:class="[
                !isNextAllowed
                    ? 'cursor-default text-gray-300 dark:text-gray-500'
                    : 'cursor-pointer border-black text-black hover:bg-gray-100 hover:shadow-xl dark:text-gray-100 dark:hover:bg-gray-200 hover:dark:text-gray-800',
            ]"
            @click="nextMonth"
            type="button"
        >
            <Ripple v-show="isNextAllowed" ripple-class="bg-gray-300 dark:bg-gray-500" class="z-10"></Ripple>
            <LeftArrowIcon class="h-6 w-6 rotate-180 fill-current"></LeftArrowIcon>
        </button>
        <div class="flex">
            <div v-for="month in months" :key="month.id" class="flex-1 p-4 pb-0 dark:text-white">
                <div class="text-center font-bold">
                    <span>{{ month.monthName }}</span>
                    &nbsp;<span class="font-light">{{ month.year }}</span>
                </div>
                <div class="mb-2 mt-4 flex flex-1 border-b border-gray-400">
                    <div
                        v-for="(dayInitial, weekNameIndex) in daysShort"
                        :key="weekNameIndex"
                        class="flex flex-1 flex-col text-center text-sm"
                        :class="{ weekend: weekNameIndex === 0 || weekNameIndex === 6 }"
                        :aria-label="days[weekNameIndex]"
                    >
                        {{ dayInitial }}
                    </div>
                </div>
                <div class="flex flex-col space-y-1">
                    <div
                        v-for="(week, weekIndex) in month.weeks"
                        :key="week.id"
                        class="flex"
                        :class="{ 'first-week': weekIndex === 0, 'last-week': weekIndex === month.weeks.length - 1 }"
                    >
                        <div v-for="day in week.days" :key="day.id" class="w-1/7 text-center text-sm">
                            <div class="day-content empty p-1" v-if="day.dayNumber === 0"></div>
                            <div
                                v-else
                                class="day-content rounded p-1 outline-none"
                                role="button"
                                tabindex="-1"
                                :class="[
                                    isOnDisabledRange(day.date) ? 'day-disabled' : 'day-selectable',
                                    {
                                        'day-selected':
                                            clonedModelValue !== null &&
                                            day.date !== undefined &&
                                            isSameDay(clonedModelValue, day.date),
                                    },
                                ]"
                                @click="manageClick(day, isOnDisabledRange(day.date))"
                                :aria-label="day.formatted"
                                :title="day.formatted"
                            >
                                {{ day.dayNumber }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="my-3 flex justify-center space-x-2">
            <div class="flex items-center">
                <BaseSelectField
                    label="Hour"
                    v-model.number="hour"
                    :hide-label="true"
                    class="w-16"
                    size="small"
                    :options="hourOptions"
                ></BaseSelectField>
            </div>
            <div class="flex items-center space-x-2">
                <BaseSelectField
                    label="Hour"
                    v-model.number="minute"
                    class="w-16"
                    size="small"
                    :hide-label="true"
                    :options="minuteOptions"
                ></BaseSelectField>
            </div>
            <div class="flex items-center space-x-2">
                <BaseSelectField
                    label="Hour"
                    v-model="ampm"
                    class="w-16"
                    :size="'small'"
                    :hide-label="true"
                    :options="[
                        { label: 'AM', value: 'am' },
                        { label: 'PM', value: 'pm' },
                    ]"
                ></BaseSelectField>
            </div>
        </div>
        <div class="my-4 flex items-center justify-center">
            <button
                @click="emit('close')"
                type="button"
                class="relative block w-10/12 overflow-hidden rounded bg-green-500 py-1 text-sm text-white transition duration-200 hover:shadow-lg"
            >
                <Ripple></Ripple>
                <span>Close</span>
            </button>
        </div>
    </div>
</template>
<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { type PropType, ref, computed, watch } from 'vue'
import {
    isBefore,
    isAfter,
    format,
    getDaysInMonth,
    differenceInMonths,
    startOfMonth,
    subMonths,
    addMonths,
    isSameMonth,
    isSameDay,
} from 'date-fns'
import vClickOutside from '@/directives/ClickOutside'
import { getId } from '@/util/Util'
import type { Month } from '@/entity/Month'
import type { Day } from '@/entity/Day'
import type { Week } from '@/entity/Week'
import { onBeforeMount } from 'vue'
import LeftArrowIcon from '@/icons/LeftArrowIcon.vue'
import BaseSelectField from '@/components/BaseSelectField.vue'
import Ripple from '@/components/Ripple.vue'

const props = defineProps({
    numberOfMonthsToShow: {
        type: Number as PropType<number>,
        default: 1,
        required: false,
        validator(value: number): boolean {
            return value < 4
        },
    },
    max: {
        type: Date as PropType<Date>,
        default: () => new Date(),
        required: false,
    },
    min: {
        type: Date as PropType<Date>,
        default: () => subMonths(new Date(), 18),
        required: false,
    },
    modelValue: {
        type: null as unknown as PropType<Date | null>,
        default: null,
        required: false,
    },
})

const emit = defineEmits<{
    (e: 'update:model-value', value: Date | null): void
    (e: 'clickedOutside', value: HTMLElement): void
    (e: 'close'): void
}>()

const monthNames = ref<Array<string>>([
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December',
])

const days = ref<Array<string>>(['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'])
const daysShort = ref<Array<string>>(['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'])
const containerStyle = ref<Record<string, string>>({
    width: `${props.numberOfMonthsToShow * 20.0625}rem`,
})
const clonedModelValue = ref<Date | null>(props.modelValue === null ? null : new Date(props.modelValue))
const inferredHour = clonedModelValue.value?.getHours() ?? new Date().getHours()
const hour = ref<number>(inferredHour < 13 ? inferredHour : inferredHour - 12)
const minute = ref<number>(clonedModelValue.value?.getMinutes() ?? new Date().getMinutes())
const ampm = ref<'am' | 'pm'>(inferredHour < 13 ? 'am' : 'pm')
const visibleMonths = ref<number>(0)
const monthToStart = ref<Date>(new Date())
const minuteOptions: Array<{ label: string; value: number }> = (() => {
    const output: Array<{ label: string; value: number }> = []
    for (let s = 0; s < 60; s += 1) {
        output.push({
            label: s < 10 ? `0${s}` : s.toString(10),
            value: s,
        })
    }
    return output
})()

const hourOptions: Array<{ label: string; value: number }> = (() => {
    const output: Array<{ label: string; value: number }> = []
    for (let s = 1; s < 13; s += 1) {
        output.push({
            label: s < 10 ? `0${s}` : s.toString(10),
            value: s,
        })
    }
    return output
})()

const minimumDateAllowed = computed<Date>(() => {
    if (clonedModelValue.value === null) {
        return props.min
    }

    if (isBefore(props.min, clonedModelValue.value)) {
        return props.min
    }

    return props.min
})

const isPrevAllowed = computed<boolean>(() => {
    if (minimumDateAllowed.value === null) {
        return true
    }
    return !isSameMonth(months.value[0].firstDateOfMonth, minimumDateAllowed.value)
})

const isNextAllowed = computed<boolean>(() => {
    if (props.max === null) {
        return true
    }
    return (
        !isSameMonth(months.value[months.value.length - 1].firstDateOfMonth, props.max) &&
        !isAfter(months.value[months.value.length - 1].firstDateOfMonth, props.max)
    )
})

const months = ref<Month[]>([])

const combinedDateTime = computed<Date | null>(() => {
    if (clonedModelValue.value === null) {
        return null
    }
    const dt: Date = new Date(clonedModelValue.value)
    let parsedHour = parseInt(hour.value.toString(), 10)
    parsedHour = parsedHour === 12 ? 0 : parsedHour
    const h = ampm.value === 'am' ? parsedHour : parsedHour + 12
    dt.setHours(h)
    dt.setMinutes(parseInt(minute.value.toString(), 10))
    dt.setSeconds(0)
    return dt
})

watch(combinedDateTime, (newCombinedDateTime: Date | null) => {
    emit('update:model-value', newCombinedDateTime)
})

function setUpDates() {
    // Check to see if the month difference between max allowed date to show and min allowed date to
    // show is less than the month that we want to show.
    const monthDifference = differenceInMonths(props.max, props.min)

    visibleMonths.value = monthDifference < props.numberOfMonthsToShow ? monthDifference : props.numberOfMonthsToShow
    // If the difference is zero then we must show at least 1 month.
    visibleMonths.value = visibleMonths.value === 0 ? 1 : visibleMonths.value

    monthToStart.value = clonedModelValue.value === null ? new Date() : clonedModelValue.value
}

function isOnDisabledRange(date: Date | undefined): boolean {
    if (date === undefined) {
        return false
    }
    if (isBefore(date, minimumDateAllowed.value)) {
        return true
    }

    return isAfter(date, props.max)
}

function getBlankDayObject(): Day {
    return { dayNumber: 0, id: `day-${getId()}` }
}

function getWeeks(date: Date): Array<Week> {
    const daysInMonth = getDaysInMonth(date)
    const year = parseInt(format(date, 'yyyy'), 10)
    const month = format(date, 'M')
    const monthIndex = parseInt(month, 10) - 1
    const firstDayInWeek = parseInt(format(date, 'e'), 10)
    const weeks: Week[] = []

    let week: Day[] = []

    for (let s = 1; s < firstDayInWeek; s += 1) {
        week.push(getBlankDayObject())
    }

    for (let d = 0; d < daysInMonth; d += 1) {
        const isLastDayInMonth = d >= daysInMonth - 1
        const dayNumber = d + 1

        week.push({
            dayNumber,
            month,
            year,
            date: new Date(year, monthIndex, dayNumber),
            id: `day-${d}-${month}-${year}`,
            formatted: `${days.value[week.length]}, ${monthNames.value[monthIndex]} ${dayNumber}, ${year}`,
        })

        if (week.length === 7) {
            weeks.push({ days: week, id: getId() })
            week = []
        } else if (isLastDayInMonth) {
            for (let i = 0; i < 7 - week.length; i += 1) {
                week.push(getBlankDayObject())
            }

            weeks.push({ days: week, id: getId() })
            week = []
        }
    }

    return weeks
}

function getMonth(date: Date): Month {
    const firstDateOfMonth = startOfMonth(date)
    const year = format(date, 'yyyy')
    const monthNumber = parseInt(format(date, 'M'), 10)
    const monthName = monthNames.value[monthNumber - 1]
    return {
        year,
        firstDateOfMonth,
        monthName,
        monthNumber,
        id: `month-${monthNumber}${year}`,
        weeks: getWeeks(firstDateOfMonth),
    }
}

function generateMonths() {
    for (let i = 0; i < props.numberOfMonthsToShow; i += 1) {
        months.value.push(getMonth(addMonths(monthToStart.value, i)))
    }
}

function nextMonth() {
    if (!isNextAllowed.value) {
        return
    }

    monthToStart.value = addMonths(monthToStart.value, 1)
    months.value.push(getMonth(addMonths(months.value[months.value.length - 1].firstDateOfMonth, 1)))
    months.value.shift()
}

function prevMonth() {
    if (!isPrevAllowed.value) {
        return
    }

    monthToStart.value = subMonths(monthToStart.value, 1)
    if (months.value.length === 1) {
        months.value = [getMonth(subMonths(months.value[0].firstDateOfMonth, 1))]
    } else {
        months.value.pop()
        months.value.unshift(getMonth(subMonths(months.value[0].firstDateOfMonth, 1)))
    }
}

function manageClick(day: Day, disabledDate: boolean) {
    if (disabledDate || day.date === undefined) {
        return
    }
    const dt = new Date(day.date)
    clonedModelValue.value = dt
}

function clickedOutsideComponent(event: MouseEvent) {
    emit('clickedOutside', event.target as HTMLElement)
}

onBeforeMount(() => {
    setUpDates()
    generateMonths()
})
</script>
<style lang="postcss" scoped>
.day-disabled {
    @apply text-gray-400 hover:bg-white dark:text-gray-500 dark:hover:bg-gray-800;
}

.day-disabled:hover {
    cursor: default;
}

.day-disabled.day-selected {
    @apply bg-gray-500 text-white hover:bg-gray-300 hover:text-white;
}

.day-selectable {
    @apply cursor-pointer text-gray-800 hover:bg-green-50 dark:text-gray-100 dark:hover:bg-gray-100 dark:hover:text-gray-900;
}
.day-selectable.day-selected {
    @apply cursor-pointer bg-green-500 text-white;
}
</style>
