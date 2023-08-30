plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    jvmToolchain(18)

    androidTarget {
        jvmToolchain(18)
        compilations.all {
            kotlinOptions.jvmTarget = "18"
        }
    }

    jvm("desktop")

    sourceSets {
        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)api(compose.material3)
                implementation(libs.kotlass)
                implementation(libs.htmlText)
                implementation(libs.kotlinx.datetime)
                implementation(libs.decompose)
                implementation(libs.decompose.jetbrains)
                implementation(libs.kamel.image)
                implementation(libs.accompanist.flowlayout)
//                implementation("me.xdrop:fuzzywuzzy:1.4.0")
                implementation(libs.datetime)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.client.okhttp)

            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)
                api(libs.androidx.material3)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.accompanist.webview)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }

        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation(libs.jSystemThemeDetector)
            }
        }

        val desktopTest by getting
    }
}

android {
    namespace = group.toString()
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
}

buildConfig {
    className("BuildDetails")
    buildConfigField("String", "APP_VERSION", "\"${version}\"")
}
