package org.orca.pelorus.screens.root

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.prefs.IMutablePrefs
import org.orca.pelorus.prefs.IPrefs
import org.orca.pelorus.prefs.Prefs
import org.orca.pelorus.screens.root.IRootComponent.*
import org.orca.trulysharedprefs.ISharedPrefs

/**
 * The root component for the application.
 */
interface IRootComponent {

    /**
     * Application preferences.
     */
    val prefs: IPrefs

    /**
     * Most top-level application state.
     */
    val state: StateFlow<State>

    /**
     * Callback for a successful login.
     */
    fun onLoginSuccess(credentials: CompassUserCredentials)

    /**
     * Global application state.
     */
    sealed interface State {

        /**
         * Have not tried to authenticate.
         */
        data object NotAuthenticated : State

        /**
         * Successfully authenticated and ready!
         */
        data class Authenticated(val client: CompassApiClient) : State

    }

}

class RootComponent(
    componentContext: ComponentContext,
    sharedPrefs: ISharedPrefs
) : IRootComponent, ComponentContext by componentContext {

    /**
     * Editable instance of app preferences.
     */
    private val mutablePrefs: IMutablePrefs = Prefs(sharedPrefs)

    override val prefs: IPrefs
        get() = mutablePrefs

    private val mutableState: MutableStateFlow<State> = MutableStateFlow(State.NotAuthenticated)
    override val state = mutableState.asStateFlow()

    override fun onLoginSuccess(credentials: CompassUserCredentials) {

        updateCompassCredentials(credentials)
        mutableState.update { State.Authenticated(CompassApiClient(credentials)) }

    }

    /**
     * Update the stored compass credentials if they have been modified.
     */
    private fun updateCompassCredentials(credentials: CompassUserCredentials) {
        if (prefs.getCompassCredentials() != credentials) {
            mutablePrefs.setCompassCredentials(credentials)
        }
    }

}
