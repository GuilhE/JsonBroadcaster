import java.io.FileInputStream
import java.util.*

plugins {
    id("buildlogic.plugins.application")
    id("buildlogic.plugins.hilt")
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.jsonbroadcaster.matchday"

    defaultConfig {
        applicationId = "com.jsonbroadcaster.matchday"
        versionCode = 1
        versionName = "1.0.0"

        resValue("string", "app_name_label", "MatchDay")
    }

    signingConfigs {
        if (File("signing.properties").exists()) {
            create("releaseDistribution") {
                val prop = Properties().apply { load(FileInputStream(File("signing.properties"))) }
                storeFile = File(prop.getProperty("keystorePath"))
                storePassword = prop.getProperty("keystorePassword")
                keyAlias = prop.getProperty("keyAlias")
                keyPassword = prop.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            if (File("signing.properties").exists()) {
                signingConfig = signingConfigs.getByName("releaseDistribution")
            }
        }

        getByName("debug") {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "false"
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.broadcastHandler)
    implementation(libs.android.material)
    implementation(libs.jetbrains.kotlinx.serialization)
}