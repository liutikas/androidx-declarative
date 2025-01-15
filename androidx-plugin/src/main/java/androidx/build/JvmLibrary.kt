package androidx.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.software.SoftwareType
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

abstract class JvmLibrary : Plugin<Project> {
    @get:SoftwareType(name = "androidxJvmLibrary")
    abstract val androidXJvmLibrary: AndroidXJvmLibrary

    override fun apply(target: Project) {
        androidXJvmLibrary.setDslConventions()
        target.plugins.apply(JavaLibraryPlugin::class.java)
        val java = target.extensions.getByType(JavaPluginExtension::class.java)
        val mainSourceSet = java.sourceSets.getByName("main")
        linkJvmLibraryDsl(
            target,
            androidXJvmLibrary,
            ConfigurationNames(
                mainSourceSet.apiConfigurationName,
                mainSourceSet.implementationConfigurationName
            )
        )
    }
}

abstract class JvmKotlinLibraryPlugin : Plugin<Project> {
    @get:SoftwareType(name = "androidxJvmKotlinLibrary")
    abstract val androidxJvmKotlinLibrary: AndroidXJvmKotlinLibrary

    override fun apply(target: Project) {
        androidxJvmKotlinLibrary.setDslConventions()
        target.plugins.apply("org.jetbrains.kotlin.jvm")
        val kotlinExtension = target.extensions.getByType<KotlinJvmExtension>()
        val sourceSet = kotlinExtension.sourceSets.getByName("main")
        linkJvmLibraryDsl(
            target,
            androidxJvmKotlinLibrary,
            ConfigurationNames(
                sourceSet.apiConfigurationName,
                sourceSet.implementationConfigurationName
            )
        )
        kotlinExtension.compilerOptions.languageVersion.set(
            androidxJvmKotlinLibrary.kotlinVersion.map { it.toKotlinVersion() }
        )
        target.afterEvaluate {
            // https://youtrack.jetbrains.com/issue/KT-74425/
            kotlinExtension.coreLibrariesVersion = androidxJvmKotlinLibrary.kotlinVersion.get().toCoreLibraryVersion()
        }
    }
}


private fun linkJvmLibraryDsl(
    project: Project,
    androidXJvmLibrary: AndroidXJvmLibrary,
    configurations: ConfigurationNames,
) {
    linkJavaVersion(project, androidXJvmLibrary)
    linkJavaCompilerArguments(project, androidXJvmLibrary)
    linkSourceSetToDependencies(
        project,
        configurations,
        androidXJvmLibrary.getDependencies()
    )
}

private fun linkJavaVersion(project: Project, dslModel: AndroidXJvmLibrary) {
    val java = project.extensions.getByType(JavaPluginExtension::class.java)
    java.toolchain.languageVersion.set(dslModel.javaVersion.map(JavaLanguageVersion::of))
}


private fun AndroidXJvmLibrary.setDslConventions() {
    javaVersion.convention(17)
    failOnDeprecationWarnings.convention(true)
}

@Restricted
interface AndroidXJvmKotlinLibrary : AndroidXJvmLibrary {
    @get:Restricted
    val kotlinVersion: Property<KotlinVersion>
}

@Restricted
enum class KotlinVersion {
    KOTLIN_1_8, KOTLIN_1_9, KOTLIN_2_0, KOTLIN_2_1;

    fun toKotlinVersion(): org.jetbrains.kotlin.gradle.dsl.KotlinVersion {
        return when {
            this == KOTLIN_1_8 -> org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_8
            this == KOTLIN_1_9 -> org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9
            this == KOTLIN_2_0 -> org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
            else -> org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1
        }
    }

    fun toCoreLibraryVersion(): String {
        return when {
            this == KOTLIN_1_8 -> "1.8.0"
            this == KOTLIN_1_9 -> "1.9.0"
            this == KOTLIN_2_0 -> "2.0.0"
            else -> "2.1.0"
        }
    }
}

@Restricted
interface AndroidXJvmLibrary : HasLibraryDependencies, HasJavaSupport
