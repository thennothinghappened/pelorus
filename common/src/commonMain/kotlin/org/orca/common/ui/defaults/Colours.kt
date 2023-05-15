package org.orca.common.ui.defaults

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.orca.common.ui.theme.isLight

object Colours {
    val TopBarBackground
        @Composable
        get() = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)

    val Green = Color(152, 195, 121)
    val Red = Color(224, 108, 117)
    val Yellow = Color(229, 192, 123)
}