package org.orca.pelorus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.orca.kotlass.client.CompassApiClient
import org.orca.pelorus.ui.screens.login.LoginScreen
import org.orca.pelorus.ui.theme.PelorusAppTheme
import org.orca.trulysharedprefs.ISharedPrefs

/**
 * Main entry point for the app!
 */
@Composable
fun App(prefs: ISharedPrefs) {

    PelorusAppTheme {

        Surface(Modifier.fillMaxSize()) {

            var client: CompassApiClient? by remember { mutableStateOf(null) }

            if (client == null) {

                LoginScreen(prefs, { credentials ->
                    client = CompassApiClient(credentials)
                })

                return@Surface

            }

            Column {
                Text("Authenticated with client $client!")
            }


        }

    }

}

