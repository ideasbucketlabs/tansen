/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class Permission {

    @NotNull(message = "Resource cannot be null.")
    @NotEmpty(message = "Resource cannot be blank.")
    private final String resource;

    @NotNull(message = "Action cannot be null.")
    @NotEmpty(message = "Action cannot be empty.")
    private final List<@NotNull(message = "Action cannot be null.") @NotBlank(
        message = "Action cannot be blank."
    ) String> actions;

    @NotNull(message = "Value cannot be null.")
    @NotEmpty(message = "Value cannot be blank.")
    private final String value;

    public Permission(String resource, List<String> actions, String value) {
        this.resource = resource;
        this.actions = actions;
        this.value = value;
    }

    public String getResource() {
        return resource;
    }

    public List<String> getActions() {
        return actions;
    }

    public String getValue() {
        return value;
    }
}
