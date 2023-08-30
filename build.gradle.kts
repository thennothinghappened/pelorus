
val GITHUB_USER = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER")
val GITHUB_TOKEN = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")

allprojects {
    group = "org.orca"
    version = "1.6.6-BETA"

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
        maven {
            url = uri("https://maven.pkg.github.com/thennothinghappened/kotlass")
            credentials {
                username = GITHUB_USER
                password = GITHUB_TOKEN
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/Qawaz/compose-datetime")
            credentials {
                username = GITHUB_USER
                password = GITHUB_TOKEN
            }
        }
        maven("https://plugins.gradle.org/m2/")
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose) apply false
}