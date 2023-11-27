package org.orca.pelorus.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
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
        red = Color(224, 108, 117),
        green = Color(152, 195, 121),
        yellow = Color(229, 192, 123)
    )
}

@Composable
fun PelorusTheme(
    content: @Composable () -> Unit
) {
    val customColours = CustomColours(
        red = Color(224, 108, 117),
        green = Color(152, 195, 121),
        yellow = Color(229, 192, 123)
    )

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors() else lightColors(),
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
