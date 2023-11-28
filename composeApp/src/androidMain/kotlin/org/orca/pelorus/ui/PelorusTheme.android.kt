package org.orca.pelorus.ui

import android.os.Build
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun PelorusAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colourScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        LocalContext.current.let { context ->
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
    } else {
        if (darkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }
    }

    PelorusTheme(
        colourScheme = colourScheme,
        content = content
    )
}