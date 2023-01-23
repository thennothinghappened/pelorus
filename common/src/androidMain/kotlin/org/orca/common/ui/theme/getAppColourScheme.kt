package org.orca.common.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getAppColourScheme(
    darkTheme: Boolean,
    dynamicColour: Boolean,
): ColorScheme {
    return when {
        dynamicColour && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> appDarkColourScheme
        else -> appLightColourScheme
    }
}

@Composable
actual fun getSystemDarkTheme(): Boolean {
    return isSystemInDarkTheme()
}
