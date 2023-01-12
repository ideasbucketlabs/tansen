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

@JsonPropertyOrder({ "id", "port", "rack", "host", "configurations" })
public final class NodeInformation {

    @JsonProperty("id")
    private final Integer id;

    @JsonProperty("port")
    private final Integer port;

    @JsonProperty("rack")
    private final String rack;

    @JsonProperty("host")
    private final String host;

    @JsonProperty("configurations")
    private final List<ConfigurationWithDefinition> configurations;

    @JsonCreator
    public NodeInformation(
        @JsonProperty("id") Integer id,
        @JsonProperty("port") Integer port,
        @JsonProperty("rack") String rack,
        @JsonProperty("host") String host,
        @JsonProperty("configurations") List<ConfigurationWithDefinition> configurations
    ) {
        this.id = id;
        this.port = port;
        this.rack = rack;
        this.host = host;
        this.configurations = configurations;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPort() {
        return port;
    }

    public String getRack() {
        return rack;
    }

    public String getHost() {
        return host;
    }

    public List<ConfigurationWithDefinition> getConfigurations() {
        return configurations;
    }
}
