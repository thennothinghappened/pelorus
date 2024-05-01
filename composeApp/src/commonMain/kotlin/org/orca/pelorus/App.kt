package org.orca.pelorus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.kotlass.data.user.UserDetails
import org.orca.pelorus.prefs.Prefs
import org.orca.pelorus.screens.login.LoginScreen
import org.orca.pelorus.screens.root.IRootComponent
import org.orca.pelorus.screens.root.RootScreen
import org.orca.pelorus.ui.theme.PelorusAppTheme
import org.orca.trulysharedprefs.ISharedPrefs

/**
 * Main entry point for the app!
 */
@Composable
fun App(component: IRootComponent) {
    PelorusAppTheme {
        Surface(Modifier.fillMaxSize()) {
            RootScreen(component)
        }
    }
}
