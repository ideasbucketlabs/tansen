<template>
    <Dialog
        :title="'Content for partition #' + partition + ' and offset #' + offset"
        width="w-full xl:w-11/12"
        ref="dialog"
        @overlay-clicked="onClick"
    >
        <div class="flex max-h-72 flex-col md:max-h-96 lg:max-h-96 xl:max-h-[35rem]">
            <div
                ref="editorContainer"
                class="holder overflow-auto border bg-white text-sm shadow dark:border-gray-700 dark:bg-gray-700"
            ></div>
        </div>
        <div
            class="flex h-12 items-center justify-center border-t border-green-200 bg-green-100 dark:border-gray-800 dark:bg-gray-700"
        >
            <button
                @click="onClick"
                class="relative mx-2 h-9 w-6/12 overflow-hidden rounded bg-green-500 font-semibold text-white transition duration-500 hover:bg-green-600 hover:shadow-lg focus:outline-none focus:ring-0 xl:w-2/12"
            >
                <ripple ripple-class="bg-white dark:bg-gray-700 opacity-25" />
                Close
            </button>
        </div>
    </Dialog>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { basicSetup } from 'codemirror'
import { EditorView, keymap } from '@codemirror/view'
import { indentWithTab } from '@codemirror/commands'
import { EditorState, Compartment } from '@codemirror/state'
import { json } from '@codemirror/lang-json'
import { lintGutter } from '@codemirror/lint'
import { indentUnit } from '@codemirror/language'
import { onUnmounted, type PropType, ref, watch } from 'vue'
import Dialog from '@/components/Dialog.vue'
import eventBus from '@/util/EventBus'
import { ApplicationEventTypes } from '@/entity/ApplicationEventTypes'
import Ripple from '@/components/Ripple.vue'

const props = defineProps({
    value: {
        type: String as PropType<string>,
        required: true,
    },
    partition: {
        type: Number as PropType<number>,
        required: true,
    },
    offset: {
        type: Number as PropType<number>,
        required: true,
    },
})

const dialog = ref<typeof Dialog | null>(null)
const editorContainer = ref<Element | null>(null)
const editor = ref<EditorView>()
let language = new Compartment()
let tabSize = new Compartment()
let readOnly = new Compartment()

const languageSettings = [language.of(json())]
const emit = defineEmits<{
    (e: 'close'): void
}>()

let state = EditorState.create({
    doc: props.value,
    extensions: [
        ...[
            basicSetup,
            lintGutter(),
            tabSize.of(EditorState.tabSize.of(4)),
            indentUnit.of('    '),
            keymap.of([indentWithTab]),
            readOnly.of(EditorState.readOnly.of(true)),
        ],
        ...languageSettings,
    ],
})

watch(editorContainer, async (newEditorContainer: Element | null) => {
    if (newEditorContainer !== null) {
        editor.value = new EditorView({
            state,
            parent: editorContainer.value as Element,
        })
    }
})

function onClick() {
    eventBus.once(ApplicationEventTypes.DIALOG_CLOSE, () => {
        emit('close')
    })

    if (dialog.value !== null) {
        dialog.value.hide()
    }
}

onUnmounted(() => {
    editor.value?.destroy()
    editor.value = undefined
})
</script>

<style scoped>
/* https://vuejs.org/api/sfc-css-features.html#scoped-css */
.holder :deep(.cm-wrap),
.holder :deep(.cm-editor) {
    @apply overflow-auto text-base;
    font-size: 16px; /* Had to explicitly set this since default text-base was not working in mobile and Safari. */
}

.holder :deep(.cm-scroller) {
    @apply overflow-auto;
}

.holder :deep(.ͼb) {
    @apply text-sky-500;
}

.holder :deep(.cm-line) {
    @apply font-normal !important;
}

.holder :deep(.cm-gutterElement) {
    @apply font-normal !important;
}

.holder :deep(.cm-selectionBackground) {
    @apply bg-blue-100 !important;
}

@media (prefers-color-scheme: dark) {
    .holder :deep(.cm-tooltip) {
        @apply bg-gray-900 !important;
    }
    .holder :deep(.cm-activeLine) {
        @apply bg-gray-900 !important;
    }
    .holder :deep(.cm-gutters) {
        @apply border-gray-700  bg-gray-800 !important;
    }
    .holder :deep(.cm-gutterElement) {
        @apply text-yellow-300 !important;
    }
    .holder :deep(.cm-cursor) {
        @apply border-gray-100 !important;
    }
    .holder :deep(.cm-gutterElement.cm-activeLineGutter) {
        @apply bg-gray-900 !important;
    }
    .holder :deep(.cm-selectionBackground) {
        @apply bg-sky-800 !important;
    }
    .holder :deep(.ͼc) {
        @apply text-cyan-300 !important;
    }
    .holder :deep(.ͼd) {
        @apply text-green-300 !important;
    }
    .holder :deep(.ͼe) {
        @apply text-yellow-500 !important;
    }
    .holder :deep(.ͼb) {
        @apply text-purple-400;
    }
}
</style>
