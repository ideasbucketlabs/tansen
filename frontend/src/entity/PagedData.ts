/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface PagedData<T> {
    currentPage: number
    pageSize: number
    total: number
    totalPages: number
    data: T[]
}
