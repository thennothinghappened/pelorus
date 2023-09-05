plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.osdetector)
}

kotlin {
    jvmToolchain(Pelorus.Jvm.versionNumber)

    androidTarget {
        jvmToolchain(Pelorus.Jvm.versionNumber)

        compilations.all {
            kotlinOptions.jvmTarget = Pelorus.Jvm.version.majorVersion
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

                // https://github.com/Kashif-E/KMPMovies/blob/master/common/build.gradle.kts
                // would be nice to use libs toml but this works for now
                val fxSuffix = when (osdetector.classifier) {
                    "linux-x86_64" -> "linux"
                    "linux-aarch_64" -> "linux-aarch64"
                    "windows-x86_64" -> "win"
                    "osx-x86_64" -> "mac"
                    "osx-aarch_64" -> "mac-aarch64"
                    else -> throw IllegalStateException("Unknown OS: ${osdetector.classifier}")
                }
                implementation("org.openjfx:javafx-base:19:${fxSuffix}")
                implementation("org.openjfx:javafx-graphics:19:${fxSuffix}")
                implementation("org.openjfx:javafx-controls:19:${fxSuffix}")
                implementation("org.openjfx:javafx-swing:19:${fxSuffix}")
                implementation("org.openjfx:javafx-web:19:${fxSuffix}")
                implementation("org.openjfx:javafx-media:19:${fxSuffix}")
            }
        }

        val desktopTest by getting
    }
}

android {
    namespace = Pelorus.Android.namespace
    compileSdk = Pelorus.Android.Sdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = Pelorus.Android.Sdk.min
        targetSdk = Pelorus.Android.Sdk.compile
    }

    compileOptions {
        sourceCompatibility = Pelorus.Jvm.version
        targetCompatibility = Pelorus.Jvm.version
    }
}

buildConfig {
    className("BuildDetails")
    buildConfigField("String", "APP_VERSION", "\"${Pelorus.version}\"")
}
