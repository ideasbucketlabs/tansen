/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
import { describe, it, expect } from 'vitest'
import { parseDate, flatten, unflatten, isBigInt, isBoolean, isNumber, isString } from '@/util/Util'

describe('Can parse date correctly', () => {
    it('can detect bad format', () => {
        expect(parseDate('ert')).toBe(null)
        expect(parseDate('2002-12-23 00:00:00')).toBe(null)
        expect(parseDate('2002-12-23 00:00:00')).toBe(null)
        expect(parseDate('2002-12-23 00:00')).toBe(null)
        expect(parseDate('2002-12-23 00')).toBe(null)
        expect(parseDate('13/23/2001 00:00:00')).toBe(null)
        expect(parseDate('13/23/2001 00:00:00')).toBe(null)
        expect(parseDate('12/23/2001 23:59:60')).toBe(null)
        expect(parseDate('01/01/2001 12:12:30')).toBeInstanceOf(Date)
    })
})

describe('Can infer string type properly', () => {
    it('can detect string type', () => {
        expect(isString('test')).toBe(true)
        expect(isString(1)).toBe(false)
        expect(isString(false)).toBe(false)
        expect(isString(-1)).toBe(false)
        expect(isString(/^0-9$/)).toBe(false)
    })
})

describe('Can infer bigint type properly', () => {
    it('can detect bigint type', () => {
        expect(isBigInt('test')).toBe(false)
        expect(isBigInt(1n)).toBe(true)
        expect(isBigInt(1)).toBe(false)
        expect(isBigInt(BigInt(9007199254740991))).toBe(true)
        expect(isBigInt(BigInt('0x1fffffffffffff'))).toBe(true)
        expect(isBigInt(BigInt('0b11111111111111111111111111111111111111111111111111111'))).toBe(true)
        expect(isBigInt(false)).toBe(false)
        expect(isBigInt(-1)).toBe(false)
        expect(isBigInt(/^0-9$/)).toBe(false)
    })
})

describe('Can infer boolean type properly', () => {
    it('can detect boolean type', () => {
        expect(isBoolean([1, 2, 3])).toBe(false)
        expect(isBoolean(new Date())).toBe(false)
        expect(isBoolean(new Error())).toBe(false)
        expect(isBoolean(Array.prototype.slice)).toBe(false)
        expect(isBoolean({ a: 1 })).toBe(false)
        expect(isBoolean(1)).toBe(false)
        expect(isBoolean(/x/)).toBe(false)
        expect(isBoolean('a')).toBe(false)
        expect(isBoolean(Symbol ? Symbol('a') : undefined)).toBe(false)
        expect(isBoolean(true)).toBe(true)
        expect(isBoolean(false)).toBe(true)
        expect(isBoolean(Object(true))).toBe(true)
        expect(isBoolean(Object(false))).toBe(true)
    })
})

describe('Can infer number type properly', () => {
    it('can detect number type', () => {
        expect(isNumber('test')).toBe(false)
        expect(isNumber(1n)).toBe(false)
        expect(isNumber(1)).toBe(true)
        expect(isNumber(BigInt(9007199254740991))).toBe(false)
        expect(isNumber(BigInt('0x1fffffffffffff'))).toBe(false)
        expect(isNumber(BigInt('0b11111111111111111111111111111111111111111111111111111'))).toBe(false)
        expect(isNumber(false)).toBe(false)
        expect(isNumber(-1)).toBe(true)
        expect(isNumber(/^0-9$/)).toBe(false)

        expect(isNumber(toArgs([1, 2, 3]))).toBe(false)
        expect(isNumber([1, 2, 3])).toBe(false)
        expect(isNumber(true)).toBe(false)
        expect(isNumber(new Date())).toBe(false)
        expect(isNumber(new Error())).toBe(false)
        expect(isNumber(Array.prototype.slice)).toBe(false)
        expect(isNumber({ a: 1 })).toBe(false)
        expect(isNumber(/x/)).toBe(false)
        expect(isNumber('a')).toBe(false)
        expect(isNumber(Symbol ? Symbol('a') : undefined)).toBe(false)
    })
})

function toArgs(array: number[]) {
    // eslint-disable-next-line prefer-spread
    return function () {
        // eslint-disable-next-line prefer-rest-params
        return arguments
    }.apply(undefined, array as [])
}
describe('Can flatten JSON correctly', () => {
    it('can flatten deeply nested JSON correctly', () => {
        let output = flatten({
            test: true,
            data: [
                { one: 1, two: 2 },
                { one: 2, two: 1 },
            ],
            test2: [],
            empty: {},
        })
        expect(JSON.stringify(output)).toEqual(
            '{"test":true,"data[0].one":1,"data[0].two":2,"data[1].one":2,"data[1].two":1,"test2":[],"empty":{}}'
        )

        output = flatten({
            test: true,
            data: { one: 1, two: 2 },
            another: { one: 1, two: 2 },
            simple: { one: 1, two: 2 },
        })
        expect(JSON.stringify(output)).toEqual(
            '{"test":true,"data.one":1,"data.two":2,"another.one":1,"another.two":2,"simple.one":1,"simple.two":2}'
        )

        output = flatten({
            test: true,
            data: { one: 1, two: { one: 1, two: { one: 1, simple: { one: 1, two: 2 } } } },
        })
        expect(JSON.stringify(output)).toEqual(
            // eslint-disable-next-line max-len,vue/max-len
            '{"test":true,"data.one":1,"data.two.one":1,"data.two.two.one":1,"data.two.two.simple.one":1,"data.two.two.simple.two":2}'
        )
    })
})

describe('Can unflatten JSON correctly', () => {
    it('can flatten deeply nested JSON correctly', () => {
        expect(
            unflatten(
                flatten({
                    test: true,
                    data: [
                        { one: 1, two: 2 },
                        { one: 2, two: 1 },
                    ],
                    test2: [],
                    empty: {},
                })
            )
        ).toEqual({
            test: true,
            data: [
                { one: 1, two: 2 },
                { one: 2, two: 1 },
            ],
            test2: [],
            empty: {},
        })

        expect(
            unflatten(
                flatten({
                    test: true,
                    data: { one: 1, two: 2 },
                    another: { one: 1, two: 2 },
                    simple: { one: 1, two: 2 },
                })
            )
        ).toEqual({
            test: true,
            data: { one: 1, two: 2 },
            another: { one: 1, two: 2 },
            simple: { one: 1, two: 2 },
        })

        expect(
            unflatten(
                flatten({
                    test: true,
                    data: { one: 1, two: { one: 1, two: { one: 1, simple: { one: 1, two: 2 } } } },
                })
            )
        ).toEqual({
            test: true,
            data: { one: 1, two: { one: 1, two: { one: 1, simple: { one: 1, two: 2 } } } },
        })
    })
})
