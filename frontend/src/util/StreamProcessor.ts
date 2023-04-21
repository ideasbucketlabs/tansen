/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { getProperUrl } from '@/util/HttpService'
import type { StatusCode } from '@/entity/StatusCode'
import type { ErrorResponse } from '@/entity/ErrorResponse'
import { StreamLogicError } from '@/util/StreamLogicError'

function splitStream(splitOn: string) {
    let buffer = ''

    return new TransformStream({
        transform(chunk, controller) {
            buffer += chunk
            const parts = buffer.split(splitOn)
            parts.slice(0, -1).forEach((part) => controller.enqueue(part))
            buffer = parts[parts.length - 1]
        },
        flush(controller) {
            if (buffer) {
                controller.enqueue(buffer)
            }
        },
    })
}

/**
 * Parse the NDJSON results
 */
function parseJSON() {
    return new TransformStream({
        transform(chunk, controller) {
            controller.enqueue(JSON.parse(chunk))
        },
    })
}

class StreamProcessor<T> {
    private stream: ReadableStreamDefaultReader | null = null
    private readonly processor: (data: T) => void
    private readonly errorProcessor: (input: ErrorResponse) => void
    private abortController: AbortController | null

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    constructor(onProcess: (data: T) => void, onError = (error: ErrorResponse) => {}) {
        this.processor = onProcess
        this.errorProcessor = onError
        this.abortController = null
    }
    async start(url: string) {
        if (this.abortController !== null) {
            throw new StreamLogicError('Current stream must be stopped to start a new one.')
        }

        this.abortController = new AbortController()

        const response = await fetch(getProperUrl(url), {
            headers: {
                Accept: 'application/x-ndjson',
            },
            signal: this.abortController.signal,
        }).catch((error) => {
            // If stream was aborted deliberately then it's not error.
            if (error.name !== undefined && error.name !== null && error.name === 'AbortError') {
                return
            }

            if (error.toString().includes('AbortError')) {
                return
            }

            this.errorProcessor({
                httpCode: 0 as StatusCode,
                httpStatus: 'Communication error',
                response: `Could not establish communication with ${getProperUrl(url)}`,
            })
        })

        if (response === undefined) {
            return
        }

        if (!response.ok) {
            const rawResponse = await response.json()
            const errors = rawResponse?.data?.errors ?? rawResponse.errors
            this.errorProcessor({
                httpCode: response.status as StatusCode,
                httpStatus: response.statusText,
                response: errors?.response ?? 'Invalid response',
                errors,
            })
            return
        }

        const fetchPipe = response.body
        if (fetchPipe === null) {
            this.errorProcessor({
                httpCode: response.status as StatusCode,
                httpStatus: response.statusText,
                response: 'Invalid response',
            })
            return
        }

        this.stream = fetchPipe
            .pipeThrough(new TextDecoderStream())
            .pipeThrough(splitStream('\n'))
            .pipeThrough(parseJSON())
            .getReader()

        const onRead = (result: ReadableStreamReadResult<T>) => {
            if (result.done) {
                return
            }

            this.processor(result.value)
            this.stream
                ?.read()
                .then(onRead)
                .catch((error) => this.errorProcessor(error))
        }

        this.stream
            .read()
            .then(onRead)
            .catch((error) => this.errorProcessor(error))
    }
    async stop() {
        if (this.stream !== null) {
            await this.stream.cancel()
            this.stream = null
        }

        if (this.abortController !== null) {
            this.abortController.abort()
            this.abortController = null
        }
    }
}

export default StreamProcessor
