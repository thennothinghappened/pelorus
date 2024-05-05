package org.orca.pelorus.data.services.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.prefs.Prefs
import org.orca.pelorus.data.prefs.usecases.GetSavedCredentialsUseCase
import org.orca.pelorus.data.prefs.usecases.SaveCredentialsUseCase
import org.orca.pelorus.data.services.authed.AuthedServices
import org.orca.pelorus.data.services.authed.IAuthedServices
import org.orca.pelorus.screenmodel.AuthScreenModel
import org.orca.trulysharedprefs.ISharedPrefs

/**
 * Root services instance
 */
class RootServices(
    override val cache: Cache,
    sharedPrefs: ISharedPrefs
) : IRootServices {

    override val mutablePrefs = Prefs(sharedPrefs)
    override val prefs = mutablePrefs

    override val authScreenModel = AuthScreenModel(
        GetSavedCredentialsUseCase(prefs),
        SaveCredentialsUseCase(mutablePrefs)
    )

    @Composable
    override fun rememberAuthedServices(credentials: CompassUserCredentials): IAuthedServices {
        return remember { AuthedServices(credentials) }
    }

}
