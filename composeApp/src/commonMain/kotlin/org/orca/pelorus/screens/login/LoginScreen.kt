package org.orca.pelorus.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.orca.pelorus.screens.login.manual.ManualLoginScreen

/**
 * Initial login screen for the application, basically handles top-level authentication and loading from there.
 */
object LoginScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        Column(Modifier.fillMaxSize()) {

            ListItem(
                headlineContent = {
                    Text("Login via Cookie")
                },
                modifier = Modifier.clickable {
                    navigator.push(ManualLoginScreen)
                }
            )

        }

    }

    private fun readResolve(): Any = LoginScreen

}
