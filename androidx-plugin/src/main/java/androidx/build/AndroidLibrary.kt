package androidx.build

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.software.SoftwareType
import org.gradle.api.provider.Property
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

abstract class AndroidLibraryPlugin : Plugin<Project> {
    @get:SoftwareType(name = "androidxAndroidLibrary")
    abstract val androidXAndroidLibrary: AndroidXAndroidLibrary

    override fun apply(target: Project) {
        androidXAndroidLibrary.setDslContentions()
        linkDslToAndroidExtension(target, androidXAndroidLibrary)
        target.plugins.apply("com.android.library")
    }
}

abstract class AndroidKotlinLibraryPlugin : Plugin<Project> {
    @get:SoftwareType(name = "androidxAndroidKotlinLibrary")
    abstract val androidXAndroidLibrary: AndroidXAndroidLibrary

    override fun apply(target: Project) {
        androidXAndroidLibrary.setDslContentions()
        linkDslToAndroidExtension(target, androidXAndroidLibrary)
        target.plugins.apply("com.android.library")
        target.plugins.apply("kotlin-android")
        target.extensions.getByType<KotlinAndroidExtension>().compilerOptions.jvmTarget.set(androidXAndroidLibrary.javaVersion.map {
            when {
                it == 8 -> JvmTarget.JVM_1_8
                else -> JvmTarget.JVM_21
            }
        })
    }
}

private fun linkDslToAndroidExtension(project: Project, androidXAndroidLibrary: AndroidXAndroidLibrary) {
    project.afterEvaluate {
        val android = project.extensions.getByType<LibraryExtension>()
        android.namespace = androidXAndroidLibrary.namespace.get()
        android.compileSdk = androidXAndroidLibrary.compileSdk.get()
        linkSourceSetToDependencies(
            project,
            ConfigurationNames("api", "implementation"),
            androidXAndroidLibrary.getDependencies()
        )
        project.setCompileJavaTargetVersion(androidXAndroidLibrary.javaVersion.get())
        linkJavaCompilerArguments(project, androidXAndroidLibrary)
    }
}

private fun AndroidXAndroidLibrary.setDslContentions() {
    compileSdk.convention(35)
    javaVersion.convention(8)
    failOnDeprecationWarnings.convention(true)
}

@Restricted
interface AndroidXAndroidLibrary : HasLibraryDependencies, HasJavaSupport {
    @get:Restricted
    val compileSdk: Property<Int>

    @get:Restricted
    val namespace: Property<String>
}