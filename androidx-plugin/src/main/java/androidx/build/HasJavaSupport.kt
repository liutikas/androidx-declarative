package androidx.build

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.kotlin.dsl.withType
import org.gradle.process.CommandLineArgumentProvider

interface HasJavaSupport {
    /**
     * Sets java compile version
     */
    @get:Restricted
    val javaVersion: Property<Int>

    @get:Restricted
    val failOnDeprecationWarnings: Property<Boolean>
}

internal fun Project.setCompileJavaTargetVersion(version: Int) {
    tasks.withType<JavaCompile>().configureEach {
        // TODO(aurimas): Add a version string converter
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}

internal fun linkJavaCompilerArguments(project: Project, hasJavaSupport: HasJavaSupport) {
    val argProvider = JavaCompileArgumentProvider(hasJavaSupport.failOnDeprecationWarnings)
    project.tasks.withType(JavaCompile::class.java).configureEach {
        options.compilerArgumentProviders.add(argProvider)
    }
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