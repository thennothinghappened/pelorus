package org.orca.pelorus.data.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import org.orca.pelorus.data.prefs.LocalMutablePrefs
import org.orca.pelorus.data.prefs.LocalPrefs
import org.orca.pelorus.data.prefs.Prefs
import org.orca.trulysharedprefs.ISharedPrefs

/**
 * Provider for top-level services that need to be accessed throughout the application
 * such as preferences.
 *
 * Arguments passed to this function are platform-dependent dependencies.
 */
@Composable
fun WithRootServices(
    sharedPrefs: ISharedPrefs,
    content: @Composable () -> Unit
) {

    val mutablePrefs = remember { Prefs(sharedPrefs) }

    CompositionLocalProvider(
        LocalMutablePrefs provides mutablePrefs,
        LocalPrefs provides mutablePrefs,
        content = content
    )

}
