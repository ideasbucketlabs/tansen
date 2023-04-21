/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export class StreamLogicError extends Error {
    constructor(message: string) {
        super(message)
        Object.setPrototypeOf(this, StreamLogicError.prototype)
        this.name = 'StreamLogicError'
    }
}
