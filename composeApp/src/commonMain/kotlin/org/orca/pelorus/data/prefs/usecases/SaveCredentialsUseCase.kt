package org.orca.pelorus.data.prefs.usecases

import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.data.prefs.IMutablePrefs

/**
 * Use case for saving login credentials to the preferences store.
 */
class SaveCredentialsUseCase(private val mutablePrefs: IMutablePrefs) {

    operator fun invoke(credentials: CompassUserCredentials?) {

        if (credentials == null) {
            return mutablePrefs.clearCompassCredentials()
        }

        mutablePrefs.setCompassCredentials(credentials)
    }

}
