import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties
import kotlin.io.path.toPath

rootProject.name = "pelorus"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

val properties = File(rootDir
    .resolve("local.properties")
    .toURI()).let {
    if (it.isFile) {
        val properties = Properties()

        InputStreamReader(FileInputStream(it), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }

        properties
    } else null
}

val gitHubUser: String? = properties?.getProperty("GITHUB_USER") ?: System.getenv("GITHUB_USER")
val gitHubToken: String? = properties?.getProperty("GITHUB_TOKEN") ?: System.getenv("GITHUB_TOKEN")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://repo1.maven.org/maven2")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jogamp.org/deployment/maven/")

        if (gitHubUser != null && gitHubToken != null) {
            maven {
                url = uri("https://maven.pkg.github.com/thennothinghappened/kotlass")
                credentials {
                    username = gitHubUser
                    password = gitHubToken
                }
            }
        }
    }
}

include(":composeApp")