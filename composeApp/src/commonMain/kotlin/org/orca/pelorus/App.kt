package org.orca.pelorus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.ProvideStrings
import org.koin.compose.KoinContext
import org.orca.pelorus.ui.theme.PelorusAppTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App() {
    KoinContext {
        ProvideStrings {
            PelorusAppTheme(isSystemInDarkTheme()) {
                val windowSize = calculateWindowSizeClass()

                Surface(Modifier.fillMaxSize()) {

                }
            }
        }
    }
}