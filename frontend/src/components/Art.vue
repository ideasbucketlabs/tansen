<template>
    <div class="relative flex items-center justify-center">
        <div
            :class="[pickedShapeClass]"
            class="absolute h-full w-full bg-green-50 transition-all duration-200 ease-in-out dark:bg-slate-900"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-100 transition-all duration-200 ease-in-out dark:bg-slate-800"
            style="width: 90%; height: 90%"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-200 transition-all duration-200 ease-in-out dark:bg-slate-700"
            style="width: 80%; height: 80%"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-300 transition-all duration-200 ease-in-out dark:bg-slate-600"
            style="width: 70%; height: 70%"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-400 transition-all duration-200 ease-in-out dark:bg-slate-500"
            style="width: 60%; height: 60%"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-500 transition-all duration-200 ease-in-out dark:bg-slate-400"
            style="width: 50%; height: 50%"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-600 transition-all duration-200 ease-in-out dark:bg-slate-300"
            style="width: 40%; height: 40%"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-700 transition-all duration-200 ease-in-out dark:bg-slate-200"
            style="width: 30%; height: 30%"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-800 transition-all duration-200 ease-in-out dark:bg-slate-100"
            style="width: 20%; height: 20%"
        ></div>
        <div
            :class="[pickedShapeClass]"
            class="absolute bg-green-900 transition-all duration-200 ease-in-out dark:bg-slate-50"
            style="width: 10%; height: 10%"
        ></div>
    </div>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { computed, defineProps, type PropType } from 'vue'

const props = defineProps({
    type: {
        type: Number as PropType<number>,
        default() {
            return Math.floor(Math.random() * 10)
        },
        required: false,
        validator(value: number) {
            return value >= 0 && value < 10
        },
    },
})

const shapeClasses: Array<string> = [
    'decagon',
    'triangle',
    'rhombus',
    'rabbet',
    'nonagon',
    'octagon',
    'heptagon',
    'pentagon',
    'bevel',
    'star',
]

const pickedShapeClass = computed<string>(() => {
    return shapeClasses[props.type]
})
</script>

<style scoped>
.decagon {
    clip-path: polygon(50% 0%, 80% 10%, 100% 35%, 100% 70%, 80% 90%, 50% 100%, 20% 90%, 0% 70%, 0% 35%, 20% 10%);
}

.triangle {
    clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
}

.rhombus {
    clip-path: polygon(50% 0%, 100% 50%, 50% 100%, 0% 50%);
}

.rabbet {
    clip-path: polygon(
        0% 15%,
        15% 15%,
        15% 0%,
        85% 0%,
        85% 15%,
        100% 15%,
        100% 85%,
        85% 85%,
        85% 100%,
        15% 100%,
        15% 85%,
        0% 85%
    );
}

.nonagon {
    clip-path: polygon(50% 0%, 83% 12%, 100% 43%, 94% 78%, 68% 100%, 32% 100%, 6% 78%, 0% 43%, 17% 12%);
}

.octagon {
    clip-path: polygon(30% 0%, 70% 0%, 100% 30%, 100% 70%, 70% 100%, 30% 100%, 0% 70%, 0% 30%);
}

.heptagon {
    clip-path: polygon(50% 0%, 90% 20%, 100% 60%, 75% 100%, 25% 100%, 0% 60%, 10% 20%);
}

.pentagon {
    clip-path: polygon(50% 0%, 100% 38%, 82% 100%, 18% 100%, 0% 38%);
}

.bevel {
    clip-path: polygon(20% 0%, 80% 0%, 100% 20%, 100% 80%, 80% 100%, 20% 100%, 0% 80%, 0% 20%);
}

.star {
    clip-path: polygon(50% 0%, 61% 35%, 98% 35%, 68% 57%, 79% 91%, 50% 70%, 21% 91%, 32% 57%, 2% 35%, 39% 35%);
}
</style>
