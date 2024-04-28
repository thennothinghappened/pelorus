package org.orca.pelorus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.orca.pelorus.ui.login.LoginScreen
import org.orca.pelorus.ui.theme.PelorusAppTheme

@Composable
fun App() {
    PelorusAppTheme(isSystemInDarkTheme()) {
        Surface(Modifier.fillMaxSize()) {
            Navigator(LoginScreen) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}
