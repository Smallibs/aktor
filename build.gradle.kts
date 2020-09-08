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
        /*
        nodejs {
        }
        browser {
            testTask {
                useMocha {
                    timeout = "5000" // mochaTimeout here as string
                }
            }
        }
        */
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.8")
                implementation("org.jetbrains.kotlinx:atomicfu-common:0.14.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.8")
                implementation("org.jetbrains.kotlinx:atomicfu:0.14.2")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.awaitility:awaitility-kotlin:3.1.3")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.8")
                implementation("org.jetbrains.kotlinx:atomicfu-js:0.14.2")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
