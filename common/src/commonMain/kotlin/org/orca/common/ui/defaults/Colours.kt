package org.orca.common.ui.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object Colours {
    val topBarBackground
        @Composable
        get() = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
}