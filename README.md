<p align="center" style="height: 100px">
    <picture>
        <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/ideasbucketlabs/tansen/main/documentation/images/logo-dark.svg">
        <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/ideasbucketlabs/tansen/main/documentation/images/logo-light.svg">
        <img alt="Tansen" src="https://raw.githubusercontent.com/ideasbucketlabs/tansen/main/documentation/images/logo-light.svg" width="194" height="108" style="max-width: 100%;">
    </picture>
</p>

<p align="center">
  Tansen is a light and fast web UI for visualizing Kafka clusters, managing Kafka topic, viewing Kafka topic messages, manage topic schema, view consumer groups and much more.
</p>


<p align="center">
    <a href="https://github.com/ideasbucketlabs/tansen/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"></a>
    <a href="https://hub.docker.com/r/ideasbucket/tansen"><img src="https://img.shields.io/docker/pulls/ideasbucket/tansen" alt="Total Docker Pulls"></a>
</p>

---
## Features

* Multi-Cluster Management — see all your clusters in one place
* Light and Dark theme — based on your device preference
* View Kafka Brokers — view topic and partition assignments, controller status
* View Kafka Topics — view partition count, replication status, and custom configuration
* Topic schema Management for key and message — view, edit and delete your schema
* View Consumer Groups — view per-partition parked offsets, combined and per-partition lag
* Browse Messages — browse messages with JSON, plain text, Protobuf, and Avro encoding
* Topic Configuration — create and configure new topics and edit existing one
* Support for Okta OAuth Authentication

---

## Getting started ##

To run Tansen for Apache Kafka, you can use either a pre-built Docker image or build it (or a jar file) yourself. For running with Docker you can use this command.
```shell
docker run -d -p 8080:8080
              -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
              -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
              ideasbucket/tansen:tag
```

Please refer to our [configuration page](https://github.com/ideasbucketlabs/tansen/blob/main/documentation/configuration.md) for various configuration options that Tansen provides.


### Liveliness and readiness probes

Liveliness and readiness endpoint is at `/insight/health`.

Info endpoint (build info) is located at `/insight/info`.

___

## Contributing
If you're interested in contributing to Tansen, please read our [contributing docs](https://github.com/ideasbucketlabs/tansen/blob/main/documentation/CONTRIBUTING.md) **before submitting a pull request**.

---
## Community
* Follow us on [Github](https://github.com/ideasbucketlabs/tansen), [Twitter](https://twitter.com/myideasbucket)

---
## License
Copyright © Nirmal Poudyal

Tansen is open-sourced software licensed under the [Apache License 2.0](LICENSE).
