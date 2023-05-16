<template>
    <FlyInPanel
        :loading="loading"
        title="Edit topic"
        @close="$emit('close')"
        ref="flyInPanel"
        width-class-configuration="w-full md:w-10/12 lg:w-11/12 xl:w-7/12"
    >
        <div
            class="flex flex-grow flex-col overflow-hidden rounded-b dark:border dark:border-gray-800 dark:text-gray-100"
        >
            <form class="flex flex-grow flex-col items-stretch" @submit.prevent="submit" novalidate>
                <div
                    class="flex h-32 flex-grow flex-col space-y-4 overflow-y-auto bg-white p-2 dark:bg-gray-900 md:p-4"
                >
                    <div class="flex flex-1 space-x-4">
                        <div class="flex flex-1 flex-col space-y-4">
                            <div
                                class="flex items-center space-x-2 rounded border border-green-100 p-2 dark:border-gray-400"
                            >
                                <div class="flex w-12 items-center justify-center">
                                    <alert-icon class="h-10 w-10 text-black"></alert-icon>
                                </div>
                                <div>
                                    Be careful not to get your topic in a bad state. You have full control of every
                                    topic configuration below that can be edited.
                                </div>
                            </div>
                            <fieldset class="flex flex-col space-y-4">
                                <legend class="font-bold">General</legend>
                                <div>
                                    <div class="mb-1 block text-left text-black dark:text-gray-100">Topic Name</div>
                                    <div
                                        class="block cursor-not-allowed rounded border border-gray-400 bg-gray-100 px-2 py-3 text-black dark:bg-gray-600 dark:text-gray-100"
                                    >
                                        {{ topicDetails.topic }}
                                    </div>
                                </div>
                                <div class="mb-1 block text-left text-black dark:text-gray-100">
                                    Number of Partition
                                </div>
                                <div
                                    class="block cursor-not-allowed rounded border border-gray-400 bg-gray-100 px-2 py-3 text-black dark:bg-gray-600 dark:text-gray-100"
                                >
                                    {{ topicDetails.numberOfPartition }}
                                </div>
                            </fieldset>
                            <div>
                                <fieldset class="mt-4 space-y-4">
                                    <legend class="font-bold">Storage</legend>
                                    <BaseSelectField
                                        label="Cleanup policy"
                                        :has-error="hasError('cleanup.policy')"
                                        v-model="topic['cleanup.policy']"
                                        :options="[
                                            { label: 'Delete', value: 'delete' },
                                            { label: 'Compact', value: 'compact' },
                                            { label: 'Compact, Delete', value: 'compact,delete' },
                                        ]"
                                        :info-tooltip="'Choose the retention policy to use on old log segments. Delete discards old segments when their retention time or size limit has been reached. Compact saves the last value for each unique key written to the topic.\u000A \u000ADefault value: delete'"
                                    >
                                        <div class="form-error text-sm text-red-500" v-if="hasError('cleanup.policy')">
                                            {{ getError('cleanup.policy') }}
                                        </div>
                                    </BaseSelectField>
                                    <div>
                                        <BaseInputField
                                            v-model="topic['retention.ms']"
                                            label="Retention time (in ms)"
                                            :has-error="hasError('retention.ms')"
                                            type="number"
                                            :info-tooltip="'Specify the amount of time to retain data.\u000ADefault value: 604800000 = 1 week'"
                                        >
                                            <div
                                                class="form-error text-sm text-red-500"
                                                v-if="hasError('retention.ms')"
                                            >
                                                {{ getError('retention.ms') }}
                                            </div>
                                        </BaseInputField>
                                        <div class="mt-2 flex space-x-2 text-xs md:text-sm lg:text-base">
                                            <div
                                                @click="topic['retention.ms'] = 43200000n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.ms'] === 43200000n ||
                                                    topic['retention.ms'].toString() === '43200000'
                                                "
                                                tabindex="0"
                                                data-value="43200000"
                                                :aria-labelledby="id + '1'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '1'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.ms'] === 43200000n ||
                                                        topic['retention.ms'].toString() === '43200000'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >12h</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.ms'] = 86400000n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.ms'] === 86400000n ||
                                                    topic['retention.ms'].toString() === '86400000'
                                                "
                                                tabindex="0"
                                                data-value="86400000"
                                                :aria-labelledby="id + '2'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '2'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.ms'] === 86400000n ||
                                                        topic['retention.ms'].toString() === '86400000'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >1d</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.ms'] = 172800000n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.ms'] === 172800000n ||
                                                    topic['retention.ms'].toString() === '172800000'
                                                "
                                                tabindex="0"
                                                data-value="172800000"
                                                :aria-labelledby="id + '3'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '3'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.ms'] === 172800000n ||
                                                        topic['retention.ms'].toString() === '172800000'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >2d</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.ms'] = 345600000n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.ms'] === 345600000n ||
                                                    topic['retention.ms'].toString() === '345600000'
                                                "
                                                tabindex="0"
                                                data-value="345600000"
                                                :aria-labelledby="id + '4'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '4'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.ms'] === 345600000n ||
                                                        topic['retention.ms'].toString() === '345600000'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500  dark:hover:shadow-black',
                                                    ]"
                                                    >4d</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.ms'] = 604800000n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.ms'] === 604800000n ||
                                                    topic['retention.ms'].toString() === '604800000'
                                                "
                                                tabindex="0"
                                                data-value="604800000"
                                                :aria-labelledby="id + '5'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '5'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.ms'] === 604800000n ||
                                                        topic['retention.ms'].toString() === '604800000'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >7d</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.ms'] = 1209600000n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.ms'] === 1209600000n ||
                                                    topic['retention.ms'].toString() === '1209600000'
                                                "
                                                tabindex="0"
                                                data-value="1209600000"
                                                :aria-labelledby="id + '6'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    class="rounded px-2 py-1"
                                                    :id="id + '6'"
                                                    :class="[
                                                        topic['retention.ms'] === 1209600000n ||
                                                        topic['retention.ms'].toString() === '1209600000'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500  dark:hover:shadow-black',
                                                    ]"
                                                    >2w</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.ms'] = -1n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.ms'] === -1n ||
                                                    topic['retention.ms'].toString() === '-1'
                                                "
                                                tabindex="0"
                                                data-value="-1"
                                                :aria-labelledby="id + '7'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    class="rounded px-2 py-1"
                                                    :id="id + '7'"
                                                    :class="[
                                                        topic['retention.ms'] === -1n ||
                                                        topic['retention.ms'].toString() === '-1'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >Infinite</label
                                                >
                                            </div>
                                        </div>
                                    </div>
                                    <div>
                                        <BaseInputField
                                            v-model="topic['retention.bytes']"
                                            label="Retention size (in bytes)"
                                            type="number"
                                            :has-error="hasError('retention.bytes')"
                                            :info-tooltip="'The maximum size a partition can grow to before it is discarded to free up space.\u000ADefault value: Infinite = -1'"
                                        >
                                            <div
                                                class="form-error text-sm text-red-500"
                                                v-if="hasError('retention.bytes')"
                                            >
                                                {{ getError('retention.bytes') }}
                                            </div>
                                        </BaseInputField>
                                        <div class="mt-2 flex flex-wrap space-x-2 text-xs md:text-sm lg:text-base">
                                            <div
                                                @click="topic['retention.bytes'] = 1048588n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.bytes'] === 1048588n ||
                                                    topic['retention.bytes'].toString() === '1048588'
                                                "
                                                tabindex="0"
                                                data-value="1048588"
                                                :aria-labelledby="id + '8'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '8'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.bytes'] === 1048588n ||
                                                        topic['retention.bytes'].toString() === '1048588'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >1MB</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.bytes'] = 10485760n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.bytes'] === 10485760n ||
                                                    topic['retention.bytes'].toString() === '10485760'
                                                "
                                                tabindex="0"
                                                data-value="10485760"
                                                :aria-labelledby="id + '9'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '2'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.bytes'] === 10485760n ||
                                                        topic['retention.bytes'].toString() === '10485760'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >10MB</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.bytes'] = 104857600n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.bytes'] === 104857600n ||
                                                    topic['retention.bytes'].toString() === '104857600'
                                                "
                                                tabindex="0"
                                                data-value="104857600"
                                                :aria-labelledby="id + '9'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '3'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.bytes'] === 104857600n ||
                                                        topic['retention.bytes'].toString() === '104857600'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >100MB</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.bytes'] = 1073741824n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.bytes'] === 1073741824n ||
                                                    topic['retention.bytes'].toString() === '1073741824'
                                                "
                                                tabindex="0"
                                                data-value="43200000"
                                                :aria-labelledby="id + '10'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '10'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.bytes'] === 1073741824n ||
                                                        topic['retention.bytes'].toString() === '1073741824'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500  dark:hover:shadow-black',
                                                    ]"
                                                    >1GB</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.bytes'] = 10737418240n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.bytes'] === 10737418240n ||
                                                    topic['retention.bytes'].toString() === '10737418240'
                                                "
                                                tabindex="0"
                                                data-value="10737418240"
                                                :aria-labelledby="id + '11'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    :id="id + '11'"
                                                    class="rounded px-2 py-1"
                                                    :class="[
                                                        topic['retention.bytes'] === 10737418240n ||
                                                        topic['retention.bytes'].toString() === '10737418240'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >10GB</label
                                                >
                                            </div>
                                            <div
                                                @click="topic['retention.bytes'] = -1n"
                                                @keyup.enter="topic['retention.bytes'] = -1n"
                                                role="radio"
                                                :aria-checked="
                                                    topic['retention.bytes'] === -1n ||
                                                    topic['retention.bytes'].toString() === '-1'
                                                "
                                                tabindex="0"
                                                data-value="43200000"
                                                :aria-labelledby="id + '13'"
                                                class="flex rounded border border-green-500 text-center dark:border-gray-600"
                                            >
                                                <label
                                                    class="rounded px-2 py-1"
                                                    :id="id + '13'"
                                                    :class="[
                                                        topic['retention.bytes'] === -1n ||
                                                        topic['retention.bytes'].toString() === '-1'
                                                            ? 'bg-green-500 text-white dark:bg-gray-700 dark:shadow-inner dark:shadow-gray-800'
                                                            : 'cursor-pointer bg-green-50 transition duration-200 ease-linear hover:shadow-lg dark:bg-gray-500 dark:hover:shadow-black',
                                                    ]"
                                                    >Infinite</label
                                                >
                                            </div>
                                        </div>
                                    </div>
                                    <BaseInputField
                                        v-model="topic['max.message.bytes']"
                                        label="Maximum message size in bytes"
                                        type="number"
                                        :has-error="hasError('max.message.bytes')"
                                        :info-tooltip="'The maximum message size that can be appended to this topic.\u000ADefault value: 1048588'"
                                    >
                                        <div
                                            class="form-error text-sm text-red-500"
                                            v-if="hasError('max.message.bytes')"
                                        >
                                            {{ getError('max.message.bytes') }}
                                        </div>
                                    </BaseInputField>
                                </fieldset>
                                <fieldset class="mt-4 flex space-y-4">
                                    <legend class="font-bold">Expert Setting(s)</legend>
                                    <div class="flex flex-1 flex-col">
                                        <div class="flex items-center space-x-2">
                                            <input
                                                type="checkbox"
                                                class="h-5 w-5 border-gray-300 text-blue-500 hover:border-blue-500 focus:ring-blue-500"
                                                id="tansen-ui-edit-topic-form-advanced-mode-activator"
                                                value="1"
                                                v-model="manuallyConfigureOtherSettings"
                                            />
                                            <label
                                                for="tansen-ui-edit-topic-form-advanced-mode-activator"
                                                class="cursor-pointer"
                                                >Manually configure other topic configuration options</label
                                            >
                                        </div>
                                        <div v-if="manuallyConfigureOtherSettings" class="flex flex-col space-y-4 pt-4">
                                            <template
                                                v-for="configuration in advanceModeKeys"
                                                :key="'advance-mode-' + configuration"
                                            >
                                                <BaseSelectField
                                                    :options="nodeOptions"
                                                    v-if="configuration === 'min.insync.replicas'"
                                                    cast-as="integer"
                                                    v-model="topic['min.insync.replicas']"
                                                    label="Minimum in-sync replica (min.insync.replicas)"
                                                    :has-error="hasError('min.insync.replicas')"
                                                >
                                                    <div
                                                        class="form-error text-sm text-red-500"
                                                        v-if="hasError('min.insync.replicas')"
                                                    >
                                                        {{ getError('min.insync.replicas') }}
                                                    </div>
                                                </BaseSelectField>
                                                <BaseSelectField
                                                    v-else-if="configuration === 'message.timestamp.type'"
                                                    v-model="topic[configuration]"
                                                    :label="configuration"
                                                    :info-tooltip="getTopicConfigurationDefinition(configuration)"
                                                    :options="[
                                                        { label: 'CreateTime', value: 'CreateTime' },
                                                        { label: 'LogAppendTime', value: 'LogAppendTime' },
                                                    ]"
                                                ></BaseSelectField>
                                                <BaseSelectField
                                                    v-else-if="configuration === 'compression.type'"
                                                    v-model="topic[configuration]"
                                                    :label="configuration"
                                                    :info-tooltip="getTopicConfigurationDefinition(configuration)"
                                                    :options="[
                                                        { label: 'Producer', value: 'producer' },
                                                        { label: 'Uncompressed', value: 'uncompressed' },
                                                        { label: 'ZSTD', value: 'zstd' },
                                                        { label: 'LZ4', value: 'lz4' },
                                                        { label: 'Snappy', value: 'snappy' },
                                                        { label: 'GZIP', value: 'gzip' },
                                                    ]"
                                                ></BaseSelectField>
                                                <BaseSelectField
                                                    v-else-if="typeof topic[configuration as keyof NewTopic] === 'boolean'"
                                                    v-model="topic[configuration as keyof NewTopic]"
                                                    :label="configuration"
                                                    :info-tooltip="getTopicConfigurationDefinition(configuration)"
                                                    :cast-as="'boolean'"
                                                    :options="[
                                                        { label: 'True', value: true },
                                                        { label: 'False', value: false },
                                                    ]"
                                                ></BaseSelectField>
                                                <BaseInputField
                                                    v-else
                                                    v-model="topic[configuration as keyof NewTopic]"
                                                    :label="configuration"
                                                    :has-error="hasError(configuration)"
                                                    :info-tooltip="getTopicConfigurationDefinition(configuration)"
                                                >
                                                    <div
                                                        class="form-error text-sm text-red-500"
                                                        v-if="hasError(configuration)"
                                                    >
                                                        {{ getError(configuration) }}
                                                    </div>
                                                </BaseInputField>
                                            </template>
                                        </div>
                                    </div>
                                </fieldset>
                            </div>
                        </div>
                        <div
                            class="hidden w-72 rounded bg-green-50/50 shadow shadow-green-50 dark:bg-gray-700 dark:shadow-black md:block"
                        >
                            <div class="rounded-t bg-green-400 p-2 text-white dark:bg-gray-600">
                                Topic configuration summary
                            </div>
                            <div class="flex flex-col space-y-3 p-2 text-sm lg:text-base">
                                <div class="truncate">
                                    <div>name</div>
                                    <div>
                                        <code>{{ topicDetails.topic }}</code>
                                    </div>
                                </div>
                                <div class="truncate">
                                    <div>partition</div>
                                    <div>
                                        <code>{{ topicDetails.numberOfPartition }}</code>
                                    </div>
                                </div>
                                <div
                                    v-for="configurationKey in configurationKeys"
                                    :key="configurationKey"
                                    class="truncate"
                                >
                                    <div>{{ configurationKey }}</div>
                                    <div>
                                        <code>{{ topic[configurationKey as keyof NewTopic] }}</code>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div
                    class="dark:shadow-top-black shadow-top-green-500 flex justify-center space-x-2 rounded-b bg-green-100 px-2 py-2 shadow-green-500 dark:bg-gray-700"
                >
                    <BaseButton
                        class="block h-10 w-6/12 lg:w-4/12"
                        type="submit"
                        label="Save"
                        :disabled="!haveChanges"
                    ></BaseButton>
                </div>
            </form>
        </div>
    </FlyInPanel>
