/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.configuration;

import com.ideasbucket.tansen.entity.Cluster;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("tansen")
@Validated
public class ApplicationProperties {

    private final String allowedSource;

    @NotNull
    @NotEmpty
    private final Set<@Valid Cluster> kafkaClusters;

    private final Map<String, Cluster> keyedCluster;

    public ApplicationProperties(String allowedSource, Set<Cluster> kafkaClusters) throws ExceptionInInitializerError {
        this.kafkaClusters = Set.copyOf(kafkaClusters);
        this.allowedSource = allowedSource;
        this.keyedCluster =
            Map.copyOf(
                this.kafkaClusters.stream()
                    .collect(Collectors.toMap(it -> it.getName().toLowerCase(), Function.identity()))
            );
    }

    public String getAllowedSource() {
        return allowedSource;
    }

    public Set<Cluster> getKafkaClusters() {
        return kafkaClusters;
    }

    @AssertTrue(message = "Cluster name must be unique.")
    private boolean hasValidClusterName() {
        return (
            this.kafkaClusters.stream()
                .map(it -> it.getName().trim().toLowerCase())
                .collect(Collectors.toSet())
                .size() ==
            this.kafkaClusters.size()
        );
    }

    public Map<String, Cluster> getKeyedCluster() {
        return keyedCluster;
    }
}
