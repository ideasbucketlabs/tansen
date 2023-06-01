/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import static jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "auth")
@Validated
public class AuthenticationProperties {

    @JsonProperty("type")
    @Pattern(regexp = "^(?:OAUTH2|oauth2)$", message = "Only OAuth2 is supported currently.", flags = CASE_INSENSITIVE)
    private final String type;

    @JsonProperty("oauth2")
    @Valid
    private final OAuth2 oauth2;

    @JsonCreator
    public AuthenticationProperties(@JsonProperty("type") String type, @JsonProperty("oauth2") OAuth2 oauth2) {
        this.type = type == null ? null : type.toLowerCase();
        this.oauth2 = oauth2;
    }

    public String getType() {
        return type;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }
}
