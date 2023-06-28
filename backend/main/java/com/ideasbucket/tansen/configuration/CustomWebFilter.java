/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CustomWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final String basePath = exchange.getRequest().getPath().contextPath().value();
        final String path = exchange.getRequest().getPath().pathWithinApplication().value();

        if (
            !path.startsWith("/api") &&
            !path.startsWith("/assets") &&
            !path.startsWith("/logout") &&
            !path.startsWith("/authentication")
        ) {
            return chain.filter(
                exchange
                    .mutate()
                    .request(exchange.getRequest().mutate().path(basePath + "/").contextPath(basePath).build())
                    .build()
            );
        }

        return chain.filter(exchange);
    }
}
