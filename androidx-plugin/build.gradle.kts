plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    implementation("com.android.tools.build:gradle:8.10.0-alpha02")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of("21"))
    }
}

gradlePlugin {
    plugins {
        create("jvm-ecosystem-plugin") {
            id = "androidx-jvm-ecosystem-plugin"
            implementationClass = "androidx.build.JvmEcosystemPlugin"
        }
        create("android-ecosystem-plugin") {
            id = "androidx-android-ecosystem-plugin"
            implementationClass = "androidx.build.AndroidEcosystemPlugin"
        }
    }
}
