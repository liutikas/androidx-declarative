androidLibrary {
    namespace = "androidx.palette"

    dependencies {
        api("androidx.annotation:annotation:1.0.0")
        implementation(project(":core:core"))
    }

    publishing {
        version = "1.2.0"
        group = "androidx.palette"
    }
}
