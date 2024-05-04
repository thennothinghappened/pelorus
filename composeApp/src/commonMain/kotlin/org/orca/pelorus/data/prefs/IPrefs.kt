package org.orca.pelorus.data.prefs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import org.orca.kotlass.client.CompassUserCredentials

/**
 * Preferences for Pelorus.
 */
interface IPrefs {

    /**
     * Get the stored compass user credentials if any are stored.
     */
    fun getCompassCredentials(): CompassUserCredentials?

}

/**
 * Pelorus preferences that can be edited.
 */
interface IMutablePrefs : IPrefs {

    /**
     * Set the stored compass user credentials.
     */
    fun setCompassCredentials(credentials: CompassUserCredentials)

    /**
     * Reset the stored compass user credentials.
     */
    fun clearCompassCredentials()

}

/**
 * Provided [IMutablePrefs] instance for the application, that can modify the preferences.
 * This is only needed in cases where we actually need to modify them.
 */
val LocalMutablePrefs = staticCompositionLocalOf<IMutablePrefs> {
    error("No MutablePrefs provided!!")
}

/**
 * Provided [IPrefs] instance for the application.
 */
val LocalPrefs = staticCompositionLocalOf<IPrefs> {
    error("No MutablePrefs provided!!")
}
