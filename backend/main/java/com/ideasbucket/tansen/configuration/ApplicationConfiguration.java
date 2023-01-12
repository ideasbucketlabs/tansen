/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideasbucket.tansen.service.SchemaExecutor;
import com.ideasbucket.tansen.service.impl.ClusterServiceImpl;
import com.ideasbucket.tansen.service.impl.SchemaServiceImpl;
import com.ideasbucket.tansen.util.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    private final ApplicationProperties applicationProperties;

    private final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

    @Autowired
    public ApplicationConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    ObjectMapper objectMapper() {
        return JsonConverter.getMapper();
    }

    @Bean
    public SchemaExecutor schemaExecutor() {
        return new SchemaExecutor(new SchemaServiceImpl(new ClusterServiceImpl(applicationProperties)));
    }
}
