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
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@PropertySource(value = "classpath:roles.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "rbac")
@EnableConfigurationProperties(RbacProperties.class)
@Validated
public class RbacProperties {

    @JsonProperty("roles")
    @NotNull(message = "Roles cannot be null.")
    private final List<@Valid Role> roles;

    @JsonCreator
    public RbacProperties(@JsonProperty("roles") List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }
}
