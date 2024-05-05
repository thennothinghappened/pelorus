package org.orca.pelorus.data.services.root

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.data.prefs.IMutablePrefs
import org.orca.pelorus.data.prefs.IPrefs
import org.orca.pelorus.data.services.authed.IAuthedServices
import org.orca.pelorus.screenmodel.AuthScreenModel

/**
 * Application root services, a little bit like a DI module!
 *
 * Encompasses services
 */
interface IRootServices {

    /**
     * The application cache repository.
     */
    val cache: Cache

    /**
     * Application-wide preferences.
     */
    val prefs: IPrefs

    /**
     * Editable preferences instance.
     */
    val mutablePrefs: IMutablePrefs

    /**
     * Shared screen model for authenticating and auth status.
     */
    val authScreenModel: AuthScreenModel

    /**
     * Scope to use for running coroutines in the data layer.
     */
    val dataCoroutineScope: CoroutineScope

    /**
     * Get a remembered instance of authed services bound to the given credentials.
     */
    @Composable
    fun rememberAuthedServices(credentials: CompassUserCredentials): IAuthedServices

}
