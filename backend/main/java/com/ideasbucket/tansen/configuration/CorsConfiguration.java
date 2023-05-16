/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class CorsConfiguration implements WebFluxConfigurer {

    private final ApplicationProperties applicationProperties;

    @Autowired
    public CorsConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allowedSources = this.applicationProperties.getAllowedSource();

        if (allowedSources != null && !allowedSources.trim().isEmpty()) {
            registry
                .addMapping("/**")
                .allowedOriginPatterns(allowedSources.split(","))
                .allowCredentials(true)
                .allowedMethods(org.springframework.web.cors.CorsConfiguration.ALL);
        }
    }
}
