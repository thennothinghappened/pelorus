package org.orca.pelorus

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.orca.pelorus.ui.PelorusAppTheme

@Composable
fun App() {
    PelorusAppTheme(darkTheme = isSystemInDarkTheme()) {
        Surface(Modifier.fillMaxSize()) {
            Text("hi!!")
        }
    }
}