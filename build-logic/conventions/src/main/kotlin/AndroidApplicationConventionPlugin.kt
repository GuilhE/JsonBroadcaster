@file:Suppress("UnstableApiUsage", "unused")

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            val versionCatalog = target.extensions.getByType<VersionCatalogsExtension>().named("libs")
            extensions.configure<BaseAppModuleExtension> {
                addKotlinAndroidConfigurations(versionCatalog)
                addComposeOptions(versionCatalog)
                addKotlinJvmOptions(buildComposeMetricsParameters())
//                addProjectGlobalFlavorsAndDimension()
            }
            addComposeDependencies(versionCatalog)
        }
    }

    private fun BaseAppModuleExtension.addKotlinAndroidConfigurations(libs: VersionCatalog) {
        apply {
            compileSdk = libs.findVersion("androidCompileSdk").get().toString().toInt()
            defaultConfig {
                targetSdk = libs.findVersion("androidTargetSdk").get().toString().toInt()
                minSdk = libs.findVersion("androidMinSdk").get().toString().toInt()

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testInstrumentationRunnerArguments.putAll(
                    mapOf(
                        "disableAnalytics" to "true",
                        "clearPackageData" to "true"
                    )
                )
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            lint {
                disable.add("Instantiatable")
                abortOnError = false
            }

            testOptions {
                unitTests.apply {
                    isReturnDefaultValues = true
                    isIncludeAndroidResources = true
                }
            }

            packagingOptions {
                // Optimize APK size - remove excess files in the manifest and APK
                resources {
                    excludes.addAll(
                        listOf(
                            "**/kotlin/**",
                            "**/*.kotlin_module",
                            "**/*.version",
                            "**/*.txt",
                            "**/*.xml",
                            "**/*.properties"
                        )
                    )
                }
            }
        }
    }
}