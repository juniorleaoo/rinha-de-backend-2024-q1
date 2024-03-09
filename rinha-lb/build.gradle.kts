import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.crud"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {

}

kotlin {
    jvmToolchain(21)
}

apply(plugin = "java")
apply(plugin = "kotlin")
apply(plugin = "com.github.johnrengelman.shadow")

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("rinhalb")
        archiveClassifier = ""
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "io.rinha.RinhaLBKt"))
        }
    }
}