
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

val gitHubUser = (extra["githubUser"] ?: System.getenv("GITHUB_USER"))?.toString()
val gitHubToken = (extra["githubToken"] ?: System.getenv("GITHUB_TOKEN"))?.toString()

dependencyResolutionManagement {
    repositories {

        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

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
