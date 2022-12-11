@file:Suppress("UnstableApiUsage", "unused")

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
            extensions.configure<LibraryExtension> {
                addComposeOptions(versionCatalog)
                addKotlinJvmOptions(buildComposeMetricsParameters())
            }
            addComposeDependencies(versionCatalog)
        }
    }
}