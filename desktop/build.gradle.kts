import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvmToolchain(18)
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "18"
        }
        withJava()
    }
    sourceSets {
        val decomposeVersion = "1.0.0"
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
                implementation("com.arkivanov.decompose:decompose:$decomposeVersion")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "pelorus"
            packageVersion = version.toString().split("-")[0] // Deb files are super picky
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
//            isEnabled.set(false)
        }
    }
}