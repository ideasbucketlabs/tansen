/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.Assert;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ActionCode {
    SAVE_SCHEMA("save-schema", "Add/Edit Schema", "Action Add/Edit Schema", ModuleCode.SCHEMA_REGISTRY_MODULE);

    @JsonProperty("code")
    private final String code;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("module")
    private final ModuleCode moduleCode;

    private static final Map<String, ActionCode> byCode = new HashMap<>();

    static {
        for (ActionCode actionCode : ActionCode.values()) {
            if (byCode.put(actionCode.getCode(), actionCode) != null) {
                throw new IllegalArgumentException("Duplicate Action code: " + actionCode.getCode());
            }
        }
    }

    @JsonCreator
    ActionCode(
        @JsonProperty("code") final String code,
        @JsonProperty("name") final String name,
        @JsonProperty("description") final String description,
        @JsonProperty("moduleCode") final ModuleCode moduleCode
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.moduleCode = moduleCode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public ModuleCode getModuleCode() {
        return moduleCode;
    }

    public String getDescription() {
        return description;
    }

    public static ActionCode getByCode(String code) {
        Assert.notNull(code, "Code cannot be null.");

        return byCode.get(code);
    }

    public static Set<ActionCode> getAllActionCodes() {
        return byCode.values().stream().collect(Collectors.toUnmodifiableSet());
    }
}
