@file:Suppress("UnstableApiUsage", "unused")

import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                addKotlinAndroidConfigurations(target.extensions.getByType<VersionCatalogsExtension>().named("libs"))
                addKotlinJvmOptions()
//                addProjectGlobalFlavorsAndDimension()
            }
        }
    }

    private fun LibraryExtension.addKotlinAndroidConfigurations(libs: VersionCatalog) {
        apply {
            compileSdk = libs.findVersion("androidCompileSdk").get().toString().toInt()
            defaultConfig {
                targetSdk = libs.findVersion("androidTargetSdk").get().toString().toInt()
                minSdk = libs.findVersion("androidMinSdk").get().toString().toInt()
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            lint {
                abortOnError = false
            }
        }
    }
}