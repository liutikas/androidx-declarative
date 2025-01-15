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

include("list")
include("utilities")
include("lib")
include("kotlinLib")
include("androidLib")
include("androidKotlinLib")

defaults {

}
