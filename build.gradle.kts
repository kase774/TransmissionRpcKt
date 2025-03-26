plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "dev.kason"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // kotlin logging jvm + logback impl
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.17")
    // kotlinx serialization json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    // ktor client
    implementation("io.ktor:ktor-client-core:3.1.0")
    implementation("io.ktor:ktor-client-cio:3.1.0")
    implementation("io.ktor:ktor-client-logging:3.1.0")
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}