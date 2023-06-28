/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

public class OAuth2 {

    @Valid
    private final Map<String, @Valid @NotNull(
        message = "OAuth2 properties cannot be null."
    ) OAuthClientProperties> client;

    public OAuth2(Map<String, OAuthClientProperties> client) {
        this.client =
            client
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        it -> it.getKey().toLowerCase(),
                        it -> {
                            var result = OAuthClientProperties.getInstanceWithProvider(
                                it.getValue(),
                                it.getKey().toLowerCase()
                            );
                            OAuthClientProperties.validate(result);
                            return result;
                        }
                    )
                );
    }

    public Map<String, OAuthClientProperties> getClients() {
        return client;
    }

    public OAuthClientProperties getClient(String client) {
        if (!this.client.containsKey(client)) {
            throw new InvalidParameterException(client + " is not configured");
        }

        return this.client.get(client);
    }

    @AssertTrue(message = "Invalid OAuth provider currently we only support Okta.")
    private boolean hasValidSupportedOAuthProvider() {
        if (client.isEmpty()) {
            return true;
        }

        return client.containsKey("okta");
    }
}
