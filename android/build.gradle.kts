import org.jetbrains.compose.internal.getLocalProperty

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group "org.orca"
version "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.29.0-alpha")
    implementation("androidx.window:window:1.0.0")
}

android {
    compileSdkVersion(33)
    defaultConfig {
        applicationId = "org.orca.android"
        minSdkVersion(23)
        targetSdkVersion(33)
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    signingConfigs {
        create("main") {
            storeFile = file(getLocalProperty("ANDROID_STORE_FILE")!!)
            storePassword = getLocalProperty("ANDROID_STORE_PASSWORD")
            keyAlias = getLocalProperty("ANDROID_KEY_ALIAS")
            keyPassword = getLocalProperty("ANDROID_KEY_PASSWORD")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("main")
        }
    }
}