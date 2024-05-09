package org.orca.pelorus.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.screens.login.manual.ManualLoginScreen
import org.orca.pelorus.ui.common.MediumHorizontalDivider
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.login_cookie
import pelorus.composeapp.generated.resources.login_cookie_desc
import pelorus.composeapp.generated.resources.login_tagline
import pelorus.composeapp.generated.resources.login_welcome

/**
 * Initial login screen for the application, basically handles top-level authentication and loading from there.
 */
object LoginScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        Column(Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.login_welcome),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = stringResource(Res.string.login_tagline),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            MediumHorizontalDivider()

            Column {

                LoginOption(
                    title = stringResource(Res.string.login_cookie),
                    description = stringResource(Res.string.login_cookie_desc),
                    onClick = {
                        navigator.push(ManualLoginScreen)
                    }
                )

            }

        }

    }

    /**
     * A card providing an option on how to log in.
     */
    @Composable
    private fun LoginOption(
        title: String,
        description: String,
        onClick: () -> Unit
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleSmall
                )
            },
            modifier = Modifier.clickable {
                onClick()
            }
        )
    }

    private fun readResolve(): Any = LoginScreen

}
