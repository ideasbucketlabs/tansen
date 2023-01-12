/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
export enum CompatibilityType {
    BACKWARD = 'BACKWARD',
    BACKWARD_TRANSITIVE = 'BACKWARD_TRANSITIVE',
    FORWARD = 'FORWARD',
    FORWARD_TRANSITIVE = 'FORWARD_TRANSITIVE',
    FULL = 'FULL',
    FULL_TRANSITIVE = 'FULL_TRANSITIVE',
    NONE = 'NONE',
}
