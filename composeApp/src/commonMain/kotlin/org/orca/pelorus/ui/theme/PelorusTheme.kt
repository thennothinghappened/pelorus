package org.orca.pelorus.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun PelorusTheme(
    colourScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    val pelorusSizing = GetPelorusSizing()
    val pelorusColours = GetPelorusColours()

    MaterialTheme(
        colorScheme = colourScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = {
            CompositionLocalProvider(
                LocalPelorusSizing provides pelorusSizing,
                LocalPelorusColours provides  pelorusColours,
                content = content
            )
        }
    )
}

@Composable
expect fun PelorusAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
)