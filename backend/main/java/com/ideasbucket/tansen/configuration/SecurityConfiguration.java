/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final AuthenticationProperties authenticationProperties;

    @Autowired
    public SecurityConfiguration(AuthenticationProperties authenticationProperties) {
        this.authenticationProperties = authenticationProperties;
    }

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        if (this.authenticationProperties == null || this.authenticationProperties.getType() == null) {
            return disabledSecurity(http);
        }
        return disabledSecurity(http);
    }

    private SecurityWebFilterChain disabledSecurity(ServerHttpSecurity http) {
        return http
            .exceptionHandling(exceptionHandlingSpec -> {
                exceptionHandlingSpec.authenticationEntryPoint((swe, e) ->
                    Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                );
                exceptionHandlingSpec.accessDeniedHandler((swe, e) ->
                    Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                );
            })
            .logout((ServerHttpSecurity.LogoutSpec::disable))
            .httpBasic((ServerHttpSecurity.HttpBasicSpec::disable))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(corsSpec -> {})
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(authorizeExchangeSpec -> {
                authorizeExchangeSpec.pathMatchers(HttpMethod.OPTIONS).permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.GET, "/**").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.POST, "/**").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.PUT, "/**").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.PATCH, "/**").permitAll();
                authorizeExchangeSpec.pathMatchers(HttpMethod.DELETE, "/**").permitAll();
                authorizeExchangeSpec.anyExchange().permitAll();
            })
            .build();
    }
}