</template>

<script setup lang="ts">
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import FlyInPanel from '@/components/FlyInPanel.vue'
import BaseButton from '@/components/BaseButton.vue'
import BaseInputField from '@/components/BaseInputField.vue'
import { computed, nextTick, onMounted, type PropType, ref, watch } from 'vue'
import BaseSelectField from '@/components/BaseSelectField.vue'
import { getId, getTopicConfigurationDefinition } from '@/util/Util'
import type { NewTopic } from '@/entity/NewTopic'
import { validate, convertToProperType } from '@/validators/NewTopicValidator'
import { useRoute } from 'vue-router'
import { clusterInformationStore } from '@/stores/ClusterInformationStore'
import type { TopicDetails } from '@/entity/TopicDetails'
import AlertIcon from '@/icons/AlertIcon.vue'
import { isEqual } from 'lodash'

const props = defineProps({
    loading: {
        type: Boolean as PropType<boolean>,
        required: false,
        default: false,
    },
    topicDetails: {
        type: Object as PropType<TopicDetails>,
        required: true,
    },
})
const clusterId = useRoute().params.clusterId as string
const store = clusterInformationStore()
const formIsDirty = ref<boolean>(false)
const errors = ref<Map<string, string>>(new Map<string, string>())
const id = ref<string>(getId() + '-synthetic-radio-')
const manuallyConfigureOtherSettings = ref<boolean>(false)
const topic = ref<NewTopic>(convertTopicToNewTopic(props.topicDetails))
const topicBeforeEdit = convertTopicToNewTopic(props.topicDetails)
const defaultValuedTopic = {
    'cleanup.policy': 'delete',
    'compression.type': 'producer',
    'delete.retention.ms': 86400000n,
    'file.delete.delay.ms': 60000n,
    'flush.messages': 9223372036854775807n,
    'flush.ms': 9223372036854775807n,
    'follower.replication.throttled.replicas': '',
    'index.interval.bytes': 4096,
    'leader.replication.throttled.replicas': '',
    'max.compaction.lag.ms': 9223372036854775807n,
    'max.message.bytes': 1048588,
    'message.downconversion.enable': true,
    'message.timestamp.difference.max.ms': 9223372036854775807n,
    'message.timestamp.type': 'CreateTime',
    'min.cleanable.dirty.ratio': 0.5,
    'min.compaction.lag.ms': 0n,
    'min.insync.replicas': topic.value['min.insync.replicas'],
    'replication.factor': props.topicDetails?.replicationFactor ?? 1,
    'retention.bytes': -1n,
    'retention.ms': 604800000n,
    'segment.bytes': 1073741824,
    'segment.index.bytes': 10485760,
    'segment.jitter.ms': 0n,
    'segment.ms': 604800000n,
    'unclean.leader.election.enable': false,
    name: props.topicDetails?.topic ?? '',
    partition: props.topicDetails?.numberOfPartition ?? 1,
    preallocate: false,
}

