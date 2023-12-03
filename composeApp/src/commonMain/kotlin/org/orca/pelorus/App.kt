package org.orca.pelorus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.KoinContext
import org.orca.pelorus.ui.login.LoginScreen
import org.orca.pelorus.ui.theme.PelorusAppTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App() {
    KoinContext {
        ProvideStrings {
            PelorusAppTheme(isSystemInDarkTheme()) {
                Surface(Modifier.fillMaxSize()) {
                    Navigator(LoginScreen)
                }
            }
        }
    }
}