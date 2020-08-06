plugins {
    kotlin("multiplatform") version "1.3.72"
    id("maven-publish")
}

repositories {
    mavenCentral()
}

group = "io.aktor"
version = "Master-SNAPSHOT"

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:9.3.0")
    }
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

repositories {
    jcenter()
}

kotlin {
    jvm()
    js {
    }
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    // macosX64("macos")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.0.1")
                implementation("org.jetbrains.kotlinx:atomicfu-common:0.11.12")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("org.awaitility:awaitility-kotlin:3.1.3")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:0.27.0-eap13")
                implementation("org.jetbrains.kotlinx:atomicfu:0.11.12")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.0.1")
                implementation("org.jetbrains.kotlinx:atomicfu-js:0.11.12")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
