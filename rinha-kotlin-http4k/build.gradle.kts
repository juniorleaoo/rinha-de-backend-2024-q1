plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "io.crud"
version = "0.0.1"

val kotlinVersion = "1.9.22"
val http4kVersion = "5.13.8.0"
val http4kConnectVersion = "5.7.0.0"

repositories {
    mavenCentral()
}

application {
    mainClass = "io.rinha.RinhaKt"
}

dependencies {
    implementation("org.http4k:http4k-core:${http4kVersion}")
    implementation("org.http4k:http4k-format-jackson:${http4kVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

kotlin {
    jvmToolchain(21)
}