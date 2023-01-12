import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    jacoco
    id("com.github.node-gradle.node") version "3.4.0"
    id("com.diffplug.spotless") version "6.11.0"
}

group = "com.ideasbucket"

java.sourceCompatibility = JavaVersion.VERSION_17

sourceSets {
    main {
        java { setSrcDirs(listOf("backend/main/java", "backend/main/kotlin")) }
        resources { srcDir("backend/main/resources") }
    }

    test {
        java { setSrcDirs(listOf("backend/test/java", "backend/test/kotlin")) }
        resources { srcDir("backend/test/resources") }
    }
}

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
    maven { url = uri("https://repository.mulesoft.org/nexus/content/repositories/public/") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.projectreactor.kafka:reactor-kafka:1.3.13")
    implementation("io.confluent:kafka-avro-serializer:7.1.1")
    implementation("io.confluent:kafka-protobuf-serializer:7.2.1")
    implementation("io.confluent:kafka-json-schema-serializer:7.2.1")
    implementation("com.google.protobuf:protobuf-java:3.21.9")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.4") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.4") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:5.5.4") // for kotest property test
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

jacoco { toolVersion = "0.8.8" }

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(file("$buildDir/coverage"))
    }
}

// tasks.jar { enabled = false }

val buildFrontEnd: TaskProvider<NpmTask> = tasks.register<NpmTask>("buildFrontEnd") {
    workingDir.set(file("$projectDir/frontend"))
    args.set(listOf("run", "build"))
}

val cleanFrontEnd: TaskProvider<Delete> = tasks.register<Delete>("cleanFrontEnd") {
    delete(
        file("$projectDir/backend/main/resources/templates/error/error.html"),
        file("$projectDir/backend/main/resources/templates/error/404.html"),
        file("$projectDir/backend/main/resources/templates/index.html"),
        fileTree("$projectDir/backend/main/resources/static/assets")
    )
}

val copyFrontEnd: TaskProvider<Copy> = tasks.register<Copy>("copyFrontEnd") {
    into("$projectDir")

    copy {
        from("$projectDir/frontend/dist/assets")
        into("$projectDir/backend/main/resources/static/assets")
    }

    copy {
        from("$projectDir/frontend/dist/index.html")
        into("$projectDir/backend/main/resources/templates")
    }

    copy {
        from("$projectDir/frontend/dist")
        include("index.html")
        into("$projectDir/backend/main/resources/templates/error")
        rename("index.html", "404.html")
    }

    // formatter:off
    val name = fileTree("$projectDir/frontend/dist/assets").filter { file ->
        file.name.startsWith("index") && file.name.endsWith(".css")
    }
    // formatter:on

    copy {
        from("$projectDir/backend/main/resources/templates")
        include("errorTemplate.html")
        into("$projectDir/backend/main/resources/templates/error")
        rename("errorTemplate.html", "error.html")
        filter { it.replace("__REPLACE__", name.first().name) }
    }
}

tasks.register("frontEndBuild") {
    dependsOn(buildFrontEnd, cleanFrontEnd, copyFrontEnd)
    tasks.findByName("cleanFrontEnd")?.mustRunAfter(buildFrontEnd)
    tasks.findByName("copyFrontEnd")?.mustRunAfter(cleanFrontEnd)
}

spotless {
    format("misc") {
        target("*.gradle", "*.md", ".gitignore")
        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
    }
    kotlin {
        ktlint("0.47.1").editorConfigOverride(mapOf("indent_size" to 4, "max_line_length" to 120))
        licenseHeaderFile("codequality/spotless/license.txt")
        toggleOffOn("formatter:off", "formatter:on")
        target("backend/*/kotlin/**/*.kt")
        indentWithSpaces(4)
        endWithNewline()
        trimTrailingWhitespace()
    }
    kotlinGradle {
        target("*.gradle.kts") // default target for kotlinGradle
        toggleOffOn("formatter:off", "formatter:on")
        ktlint("0.47.1").editorConfigOverride(mapOf("indent_size" to 4, "max_line_length" to 120))
        indentWithSpaces(4)
        trimTrailingWhitespace()
    }
    java {
        // palantirJavaFormat()
        target("backend/*/java/**/*.java")
        removeUnusedImports()
        toggleOffOn("formatter:off", "formatter:on")
        prettier(mapOf("prettier-plugin-java" to "1.6.2"))
            .config(mapOf("parser" to "java", "tabWidth" to 4, "printWidth" to 120))
        licenseHeaderFile("codequality/spotless/license.txt")
    }
    typescript {
        target("frontend/src/**/*.ts")
        targetExclude("frontend/src/**/*.spec.ts")
        licenseHeaderFile("codequality/spotless/license.txt", "(import|const|declare|export|var) ")
    }
    format("vuejs") {
        target("frontend/src/**/*.vue")
        withinBlocks("setup-first", "<script setup lang=\"ts\">", "</script>") {
            licenseHeaderFile(
                "codequality/spotless/licenseWithNewLineAtBeginning.txt",
                "(import|const|declare|export|var) "
            )
        }
        withinBlocks("setup-last", "<script lang=\"ts\" setup>", "</script>") {
            licenseHeaderFile(
                "codequality/spotless/licenseWithNewLineAtBeginning.txt",
                "(import|const|declare|export|var) "
            )
        }
    }
}

tasks.bootBuildImage {
    imageName = "ideasbucket/${project.name}:${project.version}"
}

// DO_NOT_REMOVE
