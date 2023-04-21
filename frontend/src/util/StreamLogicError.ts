export class StreamLogicError extends Error {
    constructor(message: string) {
        super(message)
        Object.setPrototypeOf(this, StreamLogicError.prototype)
        this.name = 'StreamLogicError'
    }
}
