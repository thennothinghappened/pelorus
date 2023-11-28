package org.orca.pelorus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.ProvideStrings
import cafe.adriel.lyricist.strings
import org.orca.pelorus.ui.PelorusAppTheme

@Composable
fun App() {

    ProvideStrings {
        PelorusAppTheme(darkTheme = isSystemInDarkTheme()) {
            Surface(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize()) {
                    Text(strings.hi, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}