const mandatoryKeys = ref<string[]>(['name', 'partition'])
const preConfiguredDisplayedKeys = [
    ...mandatoryKeys.value,
    ...['retention.bytes', 'max.message.bytes', 'retention.ms', 'replication.factor', 'cleanup.policy'],
]

const configurationKeys = computed<string[]>(() =>
    Object.keys(topic.value)
        .filter((it) => !mandatoryKeys.value.includes(it))
        .sort()
)

const nodeOptions = ref<{ label: string; value: number }[]>([])

const advanceModeKeys = computed<string[]>(() =>
    Object.keys(topic.value)
        .filter((it) => !preConfiguredDisplayedKeys.includes(it))
        .sort()
)

const prunedTopic = computed<NewTopic>(() => convertToProperType(topic.value))

const haveChanges = computed<boolean>(() => !isEqual(topicBeforeEdit, prunedTopic.value))

const emit = defineEmits<{
    (e: 'close'): void
    (e: 'submit', newTopic: { oldRecord: NewTopic; newRecord: NewTopic }): void
}>()

function parseToBigInt(input: string | undefined, defaultValue: bigint): bigint {
    if (input === undefined) {
        return defaultValue
    }

    // eslint-disable-next-line no-undef
    return BigInt(input)
}

function parseInt(input: string | undefined, defaultValue: number): number {
    if (input === undefined) {
        return defaultValue
    }

    return Number.parseInt(input, 10)
}

