androidxJvmKotlinLibrary {
    kotlinVersion = KOTLIN_1_8
    failOnDeprecationWarnings = false
    dependencies {
        api(project(":list"))
        implementation("com.intellij:annotations:12.0")
    }
    testing {
        dependencies {
            implementation("junit:junit:4.13")
        }
    }
    publishing {
        group = "androidx.foo"
        version = "1.0.0"
    }
}
