import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
}

version = "2.0.0-SNAPSHOT-1"

kotlin {

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xcontext-receivers")
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

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)
            implementation(libs.htmltext)
            implementation(libs.material3.windowSizeClassMultiplatform)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlass)
            implementation(libs.trulysharedprefs)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitiveAdapters)


        }

        androidMain.dependencies {
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.lifecycle)
            implementation(libs.sqldelight.driver.android)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sqldelight.driver.jvm)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

val proguardSharedPath = "${projectDir.path}/proguard-shared.pro"

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

            proguardFiles += File(proguardSharedPath)
            proguardFiles += File("${projectDir.path}/src/androidMain/proguard-android.pro")

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
        mainClass = "org.orca.pelorus.MainKt"

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

            modules("java.sql")

        }

        buildTypes.release.proguard {
            configurationFiles.from(proguardSharedPath, "${projectDir.path}/src/desktopMain/proguard-desktop.pro")
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
