import org.jetbrains.compose.internal.utils.getLocalProperty

//import org.jetbrains.compose.internal.getLocalProperty

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

val material3Version = "1.1.0-alpha05" // https://issuetracker.google.com/issues/258907850
val decomposeVersion = "1.0.0-beta-04"
dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.29.0-alpha")
    implementation("androidx.window:window:1.0.0")
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "org.orca.android"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    if (getLocalProperty("ANDROID_STORE_FILE") != null) {
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
                isMinifyEnabled = true
                signingConfig = signingConfigs.getByName("main")
            }
        }
    }
}