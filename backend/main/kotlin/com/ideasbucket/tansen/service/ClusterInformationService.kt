/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.service

import com.ideasbucket.tansen.entity.ClusterInformation

interface ClusterInformationService {

    suspend fun getInformation(): List<ClusterInformation>
}
