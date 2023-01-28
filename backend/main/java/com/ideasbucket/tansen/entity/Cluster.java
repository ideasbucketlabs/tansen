/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Map;

public class Cluster {

    @NotNull(message = "Cluster name cannot be null.")
    @NotBlank(message = "Cluster name cannot be blank.")
    @Size(max = 128, message = "Cluster name have less than {max} characters.")
    @Pattern(
        regexp = "^[A-Za-z][A-Za-z0-9\\-]+",
        message = "Cluster name must begin with alpha character and can only contain alphanumeric characters with dashes."
    )
    private final String name;

    @Size(max = 64, message = "Label must have less than {max} characters.")
    private final String label;

    @NotNull(message = "Bootstrap server cannot be null.")
    @NotBlank(message = "Bootstrap server cannot be blank.")
    private final String bootstrapServers;

    private final String schemaRegistryUrl;

    private final String schemaRegistryUsername;

    private final String schemaRegistryPassword;

    private final Map<String, String> kafkaOptions;
    private final Map<String, String> schemaRegistryOptions;

    private final Boolean topicAddAllowed;
    private final Boolean topicEditAllowed;
    private final Boolean topicDeleteAllowed;

    public Cluster(
        String name,
        String label,
        String bootstrapServers,
        String schemaRegistryUrl,
        String schemaRegistryUsername,
        String schemaRegistryPassword,
        Map<String, String> kafkaOptions,
        Map<String, String> schemaRegistryOptions,
        Boolean topicAddAllowed,
        Boolean topicEditAllowed,
        Boolean topicDeleteAllowed
    ) {
        this.name = name;
        this.label = label;
        this.bootstrapServers = bootstrapServers;
        var trimmedUrl = (schemaRegistryUrl == null) ? "" : schemaRegistryUrl.trim().toLowerCase();

        if (!trimmedUrl.isEmpty() && !trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
            throw new ExceptionInInitializerError("Schema registry URL must use either http or https protocol.");
        }

        if (trimmedUrl.isEmpty()) {
            this.schemaRegistryUrl = "";
        } else {
            this.schemaRegistryUrl = trimmedUrl.endsWith("/") ? trimmedUrl : trimmedUrl + "/";
        }

        this.kafkaOptions = kafkaOptions == null ? Map.of() : kafkaOptions;
        this.schemaRegistryOptions = schemaRegistryOptions == null ? Map.of() : schemaRegistryOptions;
        this.schemaRegistryUsername = schemaRegistryUsername == null ? "" : schemaRegistryUsername.trim();
        this.schemaRegistryPassword = schemaRegistryPassword == null ? "" : schemaRegistryPassword.trim();
        this.topicAddAllowed = topicAddAllowed == null || topicAddAllowed;
        this.topicEditAllowed = topicEditAllowed == null || topicEditAllowed;
        this.topicDeleteAllowed = topicDeleteAllowed == null || topicDeleteAllowed;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public Map<String, String> getKafkaOptions() {
        return kafkaOptions;
    }

    public Map<String, String> getSchemaRegistryOptions() {
        return schemaRegistryOptions;
    }

    public String getName() {
        return name;
    }

    public Boolean isSchemaRegistryConfigured() {
        return !this.getSchemaRegistryUrl().trim().isEmpty();
    }

    public String getLabel() {
        return label;
    }

    public String getSchemaRegistryUsername() {
        return schemaRegistryUsername;
    }

    public String getSchemaRegistryPassword() {
        return schemaRegistryPassword;
    }

    public Boolean isSchemaRegistryBasicAuthConfigured() {
        return (!this.getSchemaRegistryUsername().isEmpty() && !this.getSchemaRegistryPassword().isEmpty());
    }

    public Boolean getTopicAddAllowed() {
        return topicAddAllowed;
    }

    public Boolean getTopicEditAllowed() {
        return topicEditAllowed;
    }

    public Boolean getTopicDeleteAllowed() {
        return topicDeleteAllowed;
    }
}
