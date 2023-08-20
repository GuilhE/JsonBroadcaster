@file:Suppress("UnstableApiUsage")

plugins {
    id("buildlogic.plugins.library")
    id("kotlinx-serialization")
    id("com.vanniktech.maven.publish")
}

android {
    namespace = "com.broadcast.handler"
}

dependencies {
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.serialization)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.DEFAULT, true)
    signAllPublications()

    pom {
        group = "com.github.guilhe"
        version = "1.0.1"
        description.set("Update your app's UI State at runtime.")
        inceptionYear.set("2022")
        url.set("https://github.com/GuilhE/JsonBroadcaster")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("GuilhE")
                name.set("Guilherme Delgado")
                email.set("gdelgado@bliss.pt")
                url.set("https://github.com/GuilhE")
            }
        }
        scm {
            url.set("https://github.com/GuilhE/JsonBroadcaster")
            connection.set("scm:git:github.com/GuilhE/JsonBroadcaster.git")
            developerConnection.set("scm:git:ssh://github.com/GuilhE/JsonBroadcaster.git")
        }
    }
}
