package org.orca.common.data

enum class Platform {
    ANDROID,
    DESKTOP
}

expect fun getPlatform(): Platform