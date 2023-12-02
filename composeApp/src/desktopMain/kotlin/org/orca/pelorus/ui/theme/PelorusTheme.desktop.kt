package org.orca.pelorus.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import org.orca.pelorus.ui.theme.PelorusTheme

@Composable
actual fun PelorusAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colourScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    PelorusTheme(
        colourScheme = colourScheme,
        content = content
    )
}