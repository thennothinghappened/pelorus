package org.orca.pelorus.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PelorusSizing(
    val spacerLarge: Dp
)

val LocalPelorusSizing = staticCompositionLocalOf {
    PelorusSizing(
        spacerLarge = Dp.Unspecified
    )
}

@Composable
fun GetPelorusSizing(): PelorusSizing = PelorusSizing(
    spacerLarge = 16.dp
)

val sizing: PelorusSizing
    @Composable
    get() = LocalPelorusSizing.current