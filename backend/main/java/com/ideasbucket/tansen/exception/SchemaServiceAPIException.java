/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.exception;

import com.ideasbucket.tansen.entity.ApiErrorResponse;
import org.springframework.http.HttpStatus;

public class SchemaServiceAPIException extends Exception {

    private final HttpStatus httpStatus;

    private final String errorResponse;

    private final ApiErrorResponse apiErrorResponse;

    public SchemaServiceAPIException(HttpStatus httpStatus, String errorResponse, ApiErrorResponse apiErrorResponse) {
        super(errorResponse);
        this.httpStatus = httpStatus;
        this.errorResponse = errorResponse;
        this.apiErrorResponse = apiErrorResponse;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    public ApiErrorResponse getApiErrorResponse() {
        return apiErrorResponse;
    }
}
