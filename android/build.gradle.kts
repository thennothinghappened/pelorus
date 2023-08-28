import org.jetbrains.compose.internal.utils.getLocalProperty

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.29.1-alpha")
    implementation("androidx.window:window:1.1.0")
    implementation("androidx.compose.material3:material3:1.1.1") // https://issuetracker.google.com/issues/258907850
    implementation("com.arkivanov.decompose:decompose:1.0.0")
    implementation("com.google.accompanist:accompanist-webview:0.29.2-rc")
}

android {
    namespace = group.toString()
    compileSdk = 33
    defaultConfig {
        applicationId = "$group.android"
        minSdk = 23
        versionCode = 24
        versionName = version.toString()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
        warningsAsErrors = false
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
                    "proguard-rules.pro"
                )
            }
            getByName("debug") {
                versionNameSuffix = "DEBUG"
            }
        }
    }
}