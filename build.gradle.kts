plugins {
    // Apply common plugins to all subprojects
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.daggerHiltAndroid) apply false
    alias(libs.plugins.kotlinKapt) apply false // Ensure kapt is available for submodules
}

// Define dependency versions in versions.toml for consistency
// Example: libs.versions.toml
// [versions]
// androidGradlePlugin = "8.2.2"
// kotlin = "1.9.22"
// hilt = "2.50"
// [plugins]
// androidApplication = { id = "com.android.application", version.ref = "androidGradlePlugin" }
// kotlinAndroid = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
// daggerHiltAndroid = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
// kotlinKapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }