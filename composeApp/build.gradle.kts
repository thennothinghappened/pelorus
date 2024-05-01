import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.sqldelight)
}

version = "2.0.0-SNAPSHOT-1"

kotlin {

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.animation)
            implementation(compose.components.resources)

            implementation(libs.kotlass)
            implementation(libs.htmltext)
            implementation(libs.kotlinx.datetime)
            api(libs.compose.webview.multiplatform)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.trulysharedprefs)
            implementation(libs.material3.windowSizeClassMultiplatform)
            implementation(libs.decompose)
            implementation(libs.decompose.compose)
        }

        androidMain.dependencies {
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.sqldelight.driver.android)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sqldelight.driver.jvm)
        }
    }
}

object Proguard {
    const val COMMON_PATH = "proguard-rules.pro"
}

android {
    namespace = "org.orca.pelorus"
    compileSdk = libs.versions.android.sdk.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.orca.pelorus"
        minSdk = libs.versions.android.sdk.minSdk.get().toInt()
        targetSdk = libs.versions.android.sdk.targetSdk.get().toInt()
        versionCode = 31
        versionName = version.toString()
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        val storeFilePath = getLocalProperty("ANDROID_STORE_FILE")

        if (storeFilePath != null) {
            create("main") {
                storeFile = file(storeFilePath)
                storePassword = getLocalProperty("ANDROID_STORE_PASSWORD")
                keyAlias = getLocalProperty("ANDROID_KEY_ALIAS")
                keyPassword = getLocalProperty("ANDROID_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.findByName("main")
        }

        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.findByName("main")
            proguardFiles.push(File(Proguard.COMMON_PATH))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    dependencies {
        debugImplementation(libs.androidx.compose.ui.tooling)
    }
}

compose.desktop {

    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "Pelorus"
            packageVersion = version.toString().split("-")[0]

            windows {
                iconFile.set(project.file("icons/pelorus_logo.ico"))
            }

            linux {
                iconFile.set(project.file("icons/pelorus_logo.png"))
            }
        }

        buildTypes.release.proguard {
            configurationFiles.from(Proguard.COMMON_PATH)
        }
    }
}

sqldelight {
    databases {
        create("Cache") {
            packageName = "org.orca.pelorus.cache"
        }
    }
}
