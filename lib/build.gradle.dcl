androidxJvmLibrary {
    failOnDeprecationWarnings = false
    dependencies {
        api(project(":list"))
    }
    publishing {
        group = "androidx.foo"
        version = "1.0.0"
    }
}
