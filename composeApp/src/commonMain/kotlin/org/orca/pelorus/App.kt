package org.orca.pelorus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import org.orca.pelorus.cache.Cache
import org.orca.pelorus.ui.theme.PelorusAppTheme
import org.orca.pelorus.ui.login.LoginScreen
import org.orca.trulysharedprefs.ISharedPrefs

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App(cache: Cache, prefs: ISharedPrefs) {
    ProvideStrings {
        PelorusAppTheme(isSystemInDarkTheme()) {
            val windowSize = calculateWindowSizeClass()

            Navigator(LoginScreen) { navigator ->
                Scaffold(
                    topBar = {
                        Column {
                            Text(text = "width class")
                            Text(text = windowSize.widthSizeClass.toString())
                            Text(text = "height class")
                            Text(text = windowSize.heightSizeClass.toString())
                        }
                    },
                    content = { paddingValues ->
                        Box(Modifier.padding(paddingValues)) {
                            CurrentScreen()
                        }
                    },

                    bottomBar = {

                    }
                )
            }
        }
    }
}