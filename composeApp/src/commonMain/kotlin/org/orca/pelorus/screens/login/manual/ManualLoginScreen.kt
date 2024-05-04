package org.orca.pelorus.screens.login.manual

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.orca.pelorus.data.di.LocalAuthenticator
import org.orca.pelorus.screenmodel.AuthScreenModel
import org.orca.pelorus.screens.login.LoginLoadingScreen
import org.orca.pelorus.ui.theme.sizing
import org.orca.pelorus.ui.utils.collectValue

/**
 * Screen for manually logging in by pasting the user cookie & other info from the browser dev inspector.
 */
object ManualLoginScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val authModel = LocalAuthenticator.current
        val screenModel = ManualLoginScreenModel.getForScreen()

        val authState = authModel.state.collectValue()
        val state = screenModel.state.collectValue()

        Box(Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(sizing.spacerLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(IntrinsicSize.Min)
            ) {

                if (authState is AuthScreenModel.State.FailedAuthenticate) {

                    var showError by remember { mutableStateOf(false) }

                    Card(
                        onClick = { showError = !showError },
                        colors = CardDefaults.cardColors()
                            .copy(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(Modifier.padding(sizing.paddingCardInner)) {
                            Text(
                                "Failed to login!",
                                style = MaterialTheme.typography.titleMedium
                            )

                            AnimatedVisibility(showError) {

                                HorizontalDivider()
                                Text(authState.error.toString())

                            }
                        }
                    }
                }

                TextField(
                    value = state.domain,
                    onValueChange = screenModel::updateDomain,
                    label = { Text("Domain") }
                )

                TextField(
                    value = state.userId,
                    onValueChange = screenModel::updateUserId,
                    label = { Text("User ID") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                TextField(
                    value = state.cookie,
                    onValueChange = screenModel::updateCookie,
                    label = { Text("Cookie") }
                )

                Button(
                    onClick = {
                        screenModel.credentials?.let { credentials ->
                            navigator.push(LoginLoadingScreen)
                            authModel.tryLogin(credentials)
                        }
                    },
                    enabled = screenModel.credentials != null
                ) {
                    Text("Login")
                }
            }
        }

    }

    private fun readResolve(): Any = ManualLoginScreen

}
