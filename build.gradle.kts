import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("kapt") version "1.9.21"
    id("me.champeau.jmh") version "0.7.2"
    application
}

group = "me.nfekete.adventofcode"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotestVersion = "4.6.3"

dependencies {
    implementation("org.openjdk.jmh:jmh-core:1.25")
    implementation("org.openjdk.jmh:jmh-generator-annprocess:1.25")

    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.25")

    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    implementation(kotlin("test-junit5"))
    implementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
    implementation("io.kotest:kotest-runner-junit5-jvm:4.6.0")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "18"
        freeCompilerArgs += "-Xcontext-receivers"
    }
}

application {
//    mainClassName = if (project.hasProperty("mainClass")) project.properties.get("mainClass").toString() else "NULL"
}
