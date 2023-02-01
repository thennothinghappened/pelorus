package org.orca.common.data

sealed interface Platform {
    object Android : Platform
    object Desktop : Platform
}

expect fun getPlatform(): Platform