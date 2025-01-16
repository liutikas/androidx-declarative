pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    includeBuild("androidx-plugin")
}

plugins {
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

include(":annotation:annotation")
include(":collection:collection")
include(":core:core")
include(":palette:palette")

defaults {
}
