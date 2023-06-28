/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import type { ErrorResponse } from '@/entity/ErrorResponse'
import { EMPTY_FN, isBigInt } from '@/util/Util'
import type { StatusCode } from '@/entity/StatusCode'

const baseUrl = import.meta.env.VITE_API_ENDPOINT ?? ''

export function getProperUrl(url: string): string {
    if (url === '/authentication' || url === '/logout') {
        return `${baseUrl}${url}`
    }
    return url.startsWith('/') ? `${baseUrl}/api${url}` : `${baseUrl}/api/${url}`
}

function getCsrfToken(): string | null {
    const cookies = Object.fromEntries(
        document.cookie.split('; ').map((v) => v.split(/=(.*)/s).map(decodeURIComponent))
    )

    if (Object.prototype.hasOwnProperty.call(cookies, 'XSRF-TOKEN')) {
        return cookies['XSRF-TOKEN'] as string
    }

    return null
}

function nonDataOptions(method: string) {
    const headers: Record<string, string> = {
        Accept: 'application/json, text/plain, */*',
        'Content-Type': 'application/json;charset=UTF-8',
        'X-Requested-With': 'XMLHttpRequest',
    }

    if (method !== 'GET') {
        const csrfToken = getCsrfToken()

        if (csrfToken !== null) {
            headers['X-XSRF-TOKEN'] = csrfToken
        }
    }

    return {
        method,
        credentials: 'include',
        headers: headers,
    }
}

const replacer = (key: string, value: any) => (isBigInt(value) ? value.toString() : value)

async function execute<TBody, TResponse>(
    url: string,
    method: 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'GET',
    data: TBody | null,
    successFn: (response: TResponse) => void,
    errorFn: (response: ErrorResponse) => void
) {
    const options =
        method === 'POST' || method === 'PUT' || method === 'PATCH'
            ? ({
                  ...nonDataOptions(method),
                  ...{ body: JSON.stringify(data, replacer) },
              } as RequestInit)
            : (nonDataOptions(method) as RequestInit)

    try {
        const response = await fetch(url, options)
        const rawResponse = await response.json()

        if (response.ok) {
            successFn(rawResponse)
        } else {
            const errors = rawResponse?.data?.errors ?? rawResponse.errors

            if (response.status === 401 && (errors?.loginUrl ?? null) !== null) {
                window.location.replace(errors.loginUrl)
            }

            errorFn({
                httpCode: response.status as StatusCode,
                httpStatus: response.statusText,
                response: 'Invalid response',
                errors,
            })
        }
    } catch (error) {
        errorFn({
            httpCode: 0 as StatusCode,
            httpStatus: 'Communication error',
            response: `Could not establish communication with ${url}`,
        })
    }
}

export async function getAction<TResponse>(
    url: string,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(getProperUrl(url), 'GET', null, successFn, errorFn ?? EMPTY_FN)
}

export async function postAction<TBody, TResponse>(
    url: string,
    data: TBody,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(getProperUrl(url), 'POST', data, successFn, errorFn ?? EMPTY_FN)
}

export function logoutAction() {
    const form = document.createElement('form')
    form.action = getProperUrl('/logout')
    form.method = 'POST'

    const csrfToken = getCsrfToken()

    if (csrfToken !== null) {
        const csrfElement = document.createElement('input')
        csrfElement.value = csrfToken
        csrfElement.name = '_csrf'
        csrfElement.type = 'hidden'

        form.appendChild(csrfElement)
    }

    document.body.appendChild(form)
    form.submit()
}

export async function putAction<TBody, TResponse>(
    url: string,
    data: TBody,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(getProperUrl(url), 'PUT', data, successFn, errorFn ?? EMPTY_FN)
}

export async function patchAction<TBody, TResponse>(
    url: string,
    data: TBody,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(getProperUrl(url), 'PATCH', data, successFn, errorFn ?? EMPTY_FN)
}

export async function deleteAction<TResponse>(
    url: string,
    successFn: (response: TResponse) => void,
    errorFn?: (response: ErrorResponse) => void
) {
    await execute(getProperUrl(url), 'DELETE', null, successFn, errorFn ?? EMPTY_FN)
}
