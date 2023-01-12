/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.exception;

public class SchemaRegistryNotConfiguredException extends Exception {

    private final String message;

    public SchemaRegistryNotConfiguredException(String message) {
        super(message);
        this.message = message;
    }

    public SchemaRegistryNotConfiguredException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
