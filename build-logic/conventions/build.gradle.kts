plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.toString()))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.gradle.android.tools)
    implementation(libs.gradle.kotlin)
}

group = "buildlogic.plugins"

gradlePlugin {
    plugins {
        //Application modules
        register("AndroidAppConventionPlugin") {
            id = "${project.group}.application"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("AndroidHiltConventionPlugin") {
            id = "${project.group}.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        //Library modules
        register("AndroidLibraryConventionPlugin") {
            id = "${project.group}.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("AndroidLibraryComposeConventionPlugin") {
            id = "${project.group}.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
    }
}