package org.orca.common.ui.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

object Font {
    val topAppBar
        @Composable
        get() = MaterialTheme.typography.titleMedium

    val title
        @Composable
        get() = MaterialTheme.typography.titleMedium

    val filterChip
        @Composable
        get() = MaterialTheme.typography.labelMedium
    val button
        @Composable
        get() = MaterialTheme.typography.labelLarge

    val settingLabel
        @Composable
        get() = MaterialTheme.typography.labelLarge
            .copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    val infoSmall
        @Composable
        get() = MaterialTheme.typography.labelLarge
}