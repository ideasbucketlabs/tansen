/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "loginRequired", "loggedIn", "firstName", "lastName", "initials" })
public class AuthStatus {

    @JsonProperty("loginRequired")
    private final Boolean loginRequired;

    @JsonProperty("loggedIn")
    private final Boolean loggedIn;

    @JsonProperty("firstName")
    private final String firstName;

    @JsonProperty("lastName")
    private final String lastName;

    @JsonProperty("initials")
    private final String initials;

    @JsonProperty("loginOptions")
    private final List<LoginOptions> loginOptions;

    @JsonCreator
    public AuthStatus(
        @JsonProperty("loginRequired") Boolean loginRequired,
        @JsonProperty("loggedIn") Boolean loggedIn,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("loginOptions") List<LoginOptions> loginOptions
    ) {
        this.loginRequired = loginRequired;
        this.loggedIn = loggedIn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.initials = getNameInitials(firstName, lastName);
        this.loginOptions = loginOptions;
    }

    public Boolean getLoginRequired() {
        return loginRequired;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getInitials() {
        return initials;
    }

    public List<LoginOptions> getLoginOptions() {
        return loginOptions;
    }

    @JsonIgnore
    public static AuthStatus noLoginRequired() {
        return new AuthStatus(false, true, null, null, null);
    }

    @JsonIgnore
    public static AuthStatus notLoggedIn(List<LoginOptions> loginOptions) {
        return new AuthStatus(true, false, null, null, loginOptions);
    }

    @JsonIgnore
    public static AuthStatus loggedIn(String firstName, String lastName) {
        return new AuthStatus(true, true, firstName, lastName, null);
    }

    @JsonIgnore
    private String getNameInitials(String firstName, String lastName) {
        if ((firstName == null) && (lastName == null)) {
            return null;
        }

        if (lastName == null) {
            return firstName.substring(0, 1).toUpperCase();
        }

        if (firstName == null) {
            return lastName.substring(0, 1).toUpperCase();
        }

        return firstName.substring(0, 1).toUpperCase() + lastName.substring(0, 1).toUpperCase();
    }
}
