package androidx.build

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.software.SoftwareType
import org.gradle.api.provider.Property
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.kotlin.dsl.getByType

abstract class AndroidLibraryPlugin : Plugin<Project> {
    @get:SoftwareType(name = "androidxAndroidLibrary")
    abstract val androidXAndroidLibrary: AndroidXAndroidLibrary

    override fun apply(target: Project) {
        androidXAndroidLibrary.setDslContentions()
        target.afterEvaluate {
            val android = target.extensions.getByType<LibraryExtension>()
            android.namespace = androidXAndroidLibrary.namespace.get()
            android.compileSdk = androidXAndroidLibrary.compileSdk.get()
        }
        target.plugins.apply("com.android.library")
    }
}

private fun AndroidXAndroidLibrary.setDslContentions() {
    compileSdk.convention(35)
}

@Restricted
interface AndroidXAndroidLibrary {
    @get:Restricted
    val compileSdk: Property<Int>

    @get:Restricted
    val namespace: Property<String>
}