plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_11.toString()))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation(libs.gradle.android.tools)
    implementation(libs.gradle.kotlin)
}

group = "buildlogic.plugins.conventions"

gradlePlugin {
    plugins {
        register("AndroidApplicationConventionPlugin") {
            id = "${project.group}.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("AndroidLibraryConventionPlugin") {
            id = "${project.group}.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("AndroidLibraryComposeConventionPlugin") {
            id = "${project.group}.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("AndroidHiltConventionPlugin") {
            id = "${project.group}.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
    }
}