function convertTopicToNewTopic(input: TopicDetails): NewTopic {
    const configurationMap = new Map<string, string>()
    input.configurations.forEach((it) => configurationMap.set(it.name, it.value))

    return {
        name: props.topicDetails.topic,
        partition: props.topicDetails.numberOfPartition,
        'replication.factor': props.topicDetails.replicationFactor,
        'cleanup.policy': (configurationMap.get('cleanup.policy') ?? 'delete') as unknown as
            | 'delete'
            | 'compact'
            | 'compact,delete',
        'compression.type': configurationMap.get('compression.type') ?? 'producer',
        'delete.retention.ms': parseToBigInt(configurationMap.get('delete.retention.ms'), 86400000n),
        'file.delete.delay.ms': parseToBigInt(configurationMap.get('file.delete.delay.ms'), 60000n),
        'flush.messages': parseToBigInt(configurationMap.get('flush.messages'), 9223372036854775807n),
        'flush.ms': parseToBigInt(configurationMap.get('flush.ms'), 9223372036854775807n),
        'follower.replication.throttled.replicas':
            configurationMap.get('follower.replication.throttled.replicas') ?? '',
        'index.interval.bytes': parseInt(configurationMap.get('index.interval.bytes'), 4096),
        'leader.replication.throttled.replicas': configurationMap.get('leader.replication.throttled.replicas') ?? '',
        'max.compaction.lag.ms': parseToBigInt(configurationMap.get('max.compaction.lag.ms'), 9223372036854775807n),
        'max.message.bytes': parseInt(configurationMap.get('max.message.bytes'), 1048588),
        'message.downconversion.enable':
            (configurationMap.get('message.downconversion.enable') ?? 'true').toLowerCase() === 'true',
        'message.timestamp.difference.max.ms': parseToBigInt(
            configurationMap.get('message.timestamp.difference.max.ms'),
            9223372036854775807n
        ),
        'message.timestamp.type': (configurationMap.get('message.timestamp.type') ?? 'CreateTime') as unknown as
            | 'CreateTime'
            | 'LogAppendTime',
        'min.cleanable.dirty.ratio': parseFloat(configurationMap.get('min.cleanable.dirty.ratio') ?? '0.5'),
        'min.compaction.lag.ms': parseToBigInt(configurationMap.get('min.compaction.lag.ms'), 0n),
        'min.insync.replicas': parseInt(configurationMap.get('min.insync.replicas'), 1),
        'retention.bytes': parseToBigInt(configurationMap.get('retention.bytes'), -1n),
        'retention.ms': parseToBigInt(configurationMap.get('retention.ms'), 604800000n),
        'segment.bytes': parseInt(configurationMap.get('segment.bytes'), 1073741824),
        'segment.index.bytes': parseInt(configurationMap.get('segment.index.bytes'), 10485760),
        'segment.jitter.ms': parseToBigInt(configurationMap.get('segment.jitter.ms'), 0n),
        'segment.ms': parseToBigInt(configurationMap.get('segment.ms'), 604800000n),
        'unclean.leader.election.enable':
            (configurationMap.get('unclean.leader.election.enable') ?? 'false').toLowerCase() === 'true',
        preallocate: (configurationMap.get('preallocate') ?? 'true').toLowerCase() === 'true',
    }
}

