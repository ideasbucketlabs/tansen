# Contributing

Thanks for your interest in contributing to Tansen!!! Please take a moment to review this document **before submitting a pull request**.

## Pull requests

**Please ask first before starting work on any significant new features.**

It's never a fun experience to have your pull request declined after investing a lot of time and effort into a new feature. To avoid this from happening, we request that contributors create [a feature request](https://github.com/ideasbucketlabs/tansen/discussions/new?category=ideas) to first discuss any significant new ideas. This includes things like adding new utilities, new features, etc.

## Coding standards

Tansen code is separated into two parts backend and frontend. But formatting both parts are easy you can run:

For backend
```sh
./gradlew spotlessApply
```
For frontend
```sh
cd frontend && npm run lint
```
## Running tests

You can run the test suite using the following commands:

For backend
```sh
./gradlew clean test --info
```
For frontend
```sh
cd frontend
npm run test:unit:ci
```
Please ensure that the tests are passing when submitting a pull request. If you're adding new features to Tansen, please include tests.

## Development process

Tansen project is separated into two parts/stacks `backend` and `frontend`. `backend` is in _Spring Boot_ with _Weblfux_ using _Kotlin_ and _Java_. `frontend` is in VueJS using Typescript. Hence, you will need following to work with Tansen
* JDK 17
* nodejs
* Kafka

Here are the steps to get started

### Step 1 - Make sure Kafka is running ###
In order to speed up the Tansen comes with `docker-compose.yml` inside [setup/plaintext/docker-compose.yml](https://raw.githubusercontent.com/ideasbucketlabs/tansen/main/setup/plaintext/docker-compose.yml). This should start all necessary components related to Kafka. 

### Step 2 - Set the necessary environment variables
You can set following environment variables to get started.
```environment-variables
tansen.kafka-clusters.0.bootstrap_servers=localhost:9092,localhost:9101;
tansen.kafka-clusters.0.label=Local;
tansen.kafka-clusters.0.name=local;
tansen.kafka-clusters.0.schema_registry_url=http://localhost:8081/
SPRING_PROFILES_ACTIVE=test
```
If you are using `IntelliJ` you can use following screenshot as reference.
<p>
  <picture>
      <img alt="Tansen Environment configuration" src="https://raw.githubusercontent.com/ideasbucketlabs/tansen/main/documentation/images/environment-settings.png" width="144" height="58" style="max-width: 100%;">
  </picture>
</p>

You can do test with lots of different types of configurations for more information regarding configuration please visit configuration section.

### Step 3 - Start the application
`./gradlew bootRun`
You can access application at `http://localhost:8080`

### Step 4 - (Only applicable if you want to access UI)
When you start the application you should be able to access all the APIs. You can access list of APIs at `/api-documentation.html` But if you want to access the UI then you will need to compile frontend. Here are the steps.
```bash
cd frontend
npm install 
npm run build
cd ..
./gradlew cleanFrontEnd
./gradlew copyFrontEnd
./gradlew bootRun
```
You can access application at `http://localhost:8080`

### Step 5 - (Only applicable if you want to work in UI/Frontend)
```bash
cd frontend
npm install 
npm run dev
cd ..
./gradlew bootRun
```
You can access application at `http://localhost:5173` 

Please note port is **5173** _not 8080_ also make sure that you are running backend with Spring Profile **`test`** otherwise you will get CORS error.

---

## Configuration

Tansen supports many configuration(s) for more information regarding configuration please visit [configuration page](https://github.com/ideasbucketlabs/tansen/blob/main/documentation/configuration.md).

---

Please ensure that the tests are passing when submitting a pull request. If you're adding new features to Tansen, please include tests.
