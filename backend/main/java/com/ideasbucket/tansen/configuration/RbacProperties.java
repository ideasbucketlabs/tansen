/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "rbac")
@Validated
public class RbacProperties {

    @JsonProperty("roles")
    private final List<@Valid Role> roles;

    @JsonCreator
    public RbacProperties(@JsonProperty("roles") List<Role> roles) {
        this.roles = roles == null ? List.of() : List.copyOf(roles);
    }

    public List<Role> getRoles() {
        return roles;
    }
}
