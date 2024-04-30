package org.orca.pelorus.ui.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassApiError
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.data.prefs.IPrefs

@Composable
fun LoginScreen(
    prefs: IPrefs,
    onLoginSuccess: (CompassUserCredentials) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    screenModel: LoginScreenModel = remember { LoginScreenModel(coroutineScope) },
) {

    var domain: String by rememberSaveable { mutableStateOf("") }
    var userId: Int? by rememberSaveable { mutableStateOf(null) }
    var cookie: String by rememberSaveable { mutableStateOf("") }

    Column {

        when (val loginState = screenModel.state.collectAsState().value) {

            is LoginScreenModel.State.Authenticated -> {
                return onLoginSuccess(loginState.credentials)
            }

            is LoginScreenModel.State.Loading -> {

                Box(Modifier.fillMaxSize()) {
                    Column(Modifier.align(Alignment.Center)) {
                        Text("Logging into Compass...")
                        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                    }
                }

                return

            }

            is LoginScreenModel.State.NotAuthenticated -> {

                // If we already have credentials saved, try logging in immediately.
                prefs.getCompassCredentials()?.let { credentials ->
                    return LaunchedEffect(Unit) {
                        screenModel.tryAuthenticate(credentials)
                    }
                }

            }

            is LoginScreenModel.State.FailedAuthenticate -> {
                Text(
                    text = "Failed to authenticate with the Compass Server:\n${loginState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }


        }

        TextField(
            value = domain,
            onValueChange = { domain = it },
            label = { Text("Domain") }
        )

        TextField(
            value = (userId ?: "").toString(),
            onValueChange = { userId = it.toIntOrNull() },
            label = { Text("User ID") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        TextField(
            value = cookie,
            onValueChange = { cookie = it },
            label = { Text("Cookie") }
        )

        HorizontalDivider()

        Button(
            onClick = {
                screenModel.tryAuthenticate(CompassUserCredentials(
                    domain = domain,
                    userId = userId!!,
                    cookie = cookie
                ))
            },
            enabled = (userId != null)
        ) {
            Text("Login")
        }

    }
}

/**
 * Screen Model for the login screen handling logins.
 */
class LoginScreenModel(private val coroutineScope: CoroutineScope) {

    private val mutableState: MutableStateFlow<State> = MutableStateFlow(State.NotAuthenticated)

    /**
     * The current state for if we've authenticated successfully.
     */
    val state = mutableState.asStateFlow()

    /**
     * Job checking authentication currently if so.
     */
    private var authCheckJob: Job? = null

    /**
     * Attempt to authenticate with Compass.
     */
    fun tryAuthenticate(credentials: CompassUserCredentials) {

        if (authCheckJob?.isActive == true) {
            authCheckJob?.cancel()
        }

        authCheckJob = coroutineScope.launch {

            mutableState.update { State.Loading }

            val client = CompassApiClient(credentials)

            val result = withContext(Dispatchers.IO) {
                client.checkAuth()
            }

            mutableState.update {
                when (result) {
                    is CompassApiResult.Success -> State.Authenticated(credentials)
                    is CompassApiResult.Failure -> State.FailedAuthenticate(result.error)
                }
            }

        }

    }

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
         * Successfully authenticated!
         */
        data class Authenticated(val credentials: CompassUserCredentials) : State

        /**
         * Failed to authenticate.
         */
        data class FailedAuthenticate(val error: CompassApiError) : State
    }

}

