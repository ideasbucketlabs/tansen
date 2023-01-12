# Tansen

ergergg

---
## Configuration

> **Note:** Only `TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS` and `TANSEN.KAFKA-CLUSTERS.0.NAME` is a mandatory configuration that must be configured. Others are all optional.

### Endpoints

By default, Tansen listens and uses `HTTP` protocol. However, you can use the `HTTPS` protocol by using the following configuration.

```shell
docker run -d \
      -v somepath/with-certificates:/etc/tansen/secrets \
      -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.LABEL=Local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=localhost:9092,localhost:9101' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY_URL=http://localhost:8081' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SERVER_SSL_KEY-ALIAS=tansen' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SERVER_SSL_KEY-PASSWORD=password' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SERVER_SSL_KEY-STORE=/etc/tansen/secrets/tansen.p12' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SERVER_SSL_KEY-STORE-PASSWORD=password' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SERVER_SSL_KEY-STORE-TYPE=PKCS12' \
      -e 'SPRING_PROFILES_ACTIVE=prod' \
      ideasbucket/tansen:tag
```

### Kafka broker connection

#### PLAIN (no SSL and authentication)

```shell
docker run -d -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
              -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
              -e 'SPRING_PROFILES_ACTIVE=prod' \
              ideasbucket/tansen:tag
```

#### 2-WAY-SSL

```shell
docker run -d \
      -v somepath/with-certificates:/etc/kafka/secrets \
      -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SECURITY_PROTOCOL=SSL' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_TRUSTSTORE_LOCATION=/etc/kafka/secrets/kafka.tansen.truststore.jks' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_TRUSTSTORE_PASSWORD=confluent' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_KEYSTORE_LOCATION=/etc/kafka/secrets/kafka.tansen.keystore.jks' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_KEYSTORE_PASSWORD=confluent' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_KEY_PASSWORD=confluent' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_ENDPOINT_IDENTIFICATION_ALGORITHM=HTTPS' \
      -e 'SPRING_PROFILES_ACTIVE=prod' \
      ideasbucket/tansen:tag
```

#### SASL-PLAIN

```shell
docker run -d \
      -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY_URL=http://schema-registry:8081' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SECURITY_PROTOCOL=SASL_PLAINTEXT' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SASL_JAAS_CONFIG="org.apache.kafka.common.security.plain.PlainLoginModule required username=\"client\" password=\"client-secret\";' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SASL_MECHANISM=PLAIN' \
      -e 'SPRING_PROFILES_ACTIVE=prod' \
      ideasbucket/tansen:tag
```

#### SASL-SCRAM

```shell
docker run -d \
      -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY_URL=http://schema-registry:8081' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SECURITY_PROTOCOL=SASL_PLAINTEXT' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SASL_JAAS_CONFIG="org.apache.kafka.common.security.scram.ScramLoginModule required username=\"client\" password=\"client-secret\";' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SASL_MECHANISM=SCRAM-SHA-256' \
      -e 'SPRING_PROFILES_ACTIVE=prod' \
      ideasbucket/tansen:tag
```

#### SASL-SSL

```shell
docker run -d \
      -v somepath/with-certificates:/etc/kafka/secrets \
      -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY_URL=http://schema-registry:8081' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SECURITY_PROTOCOL=SASL_SSL' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SASL_JAAS_CONFIG="org.apache.kafka.common.security.plain.PlainLoginModule required username=\"client\" password=\"client-secret\";' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SASL_MECHANISM=PLAIN' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_TRUSTSTORE_LOCATION=/etc/kafka/secrets/kafka.tansen.truststore.jks' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_TRUSTSTORE_PASSWORD=confluent' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_KEYSTORE_LOCATION=/etc/kafka/secrets/kafka.tansen.keystore.jks' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_KEYSTORE_PASSWORD=confluent' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.KAFKA-OPTIONS_SSL_KEY_PASSWORD=confluent' \
      -e 'SPRING_PROFILES_ACTIVE=prod' \
      ideasbucket/tansen:tag
```

### Kafka schema registry

#### PLAIN (no SSL and authentication)

```shell
docker run -d -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
              -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
              -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY=localhost:8081' \
              -e 'SPRING_PROFILES_ACTIVE=prod' \
              ideasbucket/tansen:tag
```
#### 2-WAY-SSL

```shell
docker run -d \
      -v somepath/with-certificates:/etc/kafka/secrets \
      -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY=localhost:8081' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA-REGISTRY-OPTIONS_TRUSTSTORE_LOCATION=/etc/kafka/secrets/kafka.tansen.truststore.jks' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA-REGISTRY-OPTIONS_TRUSTSTORE_PASSWORD=confluent' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA-REGISTRY-OPTIONS_KEYSTORE_LOCATION=/etc/kafka/secrets/kafka.tansen.keystore.jks' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA-REGISTRY-OPTIONS_KEYSTORE_PASSWORD=confluent'
      -e 'SPRING_PROFILES_ACTIVE=prod' \
      ideasbucket/tansen:tag
```

#### Username/Password

```shell
docker run -d \
      -v somepath/with-certificates:/etc/kafka/secrets \
      -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY=localhost:8081' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY_USERNAME=username' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY_PASSWORD=very_secure_password' \
      -e 'SPRING_PROFILES_ACTIVE=prod' \
      ideasbucket/tansen:tag
```

#### Disable Topic Add/Edit/Delete

```shell
docker run -d \
      -v somepath/with-certificates:/etc/kafka/secrets \
      -e 'TANSEN.KAFKA-CLUSTERS.0.BOOTSTRAP_SERVERS=broker:9092' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.NAME=local' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY=localhost:8081' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY_USERNAME=username' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.SCHEMA_REGISTRY_PASSWORD=very_secure_password' \
      -r 'TANSEN.KAFKA-CLUSTERS.0.TOPIC_DELETE_ALLOWED=false' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.TOPIC_ADD_ALLOWED=false' \
      -e 'TANSEN.KAFKA-CLUSTERS.0.TOPIC_EDIT_ALLOWED=false' \
      -e 'SPRING_PROFILES_ACTIVE=prod' \
      ideasbucket/tansen:tag
```
## License
Copyright © Nirmal Poudyal

Tansen is open-sourced software licensed under the [Apache License 2.0](LICENSE).
