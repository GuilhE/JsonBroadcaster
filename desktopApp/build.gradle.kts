import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("buildlogic.plugins.kmp.compose")
}

compose {
    desktop {
        application {
            mainClass = "JsonBroadcasterKt"
            jvmArgs += listOf("-Xmx2G")
            nativeDistributions {
                targetFormats(TargetFormat.Dmg)

                group = "com.adb.jsonbroadcaster"
                version = "1.0.0"
                packageVersion = version as String
                packageName = "JsonBroadcaster"
                description = "ADB json broadcaster"
                copyright = "Copyright (c) 2022-present GuilhE"
                licenseFile.set(project.file("../LICENSE"))

                with(project.file("src/desktopMain/resources")) {
                    macOS { iconFile.set(resolve("icon.icns")) }
                    linux { iconFile.set(resolve("icon.png")) }
                    windows { iconFile.set(resolve("icon.ico")) }
                }
            }
        }
    }
}

kotlin {
    jvm("desktop")
    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.jetbrains.kotlinx.serialization)
            }
        }
    }
}