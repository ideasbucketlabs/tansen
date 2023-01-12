/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { ErrorResponse } from '@/entity/ErrorResponse'
import { EMPTY_FN, isBigInt } from '@/util/Util'
import type { StatusCode } from '@/entity/StatusCode'
import ReconnectingEventSource from 'reconnecting-eventsource'

const baseUrl = import.meta.env.VITE_API_ENDPOINT ?? ''

function getProperUrl(url: string): string {
    return url.startsWith('/') ? `${baseUrl}/api${url}` : `${baseUrl}/api/${url}`
}

function nonDataOptions(method: string) {
    return {
        method,
        credentials: 'include',
        headers: {
            Accept: 'application/json, text/plain, */*',
            'Content-Type': 'application/json;charset=UTF-8',
        },
    }
}

export function getEventSource(url: string): EventSource {
    return new ReconnectingEventSource(getProperUrl(url), { withCredentials: true })
}

const replacer = (key: string, value: any) => (isBigInt(value) ? value.toString() : value)

async function execute<TBody, TResponse>(
    url: string,
    method: string,
    data: TBody | null,
    successFn: (response: TResponse) => void,
    errorFn: (response: ErrorResponse) => void
) {
    const inferredUrl = getProperUrl(url)

    const options =
        method === 'POST' || method === 'PUT' || method === 'PATCH'
            ? ({
                  ...nonDataOptions(method),
                  ...{ body: JSON.stringify(data, replacer) },
              } as RequestInit)
            : (nonDataOptions(method) as RequestInit)

    try {
        const response = await fetch(inferredUrl, options)
        const rawResponse = await response.json()

        if (!response.ok) {
            const errors = rawResponse?.data?.errors ?? rawResponse.errors
            errorFn({
                httpCode: response.status as StatusCode,
                httpStatus: response.statusText,
                response: 'Invalid response',
                errors,
            })
        } else {
            successFn(await rawResponse)
        }
    } catch (error) {
        errorFn({
            httpCode: 0 as StatusCode,
            httpStatus: 'Communication error',
            response: `Could not establish communication with ${inferredUrl}`,
        })
    }
}

export async function getAction<TResponse>(
    url: string,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(url, 'GET', null, successFn, errorFn ?? EMPTY_FN)
}

export async function postAction<TBody, TResponse>(
    url: string,
    data: TBody,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(url, 'POST', data, successFn, errorFn ?? EMPTY_FN)
}

export async function putAction<TBody, TResponse>(
    url: string,
    data: TBody,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(url, 'PUT', data, successFn, errorFn ?? EMPTY_FN)
}

export async function patchAction<TBody, TResponse>(
    url: string,
    data: TBody,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(url, 'PATCH', data, successFn, errorFn ?? EMPTY_FN)
}

export async function deleteAction<TResponse>(
    url: string,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(url, 'DELETE', null, successFn, errorFn ?? EMPTY_FN)
}
