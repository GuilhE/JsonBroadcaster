buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle.android.tools)
        classpath(libs.gradle.android.hilt)
        classpath(libs.gradle.kotlin)
        classpath(libs.gradle.kotlin.serialization)
        classpath(libs.gradle.vanniktech.maven.publish)
    }
}

allprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.jetbrains.compose.compiler:compiler")).apply {
                val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
                using(module("androidx.compose.compiler:compiler:${versionCatalog.findVersion("androidxComposeCompiler").get()}"))
            }
        }
    }

    afterEvaluate {
        //https://discuss.kotlinlang.org/t/disabling-androidandroidtestrelease-source-set-in-gradle-kotlin-dsl-script
        project.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()?.let { kmpExt ->
            kmpExt.sourceSets.removeAll {
                setOf(
                    "androidAndroidTestRelease",
                    "androidTestFixtures",
                    "androidTestFixturesDebug",
                    "androidTestFixturesRelease",
                ).contains(it.name)
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            freeCompilerArgs = listOf(
                "-Xskip-prerelease-check",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlin.RequiresOptIn"
            )
        }
    }
}