package org.orca.common.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance

expect fun getAppColourScheme(darkTheme: Boolean, dynamicColour: Boolean): ColorScheme
expect fun getSystemDarkTheme(): Boolean

@Composable
fun AppTheme(
    darkTheme: Boolean = getSystemDarkTheme(),
    dynamicColour: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = getAppColourScheme(darkTheme, dynamicColour),
        content = content
    )
}

@Composable
fun ColorScheme.isLight() = this.background.luminance() > 0.5

val appDarkColourScheme = darkColorScheme()
val appLightColourScheme = lightColorScheme()