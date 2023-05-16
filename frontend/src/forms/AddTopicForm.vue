<template>
    <FlyInPanel
        :loading="loading"
        title="Add topic"
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
                            <fieldset class="flex flex-col space-y-4">
                                <legend class="font-bold">General</legend>
                                <BaseInputField v-model="topic.name" label="Topic Name" :has-error="hasError('name')">
                                    <div class="form-error text-sm text-red-500" v-if="hasError('name')">
                                        {{ getError('name') }}
                                    </div>
                                </BaseInputField>
                                <BaseInputField
                                    type="number"
                                    label="Number of Partition"
                                    :has-error="hasError('partition')"
                                    v-model.number="topic.partition"
                                >
                                    <div class="form-error text-sm text-red-500" v-if="hasError('partition')">
                                        {{ getError('partition') }}
                                    </div>
                                </BaseInputField>
                            </fieldset>
                            <div v-if="!createWithDefaults">
                                <fieldset class="flex flex-col space-y-4">
                                    <legend class="font-bold">Availability</legend>
                                    <BaseRadioField
                                        v-model="preConfigurationAvailability"
                                        label="Balanced availability/durability"
                                        value="balanced"
                                        name="availability"
                                        un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                        selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800"
                                        class="rounded p-4 transition duration-200 ease-linear"
                                        :info-tooltip="'replication.factor = 3\u000Amin.insync.replicas = 2'"
                                    >
                                        <span class="italic text-gray-600 dark:text-gray-300"
                                            >This option provides a balance between availability and durability. It
                                            enforces high replication with strict sync requirements.</span
                                        >
                                    </BaseRadioField>
                                    <BaseRadioField
                                        name="availability"
                                        :value="'lowAvailability'"
                                        v-model="preConfigurationAvailability"
                                        label="Low availability (recommended for development)"
                                        un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                        selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800"
                                        class="rounded p-4 transition duration-200 ease-linear"
                                        :info-tooltip="'replication.factor = 1\u000Amin.insync.replicas = 1'"
                                    >
                                        <span class="italic text-gray-600 dark:text-gray-300">
                                            This option does not provide replication. It is recommended for development.
                                        </span>
                                    </BaseRadioField>
                                    <BaseRadioField
                                        name="availability"
                                        v-model="preConfigurationAvailability"
                                        value="maximumAvailability"
                                        label="Maximum Availability"
                                        un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                        selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800"
                                        class="rounded p-4 transition duration-200 ease-linear"
                                        :info-tooltip="'replication.factor = 3\u000Amin.insync.replicas = 1'"
                                    >
                                        <span class="italic text-gray-600 dark:text-gray-300">
                                            This option prioritizes topic availability for read and writes. It enforces
                                            high replication with loose replica sync requirements.
                                        </span>
                                    </BaseRadioField>
                                    <BaseRadioField
                                        name="availability"
                                        value="moderateAvailability"
                                        v-model="preConfigurationAvailability"
                                        label="Moderate availability"
                                        un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                        selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800"
                                        class="rounded p-4 transition duration-200 ease-linear"
                                        :info-tooltip="'replication.factor = 2\u000Amin.insync.replicas = 1'"
                                    >
                                        <span class="italic text-gray-600 dark:text-gray-300">
                                            This option provides basic redundancy. It enforces replication with minimal
                                            sync requirements.
                                        </span>
                                    </BaseRadioField>
                                    <BaseRadioField
                                        name="availability"
                                        value="customAvailability"
                                        v-model="preConfigurationAvailability"
                                        label="Custom availability settings"
                                        un-selected-class="hover:bg-green-50 dark:hover:bg-gray-800"
                                        selected-class="bg-green-100 dark:bg-gray-700 dark:shadow-gray-800"
                                        class="rounded-t p-4 transition duration-200 ease-linear"
                                    >
                                        <span class="italic text-gray-600 dark:text-gray-300">
                                            Manually set replication-factor and min.insync.replicas
                                        </span>
                                    </BaseRadioField>
                                    <div
                                        class="rounded bg-green-100 md:flex"
                                        v-if="preConfigurationAvailability === 'customAvailability'"
                                    >
                                        <BaseSelectField
                                            :options="nodeOptions"
                                            cast-as="integer"
                                            v-model="topic['replication.factor']"
                                            :has-error="hasError('replication.factor')"
                                            label="Replication factor"
                                            class="-mt-4 mb-4 flex-1 bg-green-100 pl-4 pr-4 md:pr-1"
                                        >
                                            <div
                                                class="form-error text-sm text-red-500"
                                                v-if="hasError('replication.factor')"
                                            >
                                                {{ getError('replication.factor') }}
                                            </div>
                                        </BaseSelectField>

                                        <BaseSelectField
                                            :options="nodeOptions"
                                            cast-as="integer"
                                            v-model="topic['min.insync.replicas']"
                                            label="Minimum in-sync replica"
                                            :has-error="hasError('min.insync.replicas')"
                                            class="-mt-4 mb-4 flex-1 bg-green-100 pl-4 pr-4 md:pl-1"
                                        >
                                            <div
                                                class="form-error text-sm text-red-500"
                                                v-if="hasError('min.insync.replicas')"
                                            >
                                                {{ getError('min.insync.replicas') }}
                                            </div>
                                        </BaseSelectField>
                                    </div>
                                </fieldset>
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
                                                id="tansen-ui-add-topic-form-advanced-mode-activator"
                                                value="1"
                                                v-model="manuallyConfigureOtherSettings"
                                            />
                                            <label
                                                for="tansen-ui-add-topic-form-advanced-mode-activator"
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
                                                    v-if="configuration === 'message.timestamp.type'"
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
                                <div v-for="configurationKey in mandatoryKeys" :key="configurationKey" class="truncate">
                                    <div>{{ configurationKey }}</div>
                                    <div>
                                        <code>{{ topic[configurationKey as keyof NewTopic] }}</code>
                                    </div>
                                </div>
                            </div>
                            <div class="flex flex-col space-y-3 p-2 text-sm lg:text-base">
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
                        v-if="createWithDefaults"
                        label="Create with defaults"
                    ></BaseButton>
                    <BaseButton
                        v-if="!createWithDefaults"
                        class="block h-10 w-6/12 lg:w-4/12"
                        type="submit"
                        label="Save"
                    ></BaseButton>
                    <button
                        type="button"
                        v-if="createWithDefaults"
                        @click="createWithDefaults = false"
                        class="block h-10 w-6/12 overflow-hidden rounded border border-green-500 bg-green-50 transition duration-200 ease-linear hover:bg-green-200 hover:text-green-500 hover:shadow-lg dark:border-blue-800 dark:bg-blue-700 dark:hover:bg-blue-600 dark:hover:text-gray-100 dark:hover:shadow-black lg:w-4/12"
                    >
                        Customize setting(s)
                    </button>
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
import BaseRadioField from '@/components/BaseRadioField.vue'
import { getId, getTopicConfigurationDefinition } from '@/util/Util'
import type { NewTopic } from '@/entity/NewTopic'
import { validate, convertToProperType } from '@/validators/NewTopicValidator'
import { useRoute } from 'vue-router'
import { clusterInformationStore } from '@/stores/ClusterInformationStore'

