package androidx.build

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Nested
import org.gradle.declarative.dsl.model.annotations.Configuring
import org.gradle.declarative.dsl.model.annotations.Restricted
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register


internal fun linkPublishingToMavenPublish(
    project: Project,
    hasPublishing: HasPublishingSupport,
    componentName: String
) {
    project.extensions.getByType(PublishingExtension::class.java).apply {
        repositories {
            maven { setUrl(project.layout.buildDirectory.dir("repo")) }
        }
    }
    project.afterEvaluate {
        project.configure<PublishingExtension> {
            publications {
                register<MavenPublication>("release") {
                    from(components.getByName(componentName))
                    groupId = hasPublishing.publishing.group.get().groupName
                    version = hasPublishing.publishing.version.get()
                }
            }
        }
    }
}

interface HasPublishingSupport {
    @get:Nested
    val publishing: Publishing

    @Configuring
    fun publishing(action: Action<Publishing>) {
        action.execute(publishing)
    }
}

@Restricted
interface Publishing {
    @get:Restricted
    val name: Property<String>
    @get:Restricted
    val inceptionYear: Property<Int>
    @get:Restricted
    val description: Property<String>
    @get:Restricted
    val version: Property<String>
    @get:Restricted
    val group: Property<AndroidXMavenGroup>
}
