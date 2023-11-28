import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.backend.common.push

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
}

version = "2.0.0-SNAPSHOT-1"

kotlin {
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
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.kotlass)
            implementation(libs.htmltext)
            implementation(libs.kotlinx.datetime)
            api(libs.compose.webview.multiplatform)
            implementation(libs.lyricist)
            implementation(libs.lyricist.processor)
        }

        androidMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

object Proguard {
    const val COMMON_PATH = "proguard-rules.pro"
}

android {
    namespace = "org.orca.pelorus"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.orca.pelorus"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
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
        debugImplementation(libs.compose.ui.tooling)
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
//                iconFile.set(project.file(""))
            }
        }

        buildTypes.release.proguard {
            configurationFiles.from(Proguard.COMMON_PATH)
        }

        // https://github.com/KevinnZou/compose-webview-multiplatform/blob/main/README.desktop.md#flags
        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED") // recommended but not necessary

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
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

ksp {
    arg("lyricist.generateStringsProperty", "true")
}

// Workaround for KSP, see https://github.com/adrielcafe/lyricist#multiplatform-setup
dependencies {
    add("kspCommonMainMetadata", libs.lyricist.processor)
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}