buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.shipkit:shipkit-changelog:2.0.1")
        classpath("org.shipkit:shipkit-auto-version:2.0.4")
        classpath("com.gradleup.nmcp:nmcp:0.0.4")
    }
}

plugins {
    java
    `java-library`
    `maven-publish`
    id("com.gradleup.nmcp").version("0.0.4")
}

apply(from="gradle/java-publication.gradle.kts")
apply(from="gradle/shipkit.gradle.kts")

group = "io.github.nettyplus"
description = "netty-leak-detector-junit-extension"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.junit.jupiter.api)
    implementation(platform("io.netty:netty-bom:4.1.107.Final"))
    implementation("io.netty:netty-buffer")
    implementation("io.netty:netty-common")

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    failFast = true
    maxParallelForks = 1
}

nmcp {
    if (System.getenv("NEXUS_TOKEN_PWD") != null) {
        publish("mavenJava") {
            username = System.getenv("NEXUS_TOKEN_USER")
            password = System.getenv("NEXUS_TOKEN_PWD")
            publicationType = "AUTOMATIC"
        }
    }
}
