package org.orca.common.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
actual fun DesktopBackButton(onBackPress: () -> Unit) {
    Button(onClick = onBackPress) {
        Icon(Icons.Default.ArrowBack, "Back")
    }
}