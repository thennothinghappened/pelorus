package org.orca.pelorus.prefs

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
