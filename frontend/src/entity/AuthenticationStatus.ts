/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { LoginOption } from '@/entity/LoginOptions'

export interface AuthenticationStatus {
    loginRequired: boolean
    loggedIn: boolean
    firstName?: string
    lastName?: string
    initials?: string
    loginOptions?: LoginOption[]
}
