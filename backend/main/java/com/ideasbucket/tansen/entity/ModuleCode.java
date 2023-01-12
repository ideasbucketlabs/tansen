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
public enum ModuleCode {
    SCHEMA_REGISTRY_MODULE("schema-registry-module", "Schema Registry Module", "Module to manage Schema Registry"),
    TOPIC_MODULE("topic-module", "Topic Module", "Module to Topics");

    @JsonProperty("code")
    private final String code;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    private static final Map<String, ModuleCode> byCode = new HashMap<>();

    static {
        for (ModuleCode moduleCode : ModuleCode.values()) {
            if (byCode.put(moduleCode.getCode(), moduleCode) != null) {
                throw new IllegalArgumentException("Duplicate Module code: " + moduleCode.getCode());
            }
        }
    }

    @JsonCreator
    ModuleCode(
        @JsonProperty("code") String code,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description
    ) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Set<ModuleCode> getAllModuleCodes() {
        return byCode.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    public static ModuleCode getByCode(String code) {
        Assert.notNull(code, "Code cannot be null.");

        return byCode.get(code);
    }
}
