jarLibraryWithKotlin {
    dependencies {
        api(project(":annotation:annotation"))
    }

    testing {
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }

    publishing {
        version = "1.0.2"
        group = "androidx.collection"
    }
}
