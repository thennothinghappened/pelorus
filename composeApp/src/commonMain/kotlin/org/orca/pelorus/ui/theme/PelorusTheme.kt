package org.orca.pelorus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

val LocalWindowSizeClass: ProvidableCompositionLocal<WindowSizeClass> = compositionLocalOf {
    error("No window size class provided!")
}

val windowSize: WindowSizeClass
    @Composable
    get() = LocalWindowSizeClass.current

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
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
                LocalPelorusColours provides pelorusColours,
                content = {
                    // This is really silly!
                    val windowSizeClass = calculateWindowSizeClass()

                    CompositionLocalProvider(
                        LocalWindowSizeClass provides windowSizeClass,
                        content = content
                    )
                }
            )
        }
    )
}

@Composable
expect fun PelorusAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
)
