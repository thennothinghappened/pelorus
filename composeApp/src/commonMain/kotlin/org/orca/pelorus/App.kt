package org.orca.pelorus

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import cafe.adriel.voyager.transitions.SlideTransition
import org.orca.pelorus.data.di.WithAuthedServices
import org.orca.pelorus.data.di.authedServices
import org.orca.pelorus.data.di.rootServices
import org.orca.pelorus.data.services.root.RootServices
import org.orca.pelorus.screenmodel.AuthScreenModel
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.screens.login.LoginLoadingScreen
import org.orca.pelorus.screens.login.LoginScreen
import org.orca.pelorus.screens.root.RootScreen
import org.orca.pelorus.ui.theme.PelorusAppTheme
import org.orca.pelorus.ui.utils.collectValue

/**
 * Main entry point for the app!
 */
@Composable
fun App() {
    PelorusAppTheme {
        Surface(Modifier.fillMaxSize()) {

            Navigator(RootScreen) { navigator ->

                val authModel = rootServices.authScreenModel
                val authState = authModel.state.collectValue()

                if (authState is AuthScreenModel.State.Success) {

                    if (navigator.lastItem !is AuthenticatedScreen) {
                        navigator.replaceAll(RootScreen)
                        return@Navigator
                    }

                    WithAuthedServices(rootServices.rememberAuthedServices(authState.credentials)) {
                        SlideTransition(navigator)
                    }

                } else {

                    if (navigator.lastItem is AuthenticatedScreen) {

                        navigator.push(
                            if (authState is AuthScreenModel.State.Loading) {
                                LoginLoadingScreen
                            } else {
                                LoginScreen
                            }
                        )

                        return@Navigator
                    }

                    ScaleTransition(navigator)

                }

            }
        }
    }
}
