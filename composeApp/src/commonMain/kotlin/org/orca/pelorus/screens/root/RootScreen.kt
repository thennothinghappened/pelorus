package org.orca.pelorus.screens.root

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.data.user.UserDetails
import org.orca.pelorus.prefs.ui.LocalPrefs
import org.orca.pelorus.screens.login.LoginScreen

/**
 * Root controlling screen encompassing the rest of the app.
 */
@Composable
fun RootScreen(component: IRootComponent) {

    CompositionLocalProvider(LocalPrefs provides component.prefs) {

        when (val state = component.state.collectAsState().value) {

            is IRootComponent.State.NotAuthenticated -> {
                LoginScreen(component::onLoginSuccess)
            }

            is IRootComponent.State.Authenticated -> {
                Column {

                    val client = state.client
                    var response: CompassApiResult<UserDetails>? by remember { mutableStateOf(null) }

                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            response = client.getMyUserDetails()
                        }
                    }

                    when (val r = response) {

                        null -> {
                            CircularProgressIndicator()
                        }

                        is CompassApiResult.Failure -> {
                            Text("Failed to fetch user details:\n${r.error}")
                        }

                        is CompassApiResult.Success -> {

                            val details = r.data

                            Text("Welcome to Compass, ${details.firstName}!")

                        }

                    }

                }
            }
        }
    }
}