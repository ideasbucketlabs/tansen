/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration.auth;

import static jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

import com.ideasbucket.tansen.entity.OAuth2;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "auth")
@Validated
public class AuthenticationProperties {

    @Pattern(
        regexp = "^(?:disabled|oauth2|DISABLED|OAUTH2)$",
        message = "Only OAUTH2 and DISABLED is supported.",
        flags = CASE_INSENSITIVE
    )
    private final String type;

    @Valid
    private final OAuth2 oauth2;

    public AuthenticationProperties(String type, OAuth2 oauth2) {
        this.type = type == null ? "DISABLED" : type.toLowerCase();
        this.oauth2 = oauth2;
    }

    public String getType() {
        return type;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }
}
