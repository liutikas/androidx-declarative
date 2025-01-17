androidLibraryWithKotlin {
    namespace = "androidx.core"

    dependencies {
        api("androidx.annotation:annotation:1.0.0")
    }

    deviceTest {
        dependencies {
            implementation("junit:junit:4.13.2")
            implementation("androidx.test:runner:1.6.2")
            implementation("androidx.test:rules:1.6.1")
            implementation("androidx.test.ext:junit:1.2.1")
        }
    }

    devicelessTest {
        robolectricEnabled = true
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }

    publishing {
        group = "androidx.foo"
        version = "1.0.0"
        name = "Core"
        inceptionYear = 2015
        description = """Provides backward-compatible implementations of Android
                platform APIs and features."""
    }
}
