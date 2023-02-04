plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("kotlin-parcelize")
    id("app.cash.sqldelight") version "2.0.0-alpha05"
}

group = "org.orca"
version = "1.0-SNAPSHOT"

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val datetimeVersion = "0.4.0"
        val composeRichtextVersion = "0.16.0"
        val decomposeVersion = "1.0.0-beta-04"
        val sqlDelightVersion = "2.0.0-alpha05"
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class) api(compose.material3)
                implementation("org.orca:kotlass:1.0-SNAPSHOT")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$datetimeVersion")
                implementation("com.halilibo.compose-richtext:richtext-ui-material3:$composeRichtextVersion")
                implementation("org.jsoup:jsoup:1.14.3")
                implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.0")
                api("androidx.core:core-ktx:1.9.0")
                api("androidx.compose.material3:material3:1.1.0-alpha05")
                implementation("app.cash.sqldelight:android-driver:$sqlDelightVersion")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation("com.github.Dansoftowner:jSystemThemeDetector:3.6")
                implementation("app.cash.sqldelight:sqlite-driver:$sqlDelightVersion")
            }
        }
        val desktopTest by getting
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("org.orca")
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}