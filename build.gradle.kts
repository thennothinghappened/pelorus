
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
    }
}

plugins {
    kotlin("multiplatform") version "1.8.0" apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
}