jarLibraryWithKotlin {
    dependencies {
        api(project(":annotation:annotation"))
    }

    testing {
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }
    kotlinVersion = KOTLIN_1_9

    publishing {
        version = "1.0.2"
        group = ANDROIDX_COLLECTION
    }
}
