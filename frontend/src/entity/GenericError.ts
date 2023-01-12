/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export interface GenericError {
    httpCode: number
    httpStatus: string
    response: string
    detail: Detail
}

export interface Detail {
    message: string
    errorCode: number
}
