import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.crud"
version = "0.0.1"

val kotlinVersion = "1.9.22"
val http4kVersion = "5.13.8.0"
val http4kConnectVersion = "5.7.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.http4k:http4k-core:${http4kVersion}")
    implementation("org.http4k:http4k-format-jackson:${http4kVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
}

kotlin {
    jvmToolchain(21)
}

apply(plugin = "java")
apply(plugin = "kotlin")
apply(plugin = "com.github.johnrengelman.shadow")

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("rinha")
        archiveClassifier = ""
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "io.rinha.RinhaKt"))
        }
    }
}