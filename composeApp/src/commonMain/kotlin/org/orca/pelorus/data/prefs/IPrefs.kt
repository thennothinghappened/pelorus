package org.orca.pelorus.data.prefs

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.orca.kotlass.client.CompassUserCredentials

/**
 * Preferences for Pelorus.
 */
interface IPrefs {

    /**
     * The stored compass user credentials if any are stored.
     */
    val compassCredentials: StateFlow<CompassUserCredentials?>

    /**
     * Whether we should verify that our login credentials are valid on startup.
     */
    val verifyValidLogin: StateFlow<Boolean>

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

    /**
     * Set whether we should verify that our login credentials are valid on startup.
     */
    fun setVerifyValidLogin(verify: Boolean)

}
