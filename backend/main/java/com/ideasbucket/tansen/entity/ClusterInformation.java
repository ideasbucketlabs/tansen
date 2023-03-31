/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonPropertyOrder({ "id", "name", "label", "online", "nodes", "totalNumberOfTopics", "totalNumberOfPartitions" })
public final class ClusterInformation {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("label")
    private final String label;

    @JsonProperty("online")
    private final Boolean online;

    @JsonProperty("nodes")
    private final List<NodeInformation> nodes;

    @JsonProperty("configurations")
    private final List<ConfigurationWithDefinition> configurations;

    @JsonProperty("totalNumberOfTopics")
    private final Integer totalNumberOfTopics;

    @JsonProperty("totalNumberOfPartitions")
    private final Integer totalNumberOfPartitions;

    @JsonProperty("totalReplicas")
    private final Integer totalReplicas;

    @JsonProperty("inSyncReplicas")
    private final Integer inSyncReplicas;

    @JsonProperty("outOfSyncReplicas")
    private final Integer outOfSyncReplicas;

    @JsonProperty("clusterSize")
    private final Long clusterSize;

    @JsonCreator
    public ClusterInformation(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("label") String label,
        @JsonProperty("online") Boolean online,
        @JsonProperty("nodes") List<NodeInformation> nodes,
        @JsonProperty("configurations") List<ConfigurationWithDefinition> configurations,
        @JsonProperty("totalNumberOfTopics") Integer totalNumberOfTopics,
        @JsonProperty("totalNumberOfPartitions") Integer totalNumberOfPartitions,
        @JsonProperty("totalReplicas") Integer totalReplicas,
        @JsonProperty("inSyncReplicas") Integer inSyncReplicas,
        @JsonProperty("outOfSyncReplicas") Integer outOfSyncReplicas,
        @JsonProperty("clusterSize") Long clusterSize
    ) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.online = online;
        this.nodes = nodes;
        this.totalNumberOfTopics = totalNumberOfTopics;
        this.totalNumberOfPartitions = totalNumberOfPartitions;
        this.totalReplicas = totalReplicas;
        this.inSyncReplicas = inSyncReplicas;
        this.outOfSyncReplicas = outOfSyncReplicas;
        this.configurations = configurations;
        this.clusterSize = clusterSize;
    }

    public ClusterInformation(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("label") String label,
        @JsonProperty("online") Boolean online,
        @JsonProperty("nodes") List<NodeInformation> nodes,
        @JsonProperty("configurations") List<ConfigurationWithDefinition> configurations,
        @JsonProperty("totalNumberOfTopics") Integer totalNumberOfTopics,
        @JsonProperty("totalNumberOfPartitions") Integer totalNumberOfPartitions,
        @JsonProperty("totalReplicas") Integer totalReplicas,
        @JsonProperty("inSyncReplicas") Integer inSyncReplicas,
        @JsonProperty("clusterSize") Long clusterSize
    ) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.online = online;
        this.nodes = nodes;
        this.totalNumberOfTopics = totalNumberOfTopics;
        this.totalNumberOfPartitions = totalNumberOfPartitions;
        this.totalReplicas = totalReplicas;
        this.inSyncReplicas = inSyncReplicas;
        this.outOfSyncReplicas = totalReplicas - inSyncReplicas;
        this.configurations = configurations;
        this.clusterSize = clusterSize;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public Boolean isOnline() {
        return online;
    }

    public List<NodeInformation> getNodes() {
        return nodes;
    }

    public Integer getTotalNumberOfTopics() {
        return totalNumberOfTopics;
    }

    public Integer getTotalNumberOfPartitions() {
        return totalNumberOfPartitions;
    }

    public String getId() {
        return id;
    }

    public Integer getInSyncReplicas() {
        return inSyncReplicas;
    }

    public Integer getTotalReplicas() {
        return totalReplicas;
    }

    public Integer getOutOfSyncReplicas() {
        return outOfSyncReplicas;
    }

    public List<ConfigurationWithDefinition> getConfigurations() {
        return configurations;
    }

    public Long getClusterSize() {
        return clusterSize;
    }
}
