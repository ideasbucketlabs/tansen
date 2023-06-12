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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "clusters", "subjects", "permissions" })
public class Role {

    @JsonProperty("name")
    @NotNull(message = "Role name cannot be null.")
    @NotBlank(message = "Role name cannot be blank.")
    private final String name;

    @JsonProperty("clusters")
    @NotNull(message = "Must have at least one cluster.")
    @NotEmpty(message = "Must have at least one cluster.")
    private final List<@NotNull(message = "Cluster name cannot be null.") @NotBlank(
        message = "Cluster name cannot be blank."
    ) String> clusters;

    @JsonProperty("subjects")
    @NotEmpty(message = "Must have at least one subject.")
    private final List<@Valid Subject> subjects;

    @JsonProperty("permissions")
    private final List<@Valid Permission> permissions;

    @JsonCreator
    public Role(
        @JsonProperty("name") String name,
        @JsonProperty("clusters") List<String> clusters,
        @JsonProperty("subjects") List<Subject> subjects,
        @JsonProperty("permissions") List<Permission> permissions
    ) {
        this.name = name;
        this.clusters = clusters;
        this.subjects = subjects;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public List<String> getClusters() {
        return clusters;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
