<template>
    <div class="flex flex-col">
        <div class="flex items-center text-green-500 dark:text-gray-100">
            <div class="mr-2 flex h-6 w-6 items-center" aria-hidden="true" aria-label="Consumers">
                <ConsumerGroupsIcon class="h-full w-full fill-current"></ConsumerGroupsIcon>
            </div>
            <h1 class="text-xl md:text-3xl">Consumers Groups</h1>
        </div>
        <div class="flex-1 overflow-y-auto md:flex">
            <div class="h-full md:flex md:h-auto md:flex-1 md:flex-col">
                <div class="h-full min-h-80 w-full md:flex md:h-auto md:min-h-[40rem] md:flex-1">
                    <div class="mt-2 h-full rounded pb-4 shadow-sm md:flex md:h-auto md:flex-1 md:flex-col md:pb-0">
                        <consumer-group-grid class="h-full min-h-80 rounded md:h-auto md:flex-1"></consumer-group-grid>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import ConsumerGroupGrid from '@/components/ConsumerGroupGrid.vue'
import ConsumerGroupsIcon from '@/icons/ConsumerGroupsIcon.vue'
</script>
