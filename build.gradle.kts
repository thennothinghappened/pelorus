import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

val localProperties = gradleLocalProperties(rootDir)

val gitHubUser = (extra["githubUser"] ?: System.getenv("GITHUB_USER"))?.toString()
val gitHubToken = (extra["githubToken"] ?: System.getenv("GITHUB_TOKEN"))?.toString()

allprojects {
    group = Pelorus.group
    version = Pelorus.version

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")

        if (gitHubUser is String && gitHubToken is String) {
            maven {
                url = uri("https://maven.pkg.github.com/thennothinghappened/kotlass")
                credentials {
                    username = gitHubUser
                    password = gitHubToken
                }
            }
            maven {
                url = uri("https://maven.pkg.github.com/Qawaz/compose-datetime")
                credentials {
                    username = gitHubUser
                    password = gitHubToken
                }
            }
        }
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
    alias(libs.plugins.kotlin.compose) apply false
}
