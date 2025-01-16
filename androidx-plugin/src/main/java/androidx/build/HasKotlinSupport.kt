package androidx.build

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

internal fun setTargetKotlinVersion(project: Project, hasKotlinSupport: HasKotlinSupport) {
    project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
        compilerOptions.apiVersion.set(
            hasKotlinSupport.kotlinVersion.map { it.toKotlinVersion() }
        )
        compilerOptions.languageVersion.set(
            hasKotlinSupport.kotlinVersion.map { it.toKotlinVersion() }
        )
    }
    project.afterEvaluate {
        // https://youtrack.jetbrains.com/issue/KT-74425/
        project.extensions.getByType<KotlinBaseExtension>().coreLibrariesVersion =
            hasKotlinSupport.kotlinVersion.get().toCoreLibraryVersion()
    }
}

@Restricted
interface HasKotlinSupport {
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
