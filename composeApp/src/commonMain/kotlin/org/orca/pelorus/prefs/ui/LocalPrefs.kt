package org.orca.pelorus.prefs.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import org.orca.pelorus.prefs.IPrefs
import org.orca.pelorus.screens.root.RootComponent

val LocalPrefs: ProvidableCompositionLocal<IPrefs> = compositionLocalOf {
    error("No IPrefs instance given!")
}

/**
 * The current global app preferences instance.
 *
 * This instance cannot be modified - the modifiable instance is held by the
 * [RootComponent] instance for editing.
 */
val prefs: IPrefs
    @Composable
    get() = LocalPrefs.current
