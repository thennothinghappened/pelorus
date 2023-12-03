package org.orca.pelorus.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PelorusSizing(
    val spacerLarge: Dp,

    val paddingCardInner: Dp
)

val LocalPelorusSizing: ProvidableCompositionLocal<PelorusSizing> = staticCompositionLocalOf {
    error("Missing PelorusSizing instance!")
}

@Composable
fun GetPelorusSizing(): PelorusSizing = PelorusSizing(
    spacerLarge = 16.dp,
    paddingCardInner = 16.dp
)

val sizing: PelorusSizing
    @Composable
    get() = LocalPelorusSizing.current