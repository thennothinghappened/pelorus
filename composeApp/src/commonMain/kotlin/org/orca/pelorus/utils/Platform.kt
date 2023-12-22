package org.orca.pelorus.utils

sealed interface Platform {
    sealed interface Mobile : Platform {
        data object Android : Mobile
    }

    sealed interface Desktop : Platform {
        data object Windows : Desktop
        data object MacOS : Desktop
        data object Linux : Desktop
        data object Other : Desktop

    }
}

expect val platform: Platform