defineProps({
    loading: {
        type: Boolean as PropType<boolean>,
        required: false,
        default: false,
    },
})
const clusterId = useRoute().params.clusterId as string
const store = clusterInformationStore()

const preConfigurationAvailability = ref<
    'balanced' | 'lowAvailability' | 'maximumAvailability' | 'moderateAvailability' | 'customAvailability'
>('balanced')
const createWithDefaults = ref<boolean>(true)
const formIsDirty = ref<boolean>(false)
const errors = ref<Map<string, string>>(new Map<string, string>())
const id = ref<string>(getId() + '-synthetic-radio-')
const manuallyConfigureOtherSettings = ref<boolean>(false)
const topic = ref<NewTopic>({
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
    'min.insync.replicas': 2,
    'replication.factor': 3,
    'retention.bytes': -1n,
    'retention.ms': 604800000n,
    'segment.bytes': 1073741824,
    'segment.index.bytes': 10485760,
    'segment.jitter.ms': 0n,
    'segment.ms': 604800000n,
    'unclean.leader.election.enable': false,
    name: '',
    partition: 1,
    preallocate: false,
})

const mandatoryKeys = ref<string[]>(['name', 'partition'])
const preConfiguredDisplayedKeys = [
    ...mandatoryKeys.value,
    ...[
        'retention.bytes',
        'max.message.bytes',
        'retention.ms',
        'min.insync.replicas',
        'replication.factor',
        'cleanup.policy',
    ],
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

const emit = defineEmits<{ (e: 'close'): void; (e: 'submit', newTopic: NewTopic): void }>()

watch(
    preConfigurationAvailability,
    (
        newPreConfigurationAvailabilityValue:
            | 'balanced'
            | 'lowAvailability'
            | 'maximumAvailability'
            | 'moderateAvailability'
            | 'customAvailability'
    ) => {
        if (newPreConfigurationAvailabilityValue === 'balanced') {
            topic.value['replication.factor'] = 3
            topic.value['min.insync.replicas'] = 2
        } else if (newPreConfigurationAvailabilityValue === 'lowAvailability') {
            topic.value['replication.factor'] = 1
            topic.value['min.insync.replicas'] = 1
        } else if (newPreConfigurationAvailabilityValue === 'maximumAvailability') {
            topic.value['replication.factor'] = 3
            topic.value['min.insync.replicas'] = 1
        } else if (newPreConfigurationAvailabilityValue === 'moderateAvailability') {
            topic.value['replication.factor'] = 2
            topic.value['min.insync.replicas'] = 1
        } else {
            topic.value['replication.factor'] = 1
            topic.value['min.insync.replicas'] = 1
        }
    }
)

watch(
    topic,
    (newTopic: NewTopic) => {
        if (formIsDirty.value) {
            errors.value = validate(newTopic)
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
        const prunedData = convertToProperType(topic.value)
        // Check replication rule since recommended replication may not be supported by cluster.
        if (prunedData['min.insync.replicas'] > store.getClusterNodesNumbersById(clusterId)) {
            prunedData['min.insync.replicas'] = store.getClusterNodesNumbersById(clusterId)
        }
        if (prunedData['replication.factor'] > store.getClusterNodesNumbersById(clusterId)) {
            prunedData['replication.factor'] = store.getClusterNodesNumbersById(clusterId)
        }

        emit('submit', prunedData)
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
})
</script>
