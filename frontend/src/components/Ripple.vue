<template>
    <span ref="root" @click="onClick" class="absolute inset-0 block h-full w-full"></span>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { type PropType, ref } from 'vue'
const props = defineProps({
    rippleClass: {
        type: String as PropType<string>,
        default: 'bg-white dark:bg-gray-900 opacity-25',
        required: false,
    },
})

const root = ref<Element | null>(null)

function onClick(e: MouseEvent) {
    // On right click do not want to do anything.
    if (e.button === 2) {
        e.preventDefault()

        return false
    }

    const rippleElements = root.value?.getElementsByClassName('ripple') ?? []

    let ripple: HTMLElement

    if (rippleElements.length === 0) {
        ripple = document.createElement('span')
        ripple.classList.add('ripple')
        props.rippleClass.split(' ').forEach((it) => ripple.classList.add(it))
        // ripple.style.setProperty('background', 'rgba(255, 255, 255, 0.5)')
        root.value?.prepend(ripple)
    } else {
        ripple = rippleElements[0] as HTMLElement
    }

    ripple.classList.remove('ripple-animation')

    const size = Math.max((root.value as HTMLElement).clientWidth, (root.value as HTMLElement).clientHeight)

    ripple.style.setProperty('width', `${size}px`)
    ripple.style.setProperty('height', `${size}px`)

    const rippleX = e.pageX - (root.value as HTMLElement).getBoundingClientRect().left - size / 2
    const rippleY = e.pageY - (root.value as HTMLElement).getBoundingClientRect().top - size / 2

    ripple.style.setProperty('top', `${rippleY}px`)
    ripple.style.setProperty('left', `${rippleX}px`)
    ripple.classList.add('ripple-animation')

    setTimeout(() => {
        ripple.remove()
    }, 800)

    return true
}
</script>
