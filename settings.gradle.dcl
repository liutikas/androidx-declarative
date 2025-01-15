pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    includeBuild("androidx-plugin")
}

plugins {
    id("org.gradle.experimental.jvm-ecosystem").version("0.1.30")
    id("androidx-jvm-ecosystem-plugin")
    id("androidx-android-ecosystem-plugin")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "declarative-androidx"

include("list")
include("utilities")
include("lib")
include("kotlinLib")
include("androidLib")
include("androidKotlinLib")

defaults {
    javaLibrary {
        javaVersion = 17

        dependencies {
            implementation("org.apache.commons:commons-text:1.11.0")
        }

        testing {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }
    }

    javaApplication {
        javaVersion = 17

        dependencies {
            implementation("org.apache.commons:commons-text:1.11.0")
        }

        testing {
            testJavaVersion = 21
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }
    }
}
