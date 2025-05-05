buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Add Hilt Gradle plugin version
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
