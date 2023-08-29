package org.orca.common.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.jthemedetecor.OsThemeDetector

@Composable
actual fun getAppColourScheme(
    darkTheme: Boolean,
    dynamicColour: Boolean,
): ColorScheme {
    return when {
        darkTheme -> appDarkColourScheme
        else -> appLightColourScheme
    }
}

@Composable
actual fun getSystemDarkTheme(): Boolean {
    return OsThemeDetector.getDetector().isDark
}