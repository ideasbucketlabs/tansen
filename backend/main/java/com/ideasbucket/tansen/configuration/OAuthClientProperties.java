/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.BeanInitializationException;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    {
        "provider",
        "clientId",
        "clientSecret",
        "authorizationUri",
        "tokenUri",
        "userInfoUri",
        "jwkSetUri",
        "scope",
        "customParams",
    }
)
public class OAuthClientProperties {

    private final String provider;

    @JsonProperty("clientId")
    @NotNull(message = "Client ID cannot be null.")
    @NotBlank(message = "Client ID cannot be blank.")
    private final String clientId;

    @JsonProperty("clientSecret")
    @NotNull(message = "Client Secret cannot be null.")
    @NotBlank(message = "Client Secret cannot be blank.")
    private final String clientSecret;

    @JsonProperty("authorizationUri")
    private final String authorizationUri;

    @JsonProperty("tokenUri")
    private final String tokenUri;

    @JsonProperty("userInfoUri")
    private final String userInfoUri;

    @JsonProperty("jwkSetUri")
    private final String jwkSetUri;

    @JsonProperty("scope")
    @NotNull(message = "Scope cannot be null.")
    @NotEmpty(message = "Scope cannot be empty.")
    private final List<@NotNull(message = "Scope cannot be null.") @NotBlank(
        message = "Scope cannot be blank."
    ) String> scope;

    @JsonProperty("customParams")
    private final Map<String, String> customParameters;

    @JsonCreator
    public OAuthClientProperties(
        @JsonProperty("provider") String provider,
        @JsonProperty("clientId") String clientId,
        @JsonProperty("clientSecret") String clientSecret,
        @JsonProperty("authorizationUri") String authorizationUri,
        @JsonProperty("tokenUri") String tokenUri,
        @JsonProperty("userInfoUri") String userInfoUri,
        @JsonProperty("jwkSetUri") String jwkSetUri,
        @JsonProperty("scope") List<String> scope,
        @JsonProperty("customParams") Map<String, String> customParameters
    ) {
        this.provider = provider;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.jwkSetUri = jwkSetUri;
        this.scope = scope;
        this.customParameters = customParameters == null ? Map.of() : Map.copyOf(customParameters);
    }

    public static OAuthClientProperties getInstanceWithProvider(OAuthClientProperties original, String provider) {
        return new OAuthClientProperties(
            provider,
            original.clientId,
            original.clientSecret,
            original.authorizationUri,
            original.tokenUri,
            original.userInfoUri,
            original.jwkSetUri,
            original.scope,
            original.customParameters
        );
    }

    public static void validate(OAuthClientProperties target) throws BeanInitializationException {
        if (target.getProvider() == null) {
            return;
        }

        if (target.getProvider().equalsIgnoreCase("okta")) {
            if (target.getJwkSetUri() == null || target.getJwkSetUri().isBlank()) {
                throw new BeanInitializationException(
                    "Okta requires JWK set URI. Properties path 'auth.oauth2.client.okta.jwkSetUri'"
                );
            }

            if (target.getAuthorizationUri() == null || target.getAuthorizationUri().isBlank()) {
                throw new BeanInitializationException(
                    "Okta requires Authorization URI. Properties path 'auth.oauth2.client.okta.authorizationUri'"
                );
            }

            if (target.getTokenUri() == null || target.getTokenUri().isBlank()) {
                throw new BeanInitializationException(
                    "Okta requires token URI. Properties path 'auth.oauth2.client.okta.tokenUri'"
                );
            }

            if (!new HashSet<>(target.scope).containsAll(List.of("openid", "profile", "email"))) {
                throw new BeanInitializationException(
                    "Okta requires following scopes openid, profile and email at minimum. Properties path 'auth.oauth2.client.okta.scope'"
                );
            }
        }
    }

    public String getProvider() {
        return provider;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public String getJwkSetUri() {
        return jwkSetUri;
    }

    public List<String> getScope() {
        return scope;
    }
}
