/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { Day } from '@/entity/Day'

export interface Week {
    id: string
    days: Day[]
}
