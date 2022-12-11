@file:Suppress("UnstableApiUsage", "unused")

import com.android.build.api.dsl.CommonExtension

enum class FlavorDimension {
    Global
}

enum class Flavor(val dimension: FlavorDimension, val applicationIdSuffix: String = "") {
    Dev(FlavorDimension.Global, ".dev"),
    Qa(FlavorDimension.Global, ".qa"),
    Prod(FlavorDimension.Global)
}

internal fun CommonExtension<*, *, *, *>.addProjectGlobalFlavorsAndDimension() {
    apply {
        flavorDimensions += FlavorDimension.Global.name
        productFlavors {
            Flavor.values().forEach {
                register(it.name) {
                    dimension = it.dimension.name
                }
            }
        }
    }
}