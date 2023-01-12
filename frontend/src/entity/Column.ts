/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface Column {
    dataId: string
    type: 'string' | 'boolean' | 'number' | 'action'
    title: string
    sortable?: boolean
    align: 'center' | 'left' | 'right'
    width: number | 'auto'
    filterable?: boolean
    formatter?<T>(value: number | string | Date, fieldId: string, row: T): string | number
    booleanFilterConfiguration?: Record<'trueLabel' | 'falseLabel', string>
    renderCustom?: true
}
