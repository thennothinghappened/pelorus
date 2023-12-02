package org.orca.pelorus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.ProvideStrings
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import org.orca.kotlass.client.CompassApiClient
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.ui.PelorusAppTheme
import org.orca.trulysharedprefs.ISharedPrefs

private object Compass {
    lateinit var inst: CompassApiClient
        private set


}

@Composable
fun App(cache: Cache, prefs: ISharedPrefs) {
    ProvideStrings {
        PreComposeApp {
            PelorusAppTheme(isSystemInDarkTheme()) {



            }
        }
    }
}