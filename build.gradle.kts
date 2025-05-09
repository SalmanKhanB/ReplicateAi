// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

//classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
//classpath ("com.google.dagger:hilt-android-gradle-plugin:2.44")
buildscript {
    val hilt_version by extra("2.50")
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
    }
}
