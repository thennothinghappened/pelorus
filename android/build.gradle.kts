import org.jetbrains.compose.internal.utils.getLocalProperty

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}


dependencies {
    implementation(project(":common"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.window)
    implementation(libs.androidx.material3) // https://issuetracker.google.com/issues/258907850
    implementation(libs.decompose)
}

android {
    namespace = Pelorus.Android.namespace
    compileSdk = Pelorus.Android.Sdk.compile

    defaultConfig {
        applicationId = Pelorus.Android.namespace
        minSdk = Pelorus.Android.Sdk.min
        targetSdk = Pelorus.Android.Sdk.compile

        versionCode = Pelorus.Android.versionCode
        versionName = Pelorus.version
    }

    compileOptions {
        sourceCompatibility = Pelorus.Jvm.version
        targetCompatibility = Pelorus.Jvm.version
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