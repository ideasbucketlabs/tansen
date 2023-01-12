/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.*;
import javax.validation.constraints.AssertTrue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "mode" })
public final class ModeChangeRequest {

    @JsonProperty("mode")
    private final String mode;

    @JsonCreator
    public ModeChangeRequest(@JsonProperty("mode") String mode) {
        this.mode = (mode == null) ? "READWRITE" : mode.toUpperCase();
    }

    public String getMode() {
        return mode;
    }

    @AssertTrue(message = "Mode must be IMPORT, READONLY, or READWRITE (default).")
    @JsonIgnore
    public boolean hasValidMode() {
        return (this.mode.equals("IMPORT") || this.mode.equals("READONLY") || this.mode.equals("READWRITE"));
    }
}
