import org.gradle.api.JavaVersion

/**
 * Configuration for Pelorus
 */
object Pelorus {
    const val name = "Pelorus"
    const val group = "org.orca"
    const val version = "1.6.11-BETA"

    object Android {
        const val versionCode = 30
        const val namespace = "$group.android"

        object Sdk {
            const val min = 23
            const val compile = 34
        }
    }

    object Jvm {
        val version = JavaVersion.VERSION_18
        val versionNumber = version.majorVersion.toInt()
    }
}