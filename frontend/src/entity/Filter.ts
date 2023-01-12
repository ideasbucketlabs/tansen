/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface Filter {
    property: string
    type: 'number' | 'string' | 'boolean'
    operator: 'ct' | 'sw' | 'eq' | 'ew' | 'gt' | 'lt' | 'lte' | 'gte'
    value: boolean | number | string
}
