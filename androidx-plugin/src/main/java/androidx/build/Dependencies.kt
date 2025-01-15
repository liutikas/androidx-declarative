package androidx.build

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.Dependencies
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.tasks.Nested
import org.gradle.declarative.dsl.model.annotations.Configuring
import org.gradle.declarative.dsl.model.annotations.Restricted


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

internal fun linkSourceSetToDependencies(
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

internal data class ConfigurationNames(
    val api: String,
    val implementation: String,
)