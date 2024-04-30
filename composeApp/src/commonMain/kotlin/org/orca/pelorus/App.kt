package org.orca.pelorus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.data.prefs.Prefs
import org.orca.pelorus.ui.screens.login.LoginScreen
import org.orca.pelorus.ui.theme.PelorusAppTheme
import org.orca.trulysharedprefs.ISharedPrefs

/**
 * Main entry point for the app!
 */
@Composable
fun App(sharedPrefs: ISharedPrefs) {

    PelorusAppTheme {
        Surface(Modifier.fillMaxSize()) {

            val screenModel = remember { AppScreenModel(sharedPrefs) }
            val state by screenModel.state.collectAsState()

            when (val it = state) {

                is AppScreenModel.State.NotAuthenticated -> {
                    LoginScreen(screenModel.prefs, screenModel::onLoginSuccess)
                }

                is AppScreenModel.State.Authenticated -> {
                    Column {
                        Text("Authenticated with client ${it.client}!")
                    }
                }
            }

        }
    }

}

/**
 * The root screen model for the application.
 */
class AppScreenModel(sharedPrefs: ISharedPrefs) {

    /**
     * Application preferences.
     */
    val prefs = Prefs(sharedPrefs)

    private val mutableState: MutableStateFlow<State> = MutableStateFlow(State.NotAuthenticated)

    /**
     * Most top-level application state.
     */
    val state = mutableState.asStateFlow()

    /**
     * Callback for a successful login.
     */
    fun onLoginSuccess(credentials: CompassUserCredentials) {

        prefs.setCompassCredentials(credentials)
        mutableState.update { State.Authenticated(CompassApiClient(credentials)) }

    }

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
