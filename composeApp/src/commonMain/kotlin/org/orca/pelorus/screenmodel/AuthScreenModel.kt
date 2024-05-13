package org.orca.pelorus.screenmodel

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassApiError
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.data.prefs.IMutablePrefs
import org.orca.pelorus.data.usecases.LogOutUseCase
import org.orca.pelorus.screenmodel.AuthScreenModel.State

/**
 * Shared ScreenModel for Compass authentication state.
 */
class AuthScreenModel(
    private val mutablePrefs: IMutablePrefs,
    private val logOut: LogOutUseCase
) : StateScreenModel<State>(State.NotAuthenticated) {

    init {

        mutablePrefs.compassCredentials.value
            ?.let { credentials ->

                if (mutablePrefs.verifyValidLogin.value) {
                    tryLogin(credentials)
                } else {
                    mutableState.update { State.Success(credentials) }
                }

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
                    mutablePrefs.setCompassCredentials(credentials)
                    mutableState.update { State.Success(credentials) }
                }

            }

        }

    }

    /**
     * Log out - clear credentials.
     */
    fun logout() {
        logOut()
        mutableState.update { State.NotAuthenticated }
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
