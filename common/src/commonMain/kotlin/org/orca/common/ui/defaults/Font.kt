package org.orca.common.ui.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

object Font {
    val topAppBar
        @Composable
        get() = MaterialTheme.typography.titleMedium
    val filterChip
        @Composable
        get() = MaterialTheme.typography.labelMedium
}