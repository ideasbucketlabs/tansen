/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.exception;

public class InvalidClusterException extends Exception {

    private final String message;

    public InvalidClusterException(String message) {
        super(message);
        this.message = "Given cluster ID '" + message + "' is invalid.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
