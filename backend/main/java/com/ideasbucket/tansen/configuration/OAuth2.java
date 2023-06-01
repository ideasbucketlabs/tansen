/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "client" })
public class OAuth2 {

    @JsonProperty("client")
    @Valid
    private final Map<String, @Valid @NotNull(
        message = "OAuth2 properties cannot be null."
    ) OAuthClientProperties> client;

    @JsonCreator
    public OAuth2(@JsonProperty("client") Map<String, OAuthClientProperties> client) {
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

    public Map<String, OAuthClientProperties> getClient() {
        return client;
    }

    @AssertTrue(message = "Invalid OAuth provider currently we only support Okta.")
    @JsonIgnore
    private boolean hasValidSupportedOAuthProvider() {
        if (client.isEmpty()) {
            return true;
        }

        return client.containsKey("okta");
    }
}
