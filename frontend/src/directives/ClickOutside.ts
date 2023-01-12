/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { DirectiveBinding } from 'vue'

interface ExtendedHTMLElement extends HTMLElement {
    clickOutsideEvent(event: Event): void
}

export default {
    beforeMount(el: ExtendedHTMLElement, binding: DirectiveBinding) {
        // eslint-disable-next-line no-param-reassign,func-names
        el.clickOutsideEvent = function (event: Event) {
            if (!(el === event.target || el.contains(event.target as Node))) {
                binding.value(event, el)
            }
        }
        document.body.addEventListener('click', el.clickOutsideEvent)
    },
    unmounted(el: ExtendedHTMLElement) {
        document.body.removeEventListener('click', el.clickOutsideEvent)
    },
}
