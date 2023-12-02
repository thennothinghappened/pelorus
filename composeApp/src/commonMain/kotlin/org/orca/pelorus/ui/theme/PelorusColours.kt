package org.orca.pelorus.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class PelorusColours(
    val red: Color = Color(224, 108, 117),
    val green: Color = Color(152, 195, 121),
    val yellow: Color = Color(229, 192, 123)
)

val LocalPelorusColours = staticCompositionLocalOf {
    PelorusColours(
        red = Color.Unspecified,
        green = Color.Unspecified,
        yellow = Color.Unspecified
    )
}

@Composable
fun GetPelorusColours(): PelorusColours = PelorusColours(
    red = Color(224, 108, 117),
    green = Color(152, 195, 121),
    yellow = Color(229, 192, 123)
)

val colours: PelorusColours
    @Composable
    get() = LocalPelorusColours.current