/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
// @ConditionalOnProperty(value = "auth.type", havingValue = "DISABLED")
@ConditionalOnExpression("${auth}==null or '${auth.type}'.equalsIgnoreCase('DISABLED')")
public class DisabledSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
            .logout((ServerHttpSecurity.LogoutSpec::disable))
            .httpBasic((ServerHttpSecurity.HttpBasicSpec::disable))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(Customizer.withDefaults())
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
