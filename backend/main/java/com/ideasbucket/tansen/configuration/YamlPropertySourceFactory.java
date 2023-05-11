/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());

        try {
            Properties properties = factory.getObject();

            assert properties != null;
            return new PropertiesPropertySource(
                Objects.requireNonNull(encodedResource.getResource().getFilename()),
                properties
            );
        } catch (Exception exception) {
            if (exception.getCause() instanceof FileNotFoundException) {
                if (exception.getCause().getMessage().contains("roles.yml")) {
                    return new PropertiesPropertySource("roles.yml", new Properties());
                }
            }

            throw exception;
        }
    }
}
