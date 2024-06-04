@file:Suppress("UnstableApiUsage", "unused")

import com.android.build.gradle.LibraryExtension
import extensions.addComposeDependencies
import extensions.addKotlinAndroidConfigurations
import extensions.addKotlinCompileOptions
import extensions.buildComposeMetricsParameters
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            val versionCatalog = target.extensions.getByType<VersionCatalogsExtension>().named("libs")
            extensions.configure<LibraryExtension> {
                addKotlinAndroidConfigurations(versionCatalog)
            }
            addKotlinCompileOptions(buildComposeMetricsParameters())
            addComposeDependencies(versionCatalog)
        }
    }
}