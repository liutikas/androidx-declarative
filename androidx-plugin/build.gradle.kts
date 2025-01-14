plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        create("jvm-library") {
            id = "androidx-jvm-library"
            implementationClass = "androidx.build.JvmLibrary"
        }
    }
    plugins {
        create("jvm-ecosystem-plugin") {
            id = "androidx-jvm-ecosystem-plugin"
            implementationClass = "androidx.build.JvmEcosystemPlugin"
        }
    }
}
