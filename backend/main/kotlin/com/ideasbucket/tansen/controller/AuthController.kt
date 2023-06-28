/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.controller

import com.ideasbucket.tansen.configuration.auth.AuthenticationProperties
import com.ideasbucket.tansen.entity.AuthStatus
import com.ideasbucket.tansen.entity.LoginOptions
import com.ideasbucket.tansen.entity.Response
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val authenticationProperties: AuthenticationProperties?) {
    @GetMapping("/authentication")
    suspend fun session(authentication: Authentication?): Response {
        if ((authenticationProperties == null) || (authenticationProperties.type.lowercase() == "disabled")) {
            return Response.withSuccess(AuthStatus.noLoginRequired())
        }

        if (authentication == null || !authentication.isAuthenticated) {
            return Response.withSuccess(
                AuthStatus.notLoggedIn(
                    authenticationProperties.oauth2.clients.map {
                        LoginOptions(it.key, "/oauth2/authorization/${it.key}")
                    }
                )
            )
        }

        val userInformation = (authentication.principal as DefaultOidcUser).userInfo.claims
        return Response.withSuccess(
            AuthStatus.loggedIn(
                userInformation.getOrDefault("given_name", "N") as String,
                userInformation.getOrDefault("family_name", "N") as String
            )
        )
    }
}
