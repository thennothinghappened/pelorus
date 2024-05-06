package org.orca.pelorus.screens.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.orca.pelorus.data.di.rootServices
import org.orca.pelorus.screenmodel.AuthScreenModel
import org.orca.pelorus.ui.utils.collectValue

/**
 * The loading screen when you are logging in.
 */
object LoginLoadingScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val authModel = rootServices.authScreenModel
        val state = authModel.state.collectValue()

        if (state is AuthScreenModel.State.FailedAuthenticate) {
            navigator.popUntil { screen -> screen !is LoginLoadingScreen }
            return
        }

        Box(Modifier.fillMaxSize()) {

            Column(Modifier.align(Alignment.Center)) {

                Text("Logging into Compass...")
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))

            }

        }

    }

    private fun readResolve(): Any = LoginLoadingScreen

}