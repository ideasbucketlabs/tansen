/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "success", "total", "offset", "data" })
public final class Response {

    @JsonProperty("success")
    private final Boolean success;

    @JsonProperty("data")
    private final Object data;

    @JsonProperty("errors")
    private final Object errors;

    @JsonProperty("total")
    private final Long total;

    @JsonProperty("offset")
    private final Integer offset;

    @JsonCreator
    public Response(
        @JsonProperty("success") Boolean success,
        @JsonProperty("total") Long total,
        @JsonProperty("offset") Integer offset,
        @JsonProperty("data") Object data,
        @JsonProperty("errors") Object errors
    ) {
        this.success = success;
        this.data = data;
        this.offset = offset;
        this.total = total;
        this.errors = errors;
    }

    public Response(Object data) {
        this.data = data;
        this.success = true;
        this.offset = null;
        this.total = null;
        this.errors = null;
    }

    public <T> Response(Collection<T> data) {
        this.data = data;
        this.success = true;
        this.offset = 0;
        this.total = (long) data.size();
        this.errors = null;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public Long getTotal() {
        return total;
    }

    public Integer getOffset() {
        return offset;
    }

    public Object getErrors() {
        return errors;
    }

    public static Response withError(Object data) {
        return new Response(false, null, null, null, data);
    }

    public static Response withError() {
        return new Response(false, null, null, null, null);
    }

    public static Response withSuccess(Object data) {
        return new Response(true, null, null, data, null);
    }

    public static <T> Response withSuccess(Collection<T> data) {
        return new Response(true, (long) data.size(), 0, data, null);
    }

    public static <T> Response withSuccess(Collection<T> data, long total, int offset) {
        return new Response(true, total, offset, data, null);
    }
}
