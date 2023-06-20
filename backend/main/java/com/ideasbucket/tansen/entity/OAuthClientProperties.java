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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.BeanInitializationException;

public class OAuthClientProperties {

    private final String provider;

    @NotNull(message = "Client ID cannot be null.")
    @NotBlank(message = "Client ID cannot be blank.")
    private final String clientId;

    @NotNull(message = "Client Secret cannot be null.")
    @NotBlank(message = "Client Secret cannot be blank.")
    private final String clientSecret;

    private final String authorizationUri;

    private final String tokenUri;

    private final String userInfoUri;

    private final String jwkSetUri;

    private final String sessionEndPointUri;

    @NotNull(message = "Scope cannot be null.")
    @NotEmpty(message = "Scope cannot be empty.")
    private final Set<@NotNull(message = "Scope cannot be null.") @NotBlank(
        message = "Scope cannot be blank."
    ) String> scope;

    private final Map<String, String> customParameters;

    public OAuthClientProperties(
        String provider,
        String clientId,
        String clientSecret,
        String authorizationUri,
        String tokenUri,
        String userInfoUri,
        String jwkSetUri,
        String sessionEndPointUri,
        Set<String> scope,
        Map<String, String> customParameters
    ) {
        this.provider = provider;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.jwkSetUri = jwkSetUri;
        this.sessionEndPointUri = sessionEndPointUri;
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
            original.sessionEndPointUri,
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

            if (target.getSessionEndPointUri() == null || target.getSessionEndPointUri().isBlank()) {
                throw new BeanInitializationException(
                    "Okta requires session endpoint URI. Properties path 'auth.oauth2.client.okta.sessionEndPointUri'"
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

    public String getSessionEndPointUri() {
        return sessionEndPointUri;
    }

    public Set<String> getScope() {
        return scope;
    }
}
