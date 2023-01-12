/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface ServerResponse<T> {
    success: boolean
    total: number
    offset: number | null
    data: T
    errors: unknown
}
