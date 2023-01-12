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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "compatibility" })
public final class CompatibilityChangeRequest {

    @JsonProperty("compatibility")
    @NotNull(message = "Compatibility mode cannot be null.")
    @NotBlank(message = "Compatibility mode cannot be blank.")
    @Pattern(
        regexp = "^(?:BACKWARD|BACKWARD_TRANSITIVE|FORWARD|FORWARD_TRANSITIVE|FULL|FULL_TRANSITIVE|NONE)$",
        message = "Compatible mode must be BACKWARD, BACKWARD_TRANSITIVE, FORWARD, FORWARD_TRANSITIVE, FULL, FULL_TRANSITIVE or NONE"
    )
    private final String compatibility;

    @JsonCreator
    public CompatibilityChangeRequest(@JsonProperty("compatibility") String compatibility) {
        this.compatibility = compatibility != null ? compatibility.toUpperCase() : null;
    }

    public String getCompatibility() {
        return compatibility;
    }
}
