package androidx.build

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.software.SoftwareType
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.declarative.dsl.model.annotations.Configuring
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

abstract class JvmLibrary : Plugin<Project> {
    @get:SoftwareType(name = "jarLibrary")
    abstract val androidXJvmLibrary: AndroidXJvmLibrary

    override fun apply(target: Project) {
        androidXJvmLibrary.setDslConventions()
        target.plugins.apply(JavaLibraryPlugin::class.java)
        target.plugins.apply("maven-publish")
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
        val testSourceSet = java.sourceSets.getByName("test")
        target.configurations.getByName(testSourceSet.implementationConfigurationName).dependencies.addAllLater(
            androidXJvmLibrary.getTesting().dependencies.implementation.dependencies
        )
    }
}

abstract class JvmKotlinLibraryPlugin : Plugin<Project> {
    @get:SoftwareType(name = "jarLibraryWithKotlin")
    abstract val androidxJvmKotlinLibrary: AndroidXJvmKotlinLibrary

    override fun apply(target: Project) {
        androidxJvmKotlinLibrary.setDslConventions()
        androidxJvmKotlinLibrary.kotlinVersion.convention(KotlinVersion.KOTLIN_1_8)
        target.plugins.apply("org.jetbrains.kotlin.jvm")
        target.plugins.apply("maven-publish")
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
        val testSourceSet = kotlinExtension.sourceSets.getByName("test")
        target.configurations.getByName(testSourceSet.implementationConfigurationName).dependencies.addAllLater(
            androidxJvmKotlinLibrary.getTesting().dependencies.implementation.dependencies
        )
        setTargetKotlinVersion(target, androidxJvmKotlinLibrary)
    }
}

private fun linkJvmLibraryDsl(
    project: Project,
    androidXJvmLibrary: AndroidXJvmLibrary,
    configurations: ConfigurationNames,
) {
    linkPublishingToMavenPublish(project, androidXJvmLibrary, "java")
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
interface AndroidXJvmKotlinLibrary : AndroidXJvmLibrary, HasKotlinSupport

@Restricted
interface AndroidXJvmLibrary : HasLibraryDependencies, HasJavaSupport, HasPublishingSupport {
    @Nested
    fun getTesting(): Testing

    @Configuring
    fun testing(action: Action<Testing>) {
        action.execute(getTesting())
    }
}
