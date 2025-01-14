plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
}

gradlePlugin {
    plugins {
        create("jvm-ecosystem-plugin") {
            id = "androidx-jvm-ecosystem-plugin"
            implementationClass = "androidx.build.JvmEcosystemPlugin"
        }
    }
}
