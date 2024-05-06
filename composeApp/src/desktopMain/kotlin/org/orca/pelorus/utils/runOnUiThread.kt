package org.orca.pelorus.utils

import javax.swing.SwingUtilities

/**
 * Force [block] to be executed on the UI thread.
 * Purely used for Decompose.
 *
 * Helper function ripped from the [Decompose Sample Project](https://github.com/arkivanov/Decompose/blob/master/sample/app-desktop/src/jvmMain/kotlin/com/arkivanov/sample/app/Utils.kt)
 */
internal fun <T> runOnUiThread(block: () -> T): T {
    if (SwingUtilities.isEventDispatchThread()) {
        return block()
    }

    var error: Throwable? = null
    var result: T? = null

    SwingUtilities.invokeAndWait {
        try {
            result = block()
        } catch (e: Throwable) {
            error = e
        }
    }

    error?.also { throw it }

    @Suppress("UNCHECKED_CAST")
    return result as T
}
