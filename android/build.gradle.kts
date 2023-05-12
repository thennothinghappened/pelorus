import org.jetbrains.compose.internal.utils.getLocalProperty

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

//group "org.orca"
//version "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.29.1-alpha")
    implementation("androidx.window:window:1.0.0")
    implementation("androidx.compose.material3:material3:1.1.0-beta01") // https://issuetracker.google.com/issues/258907850
    implementation("com.arkivanov.decompose:decompose:1.0.0")
    implementation("com.google.accompanist:accompanist-webview:0.29.2-rc")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "org.orca.android"
        minSdk = 23
        targetSdk = 33
        versionCode = 19
        versionName = version.toString()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    lint {
        checkReleaseBuilds = false
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
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.txt"
                )
            }
            getByName("debug") {
                versionNameSuffix = "DEBUG"
            }
        }
    }
}