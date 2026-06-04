// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
}

// group / version は git submodule + includeBuild の dependencySubstitution で参照される
allprojects {
    group = "com.github.haruu11113.wearos-sensor-kit"
    version = "1.0.0"
}

true // Needed to make the Suppress annotation work for the plugins block
