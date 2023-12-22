package org.orca.pelorus.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PelorusSizing(
    val spacerVeryLarge: Dp,
    val spacerLarge: Dp,
    val spacerMedium: Dp,

    val paddingCardInner: Dp,
    val paddingCardInnerSmall: Dp,
    val paddingContainerInner: Dp,

    val elevationActionCard: Dp
)

val LocalPelorusSizing: ProvidableCompositionLocal<PelorusSizing> = staticCompositionLocalOf {
    error("Missing PelorusSizing instance!")
}

@Composable
fun GetPelorusSizing(): PelorusSizing = PelorusSizing(
    spacerVeryLarge = 32.dp,
    spacerLarge = 16.dp,
    spacerMedium = 8.dp,

    paddingCardInner = 16.dp,
    paddingCardInnerSmall = 8.dp,
    paddingContainerInner = 32.dp,

    elevationActionCard = 32.dp
)

val sizing: PelorusSizing
    @Composable
    get() = LocalPelorusSizing.current