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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    { "active", "scope", "username", "exp", "iat", "sub", "aud", "iss", "jti", "token_type", "client_id", "uid" }
)
public final class JwtValidationResponse {

    @JsonProperty("active")
    private final Boolean active;

    @JsonProperty("scope")
    private final String scope;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("exp")
    private final Integer exp;

    @JsonProperty("iat")
    private final Integer iat;

    @JsonProperty("sub")
    private final String sub;

    @JsonProperty("aud")
    private final String aud;

    @JsonProperty("iss")
    private final String iss;

    @JsonProperty("jti")
    private final String jti;

    @JsonProperty("token_type")
    private final String tokenType;

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("uid")
    private final String uid;

    @JsonCreator
    public JwtValidationResponse(
        @JsonProperty("active") Boolean active,
        @JsonProperty("scope") String scope,
        @JsonProperty("username") String username,
        @JsonProperty("exp") Integer exp,
        @JsonProperty("iat") Integer iat,
        @JsonProperty("sub") String sub,
        @JsonProperty("aud") String aud,
        @JsonProperty("iss") String iss,
        @JsonProperty("jti") String jti,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("uid") String uid
    ) {
        this.active = active;
        this.scope = scope;
        this.username = username;
        this.exp = exp;
        this.iat = iat;
        this.sub = sub;
        this.aud = aud;
        this.iss = iss;
        this.jti = jti;
        this.tokenType = tokenType;
        this.clientId = clientId;
        this.uid = uid;
    }

    public Boolean getActive() {
        return active;
    }

    public String getScope() {
        return scope;
    }

    public String getUsername() {
        return username;
    }

    public Integer getExp() {
        return exp;
    }

    public Integer getIat() {
        return iat;
    }

    public String getSub() {
        return sub;
    }

    public String getAud() {
        return aud;
    }

    public String getIss() {
        return iss;
    }

    public String getJti() {
        return jti;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUid() {
        return uid;
    }
}
