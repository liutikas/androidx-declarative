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
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.declarative.dsl.model.annotations.Configuring
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.process.CommandLineArgumentProvider

abstract class JvmLibrary : Plugin<Project> {
    @SoftwareType(name = "androidxJvmLibrary", modelPublicType = AndroidXJvmLibrary::class)
    abstract fun getAndroidXJvmLibrary(): AndroidXJvmLibrary

    override fun apply(target: Project) {
        setDslConventions()
        target.plugins.apply(JavaLibraryPlugin::class.java)
        val java = target.extensions.getByType(JavaPluginExtension::class.java)
        linkJavaVersion(target, getAndroidXJvmLibrary())
        linkSourceSetToDependencies(
            target,
            java.sourceSets.getByName("main"),
            getAndroidXJvmLibrary().getDependencies()
        )
        linkJavaCompilerArguments(target, getAndroidXJvmLibrary())
    }

    private fun setDslConventions() {
        getAndroidXJvmLibrary().apply {
            javaVersion.convention(17)
            failOnDeprecationWarnings.convention(true)
        }
    }

    internal class JavaCompileArgumentProvider(
        private val failOnDeprecationWarnings: Provider<Boolean>
    ): CommandLineArgumentProvider {
        override fun asArguments(): MutableIterable<String> {
            val args = mutableListOf("-Xlint:unchecked")
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

    private fun linkJavaVersion(project: Project, dslModel: AndroidXJvmLibrary) {
        val java = project.extensions.getByType(JavaPluginExtension::class.java)
        java.toolchain.languageVersion.set(dslModel.javaVersion.map(JavaLanguageVersion::of))
    }

    private fun linkSourceSetToDependencies(
        project: Project,
        sourceSet: SourceSet,
        dependencies: LibraryDependencies
    ) {
        project.configurations.getByName(
            sourceSet.implementationConfigurationName
        ).dependencies.addAllLater(dependencies.implementation.dependencies)
        project.configurations.getByName(
            sourceSet.apiConfigurationName
        ).dependencies.addAllLater(dependencies.api.dependencies)
    }
}

@Restricted
interface AndroidXJvmLibrary : HasLibraryDependencies {
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
