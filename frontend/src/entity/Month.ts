/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { Week } from '@/entity/Week'

export interface Month {
    year: string
    firstDateOfMonth: Date
    monthName: string
    monthNumber: number
    id: string
    weeks: Week[]
}
