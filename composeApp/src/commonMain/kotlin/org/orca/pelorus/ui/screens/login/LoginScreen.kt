package org.orca.pelorus.ui.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import org.orca.pelorus.data.prefs.PrefsKeys
import org.orca.trulysharedprefs.ISharedPrefs

@Composable
fun LoginScreen(
    prefs: ISharedPrefs,
    onLoginSuccess: (CompassUserCredentials) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    screenModel: LoginScreenModel = remember { LoginScreenModel(coroutineScope) },
) {

    val loginState by screenModel.loginState.collectAsState()

    // On successful login.
    if (loginState is LoginScreenModel.LoginState.Authenticated) {

        (loginState as LoginScreenModel.LoginState.Authenticated).credentials.let {

            prefs.editSync {
                putString(PrefsKeys.CompassDomain.key, it.domain)
                putInt(PrefsKeys.CompassUserId.key, it.userId)
                putString(PrefsKeys.CompassCookie.key, it.cookie)
            }

            onLoginSuccess(it)

        }

    }

    val savedDomain = prefs.getStringOrNull(PrefsKeys.CompassDomain.key)
    val savedUserId = prefs.getIntOrNull(PrefsKeys.CompassUserId.key)
    val savedCookie = prefs.getStringOrNull(PrefsKeys.CompassCookie.key)

    val loading = loginState is LoginScreenModel.LoginState.Loading

    var domain by rememberSaveable { mutableStateOf(savedDomain ?: "") }
    var userId by rememberSaveable { mutableStateOf(savedUserId) }
    var cookie by rememberSaveable { mutableStateOf(savedCookie ?: "") }

    Column {

        TextField(
            value = domain,
            onValueChange = { domain = it },
            label = { Text("Domain") },
            readOnly = loading
        )

        TextField(
            value = (userId ?: "").toString(),
            onValueChange = { userId = it.toIntOrNull() },
            label = { Text("User ID") },
            readOnly = loading,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        TextField(
            value = cookie,
            onValueChange = { cookie = it },
            label = { Text("Cookie") },
            readOnly = loading
        )

        HorizontalDivider()

        when (loginState) {

            is LoginScreenModel.LoginState.Loading -> {
                CircularProgressIndicator()
                return@Column
            }

            else -> {
                Button(
                    onClick = {
                        screenModel.tryAuthenticate(CompassUserCredentials(
                            domain = domain,
                            userId = userId!!,
                            cookie = cookie
                        ))
                    },
                    enabled = userId != null
                ) {
                    Text("Login")
                }
            }

        }

    }
}

/**
 * Screen Model for the login screen handling logins.
 */
class LoginScreenModel(private val coroutineScope: CoroutineScope) {

    sealed interface LoginState {

        /**
         * Have not tried to authenticate.
         */
        data object NotAuthenticated : LoginState

        /**
         * Currently trying to authenticate.
         */
        data object Loading : LoginState

        /**
         * Successfully authenticated!
         */
        data class Authenticated(val credentials: CompassUserCredentials) : LoginState

        /**
         * Failed to authenticate.
         */
        data class FailedAuthenticate(val error: CompassApiError) : LoginState
    }

    private val mutableLoginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.NotAuthenticated)

    /**
     * The current state for if we've authenticated successfully.
     */
    val loginState = mutableLoginState.asStateFlow()

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

            mutableLoginState.update { LoginState.Loading }

            val client = CompassApiClient(credentials)

            val result = withContext(Dispatchers.IO) {
                client.checkAuth()
            }

            mutableLoginState.update {
                when (result) {
                    is CompassApiResult.Success -> LoginState.Authenticated(credentials)
                    is CompassApiResult.Failure -> LoginState.FailedAuthenticate(result.error)
                }
            }

        }

    }

}

