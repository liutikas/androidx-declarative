package androidx.build

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.Dependencies
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.internal.plugins.software.SoftwareType
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.declarative.dsl.model.annotations.Configuring
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType
import org.gradle.process.CommandLineArgumentProvider
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

private data class ConfigurationNames(
    val api: String,
    val implementation: String,
)

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

private fun linkSourceSetToDependencies(
    project: Project,
    configurations: ConfigurationNames,
    dependencies: LibraryDependencies
) {
    project.configurations.getByName(configurations.implementation).dependencies.addAllLater(
        dependencies.implementation.dependencies
    )
    project.configurations.getByName(configurations.api).dependencies.addAllLater(
        dependencies.api.dependencies
    )
}

internal class JavaCompileArgumentProvider(
    private val failOnDeprecationWarnings: Provider<Boolean>
): CommandLineArgumentProvider {
    override fun asArguments(): MutableIterable<String> {
        val args = mutableListOf(
            "-Xlint:unchecked",
            "-Xlint:-options", // // JDK 21 considers Java 8 an obsolete source and target value. Disable this warning.
        )
        if (failOnDeprecationWarnings.get()) {
            args.add("-Xlint:deprecation")
        }
        return args
    }
}

private fun linkJavaCompilerArguments(project: Project, dslModel: AndroidXJvmLibrary) {
    val argProvider = JavaCompileArgumentProvider(dslModel.failOnDeprecationWarnings)
    project.tasks.withType(JavaCompile::class.java).configureEach {
        options.compilerArgumentProviders.add(argProvider)
    }
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
interface AndroidXJvmLibrary : HasLibraryDependencies {
    /**
     * Sets java compile version
     */
    @get:Restricted
    val javaVersion: Property<Int>

    @get:Restricted
    val failOnDeprecationWarnings: Property<Boolean>
}

@Restricted
interface HasLibraryDependencies {
    @Nested
    fun getDependencies(): LibraryDependencies

    @Configuring
    fun dependencies(action: Action<in LibraryDependencies?>) {
        action.execute(getDependencies())
    }
}

@Restricted
interface LibraryDependencies : BasicDependencies {
    val api: DependencyCollector
}

interface BasicDependencies : Dependencies {
    val implementation: DependencyCollector
    val runtimeOnly: DependencyCollector
    val compileOnly: DependencyCollector
}
