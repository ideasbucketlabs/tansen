/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { StatusCode } from '@/entity/StatusCode'

export interface ErrorResponse {
    httpCode: StatusCode
    httpStatus: string
    response: string
    errors?: unknown
}