watch(
    topic,
    (topic: NewTopic) => {
        if (formIsDirty.value) {
            errors.value = validate(topic)
        }
    },
    { deep: true }
)
function submit() {
    formIsDirty.value = true
    errors.value = validate(topic.value)

    if (errors.value.size !== 0) {
        nextTick().then(() => {
            const errorMessages = document.getElementsByClassName('form-error')

            if (errorMessages.length !== 0) {
                errorMessages[0].scrollIntoView({ behavior: 'smooth', block: 'end', inline: 'nearest' })
            }
        })
    } else {
        emit('submit', { oldRecord: topicBeforeEdit, newRecord: prunedTopic.value })
    }
}

function getError(configuration: string): string | null {
    return errors.value.get(configuration) ?? null
}

function hasError(configuration: string): boolean {
    return errors.value.has(configuration)
}

onMounted(() => {
    const numberOfNodes = store.getClusterNodesNumbersById(clusterId)
    if (numberOfNodes > 0) {
        nodeOptions.value = Array.from({ length: numberOfNodes }, (_, i) => i + 1).map((it) => {
            return {
                label: it.toString(10),
                value: it,
            }
        })
    }

    advanceModeKeys.value.forEach((it) => {
        if (defaultValuedTopic[it as keyof NewTopic] !== topic.value[it as keyof NewTopic]) {
            manuallyConfigureOtherSettings.value = true
        }
    })
})
</script>
