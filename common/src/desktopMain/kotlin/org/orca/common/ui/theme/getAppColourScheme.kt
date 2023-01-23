package org.orca.common.ui.theme

import androidx.compose.material3.ColorScheme
import com.jthemedetecor.OsThemeDetector

actual fun getAppColourScheme(
    darkTheme: Boolean,
    dynamicColour: Boolean,
): ColorScheme {
    return when {
        darkTheme -> appDarkColourScheme
        else -> appLightColourScheme
    }
}

actual fun getSystemDarkTheme(): Boolean {
    return OsThemeDetector.getDetector().isDark
}