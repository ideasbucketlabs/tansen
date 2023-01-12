/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service

import com.ideasbucket.tansen.entity.ConsumerGroupInformation

interface ConsumerGroupService {

    suspend fun getConsumerGroups(clusterId: String): List<Map<String, String>>

    suspend fun getConsumerGroup(clusterId: String, groupName: String): List<ConsumerGroupInformation>
}
