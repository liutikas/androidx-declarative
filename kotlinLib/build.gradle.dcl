androidxJvmKotlinLibrary {
    failOnDeprecationWarnings = false
    dependencies {
        api(project(":list"))
        implementation("com.intellij:annotations:12.0")
    }
}
