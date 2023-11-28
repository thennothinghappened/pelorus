package org.orca.pelorus.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColours(
    val red: Color,
    val green: Color,
    val yellow: Color
)

val LocalCustomColours = staticCompositionLocalOf {
    CustomColours(
        red = Color.Red,
        green = Color.Green,
        yellow = Color.Yellow
    )
}

@Composable
fun PelorusTheme(
    colourScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    val customColours = CustomColours(
        red = Color(224, 108, 117),
        green = Color(152, 195, 121),
        yellow = Color(229, 192, 123)
    )

    MaterialTheme(
        colorScheme = colourScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = {
            CompositionLocalProvider(
                LocalCustomColours provides customColours,
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