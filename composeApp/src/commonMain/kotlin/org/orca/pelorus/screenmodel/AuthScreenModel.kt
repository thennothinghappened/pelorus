package org.orca.pelorus.screenmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassApiError
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.data.prefs.LocalMutablePrefs
import org.orca.pelorus.data.prefs.LocalPrefs
import org.orca.pelorus.data.prefs.usecases.GetSavedCredentialsUseCase
import org.orca.pelorus.data.prefs.usecases.SaveCredentialsUseCase
import org.orca.pelorus.screenmodel.AuthScreenModel.*

/**
 * Shared ScreenModel for Compass authentication state.
 */
class AuthScreenModel(
    private val getCredentials: GetSavedCredentialsUseCase,
    private val saveCredentials: SaveCredentialsUseCase
) : StateScreenModel<State>(State.NotAuthenticated) {

    init {

        val savedCredentials = getCredentials()

        if (savedCredentials != null) {
            tryLogin(savedCredentials)
        }

    }

    /**
     * Attempt to authenticate with Compass.
     */
    fun tryLogin(credentials: CompassUserCredentials) {

        screenModelScope.launch {

            mutableState.update { State.Loading }

            val client = CompassApiClient(credentials)

            val result = withContext(Dispatchers.IO) {
                client.checkAuth()
            }

            when (result) {

                is CompassApiResult.Failure -> mutableState.update { State.FailedAuthenticate(result.error) }

                is CompassApiResult.Success -> {
                    saveCredentials(credentials)
                    mutableState.update { State.Success(credentials) }
                }

            }

        }

    }

    /**
     * Log out - clear credentials.
     */
    fun logout() {
        saveCredentials(null)
        mutableState.update { State.NotAuthenticated }
    }

    companion object : DependentScreenModel<AuthScreenModel>() {

        @Composable
        override fun instantiate(): AuthScreenModel {

            val prefs = LocalPrefs.current
            val mutablePrefs = LocalMutablePrefs.current
            val getSavedCredentials = remember { GetSavedCredentialsUseCase(prefs) }
            val saveCredentials = remember { SaveCredentialsUseCase(mutablePrefs) }

            return AuthScreenModel(getSavedCredentials, saveCredentials)

        }

        context (Screen)
        @Composable
        override fun getForScreen(): AuthScreenModel {
            val screenModel = instantiate()
            return rememberScreenModel { screenModel }
        }

        @Composable
        override fun getForNavigator(): AuthScreenModel {

            val screenModel = instantiate()
            return navigator.rememberNavigatorScreenModel { screenModel }
        }

    }

    /**
     * State for the login screen.
     */
    sealed interface State {

        /**
         * Have not tried to authenticate.
         */
        data object NotAuthenticated : State

        /**
         * Currently trying to authenticate.
         */
        data object Loading : State

        /**
         * Failed to authenticate.
         */
        data class FailedAuthenticate(val error: CompassApiError) : State

        /**
         * Successful authentication.
         */
        data class Success(val credentials: CompassUserCredentials) : State

    }

}
