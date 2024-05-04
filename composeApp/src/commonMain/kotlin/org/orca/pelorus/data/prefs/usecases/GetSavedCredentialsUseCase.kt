package org.orca.pelorus.data.prefs.usecases

import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.data.prefs.IMutablePrefs
import org.orca.pelorus.data.prefs.IPrefs

/**
 * Use case for getting the saved credentials.
 */
class GetSavedCredentialsUseCase(private val prefs: IPrefs) {
    operator fun invoke(): CompassUserCredentials? = prefs.getCompassCredentials()
}
