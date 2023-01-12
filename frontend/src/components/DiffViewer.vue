<template>
    <div class="relative flex flex-col flex-wrap dark:bg-gray-700">
        <div class="flex h-12 flex-grow flex-row overflow-y-auto">
            <div class="flex flex-col rounded-l">
                <template v-for="line in codeLines" :key="'code-line-' + line">
                    <div v-if="line.line === -1" class="px-4">&nbsp;</div>
                    <div
                        v-else
                        class="border-r bg-gray-100 pl-2 pr-6 text-right text-gray-700 dark:border-gray-800 dark:bg-gray-800 dark:text-yellow-300"
                    >
                        {{ line.line }}
                    </div>
                </template>
            </div>
            <div class="flex flex-1 flex-col">
                <div v-for="line in codeLines" :key="'code-' + line">
                    <pre v-if="line.condition === 'equal'" class="equal pl-1">{{ line.code }}</pre>
                    <div v-else-if="line.condition === 'insert'" class="relative bg-green-100 dark:bg-green-500">
                        <div class="absolute inset-y-0 left-0 pl-1">+</div>
                        <pre :title="'Added in: ' + leftVersionName" class="pl-1">{{ line.code }}</pre>
                    </div>
                    <div v-else class="relative bg-red-200 dark:bg-red-500">
                        <div class="absolute inset-y-0 left-0 pl-1">-</div>
                        <pre class="pl-1" :title="'Removed from: ' + leftVersionName">{{ line.code }}</pre>
                    </div>
                </div>
            </div>
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
import { DIFF_DELETE, DIFF_EQUAL, DIFF_INSERT, diff_match_patch as DiffMatchPatch } from 'diff-match-patch'
import { defineProps, type PropType, ref, watch } from 'vue'
import type { DiffLine } from '@/entity/DiffLine'

const differ = new DiffMatchPatch()

const props = defineProps({
    leftVersion: {
        type: String as PropType<string>,
        required: true,
    },
    rightVersion: {
        type: String as PropType<string>,
        required: true,
    },
    leftVersionName: {
        type: String as PropType<string>,
        required: true,
    },
    rightVersionName: {
        type: String as PropType<string>,
        required: true,
    },
    mode: {
        type: String as PropType<'split' | 'unified'>,
        required: true,
    },
})

const codeLines = ref<Array<DiffLine>>(generateDiff(props.leftVersion, props.rightVersion))

watch(
    () => props.leftVersion,
    () => {
        refreshDiff()
    },
    { immediate: false }
)

watch(
    () => props.rightVersion,
    () => {
        refreshDiff()
    },
    { immediate: false }
)

function refreshDiff() {
    codeLines.value = generateDiff(props.leftVersion, props.rightVersion)
}

function generateDiff(left: string, right: string): Array<DiffLine> {
    const a = differ.diff_linesToChars_(left.trim(), right.trim())
    const lineText1 = a.chars1
    const lineText2 = a.chars2
    const lineArray = a.lineArray
    const diffs = differ.diff_main(lineText1, lineText2, true)
    differ.diff_charsToLines_(diffs, lineArray)

    const lineNumbers: Array<DiffLine> = []
    let lineCount = 1
    for (let diff of diffs) {
        diff[1] = diff[1].replace(/\n$/, '').replace(/\n/g, '<tansen-differ/>')

        if (diff[0] === DIFF_EQUAL) {
            const lines = diff[1].split('<tansen-differ/>')

            for (let line of lines) {
                lineNumbers.push({ line: lineCount, condition: 'equal', code: line })
                lineCount = lineCount + 1
            }
        }

        if (diff[0] === DIFF_DELETE) {
            const lines = diff[1].split('<tansen-differ/>')

            for (let line of lines) {
                lineNumbers.push({ line: -1, condition: 'delete', code: line })
            }
        }

        if (diff[0] === DIFF_INSERT) {
            const lines = diff[1].split('<tansen-differ/>')

            for (let line of lines) {
                lineNumbers.push({ line: lineCount, condition: 'insert', code: line })
                lineCount = lineCount + 1
            }
        }
    }

    return lineNumbers
}
</script>
