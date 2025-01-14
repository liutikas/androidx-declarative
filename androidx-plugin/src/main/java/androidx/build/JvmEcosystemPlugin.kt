package androidx.build

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes

@RegistersSoftwareTypes(JvmLibrary::class, JvmKotlinLibraryPlugin::class)
class JvmEcosystemPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {

    }
}