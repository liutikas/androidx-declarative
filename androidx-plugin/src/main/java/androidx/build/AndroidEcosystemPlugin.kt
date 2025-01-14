package androidx.build

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.plugins.software.RegistersSoftwareTypes

@RegistersSoftwareTypes(AndroidLibraryPlugin::class)
class AndroidEcosystemPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {

    }
}