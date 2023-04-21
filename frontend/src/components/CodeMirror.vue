<template>
    <div class="flex flex-col">
        <div
            :id="id"
            class="holder overflow-auto border bg-white text-sm shadow dark:border-gray-700 dark:bg-gray-700"
        ></div>
        <div class="clear-both flow-root p-2 pr-0">
            <div class="float-left text-red-500" v-if="hasError(errors, 'schema')">
                {{ getError(errors, 'schema') }}
            </div>
            <button
                v-if="!props.readOnly && schemaType !== 'PROTOBUF'"
                class="relative float-right block overflow-hidden rounded border border-green-500 px-3 py-1 text-black transition duration-200 ease-linear hover:bg-green-50 hover:shadow-lg dark:border-gray-600 dark:text-white dark:hover:bg-gray-600 dark:hover:shadow-black"
                @click="prettifyContent"
                type="button"
            >
                <ripple ripple-class="bg-green-100 dark:bg-gray-700"></ripple>Format code
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
import { basicSetup } from 'codemirror'
import { EditorView, keymap } from '@codemirror/view'
import { indentWithTab } from '@codemirror/commands'
import { EditorState, Compartment } from '@codemirror/state'
import { StreamLanguage } from '@codemirror/language'
import { protobuf } from '@codemirror/legacy-modes/mode/protobuf'
import { json, jsonParseLinter } from '@codemirror/lang-json'
import { lintGutter, linter } from '@codemirror/lint'
import { indentUnit } from '@codemirror/language'
import { getId } from '@/util/Util'
import { defineEmits, defineProps, onMounted, onUnmounted, type PropType, ref, watch } from 'vue'
import { getError, hasError } from '@/validators/SchemaValidator'
import Ripple from '@/components/Ripple.vue'

const props = defineProps({
    readOnly: {
        type: Boolean as PropType<boolean>,
        default: false,
    },
    value: {
        type: String as PropType<string>,
        required: true,
    },
    editorClass: {
        type: String as PropType<string>,
        required: false,
        default: 'h-96 overflow-scroll !important',
    },
    schemaType: {
        type: String as PropType<'JSON' | 'AVRO' | 'PROTOBUF'>,
        required: true,
    },
    errors: {
        required: true,
        type: Object as PropType<Map<string, string>>,
    },
})

const emit = defineEmits<{ (e: 'change', value: string): void }>()

watch(
    () => props.readOnly,
    (newReadOnly) => changeMode(newReadOnly),
    { immediate: false }
)

// watch(
//     () => props.modelValue,
//     (newModelValue) => {
//         if (props.readOnly) {
//             setDoc(newModelValue)
//         }
//     },
//     { immediate: false }
// )

watch(
    () => props.value,
    (newValue) => setValue(newValue),
    { immediate: false }
)

const id = ref<string>(getId())
const editorContainer = ref<Element>()
const editor = ref<EditorView>()
let language = new Compartment()
let tabSize = new Compartment()
let readOnly = new Compartment()

const languageSettings =
    props.schemaType.toString() === 'PROTOBUF'
        ? [language.of(StreamLanguage.define(protobuf))]
        : [language.of(json()), linter(jsonParseLinter())]

let state = EditorState.create({
    doc: props.value,
    extensions: [
        ...[
            basicSetup,
            lintGutter(),
            tabSize.of(EditorState.tabSize.of(4)),
            indentUnit.of('    '),
            keymap.of([indentWithTab]),
            readOnly.of(EditorState.readOnly.of(props.readOnly)),
            EditorView.updateListener.of((viewUpdate) => {
                if (viewUpdate.docChanged) {
                    if (!props.readOnly) {
                        emit('change', viewUpdate.state.doc.toString())
                    }
                }
            }),
        ],
        ...languageSettings,
    ],
})

function setValue(newValue: string) {
    if (props.readOnly) {
        setDoc(newValue)
    }
}

onMounted(() => {
    editorContainer.value = document.querySelector('#' + id.value) as Element
    editor.value = new EditorView({
        state,
        parent: editorContainer.value,
    })
})

onUnmounted(() => {
    editor.value?.destroy()
    editor.value = undefined
    editorContainer.value = undefined
})

function changeMode(enable: boolean) {
    editor.value?.dispatch({
        effects: readOnly.reconfigure(EditorState.readOnly.of(enable)),
    })
}

function setDoc(value: string) {
    editor.value?.dispatch({
        changes: {
            from: 0,
            to: editor.value.state.doc.length,
            insert: value,
        },
    })
}

function prettifyContent() {
    const content = editor?.value?.state.doc.toString() ?? ''

    try {
        const parsedContent = JSON.parse(content)
        if (parsedContent && typeof parsedContent === 'object') {
            setDoc(JSON.stringify(parsedContent, null, 4))
        }
    } catch (e) {
        //
    }
}
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
