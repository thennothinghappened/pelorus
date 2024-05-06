package org.orca.pelorus.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.orca.pelorus.ui.theme.sizing

@Composable
fun MediumHorizontalDivider() {
    HorizontalDivider(Modifier.padding(sizing.spacerMedium))
}
