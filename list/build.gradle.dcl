androidxJvmLibrary {
    testing {
        dependencies {
            implementation("org.junit.jupiter:junit-jupiter:5.10.2")
            // TODO(aurimas): should support runtimeOnly here
            implementation("org.junit.platform:junit-platform-launcher")
        }
    }
    publishing {
        group = "androidx.foo"
        version = "1.0.0"
    }
}