/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration.auth;

import static com.ideasbucket.tansen.util.JsonConverter.createObjectNode;
import static com.ideasbucket.tansen.util.RequestResponseUtil.*;

import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestHandler;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@ConditionalOnProperty(value = "auth.type", havingValue = "oauth2")
public class OAuth2SecurityConfiguration {

    private final AuthenticationProperties authenticationProperties;
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public OAuth2SecurityConfiguration(AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
        this.clientRegistrationRepository = new InMemoryReactiveClientRegistrationRepository(oktaClientRegistration());
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        CookieServerCsrfTokenRepository csrfTokenRepository = CookieServerCsrfTokenRepository.withHttpOnlyFalse();
        XorServerCsrfTokenRequestAttributeHandler delegate = new XorServerCsrfTokenRequestAttributeHandler();
        // Use only the handle() method of XorServerCsrfTokenRequestAttributeHandler and the
        // default implementation of resolveCsrfTokenValue() from ServerCsrfTokenRequestHandler
        ServerCsrfTokenRequestHandler csrfTokenRequestHandler = delegate::handle;

        return http
            .authorizeExchange(authorizeExchangeSpec -> {
                authorizeExchangeSpec.pathMatchers(HttpMethod.OPTIONS).permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.GET, "/insight/health/readiness").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.GET, "/insight/health/liveness").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.GET, "/authentication").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.GET, "/login**").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.GET, "/oauth2/**").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.GET, "/favicon.ico").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.GET, "/assets/**").permitAll();
                authorizeExchangeSpec.anyExchange().authenticated();
            })
            .formLogin(formLoginSpec -> formLoginSpec.loginPage("/login"))
            .csrf(csrfSpec -> {
                csrfSpec.csrfTokenRepository(csrfTokenRepository);
                csrfSpec.csrfTokenRequestHandler(csrfTokenRequestHandler);
            })
            .logout(logoutSpec -> {
                logoutSpec.logoutUrl("/logout");
                logoutSpec.logoutSuccessHandler(oidcLogoutSuccessHandler(this.clientRegistrationRepository));
            })
            .oauth2Login(Customizer.withDefaults())
            .exceptionHandling(exceptionHandlingSpec -> {
                exceptionHandlingSpec.authenticationEntryPoint((serverWebExchange, ex) -> {
                    if (isAjaxRequest(serverWebExchange.getRequest().getHeaders())) {
                        var rootNode = createObjectNode();
                        rootNode.put("success", false);
                        var errorsNode = createObjectNode();
                        errorsNode.put("loginUrl", getLoginPath(authenticationProperties));
                        errorsNode.put("message", "Login required.");
                        rootNode.replace("errors", errorsNode);

                        return setJsonResponse(rootNode, serverWebExchange, HttpStatus.UNAUTHORIZED);
                    }

                    return Mono.fromRunnable(() -> {
                        ServerHttpResponse response = serverWebExchange.getResponse();
                        response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
                        response.getHeaders().set("Referer", serverWebExchange.getRequest().getPath().value());
                        response.getHeaders().setLocation(URI.create(getLoginPath(authenticationProperties)));
                    });
                });

                exceptionHandlingSpec.accessDeniedHandler((serverWebExchange, ex) -> {
                    var rootNode = createObjectNode();
                    rootNode.put("success", false);
                    var errorsNode = createObjectNode();
                    errorsNode.put("access", "You do not have access to this action.");
                    rootNode.replace("errors", errorsNode);

                    return setJsonResponse(rootNode, serverWebExchange, HttpStatus.FORBIDDEN);
                });
            })
            .build();
    }

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return this.clientRegistrationRepository;
    }

    @Bean
    WebFilter csrfCookieWebFilter() {
        return (exchange, chain) -> {
            Mono<CsrfToken> csrfToken = exchange.getAttributeOrDefault(CsrfToken.class.getName(), Mono.empty());
            return csrfToken
                .doOnSuccess(token -> {
                    /* Ensures the token is subscribed to. */
                })
                .then(chain.filter(exchange));
        };
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(final ReactiveClientRegistrationRepository repository) {
        var successHandler = new OidcClientInitiatedServerLogoutSuccessHandler(repository);
        successHandler.setPostLogoutRedirectUri("{baseUrl}");

        return successHandler;
    }

    private ClientRegistration oktaClientRegistration() {
        return CommonOAuth2Provider.OKTA
            .getBuilder("okta")
            .clientId(authenticationProperties.getOauth2().getClient("okta").getClientId())
            .clientSecret(authenticationProperties.getOauth2().getClient("okta").getClientSecret())
            .authorizationUri(authenticationProperties.getOauth2().getClient("okta").getAuthorizationUri())
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope(authenticationProperties.getOauth2().getClient("okta").getScope())
            .tokenUri(authenticationProperties.getOauth2().getClient("okta").getTokenUri())
            .userInfoUri(authenticationProperties.getOauth2().getClient("okta").getUserInfoUri())
            .jwkSetUri(authenticationProperties.getOauth2().getClient("okta").getJwkSetUri())
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .clientName("Okta")
            .providerConfigurationMetadata(
                Map.of(
                    "end_session_endpoint",
                    authenticationProperties.getOauth2().getClient("okta").getSessionEndPointUri()
                )
            )
            .build();
    }
}
