@file:Suppress("UnstableApiUsage", "unused")

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("dagger.hilt.android.plugin")
                apply("org.jetbrains.kotlin.kapt")
            }

            with(extensions.getByType<VersionCatalogsExtension>().named("libs")) {
                dependencies {
                    add("implementation", findLibrary("android.hilt").get())
                    add("kapt", findLibrary("android.hilt.compiler").get())
                }
            }
        }
    }
}