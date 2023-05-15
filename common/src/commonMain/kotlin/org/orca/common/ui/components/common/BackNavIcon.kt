package org.orca.common.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun BackNavIcon(
    onBackPress: () -> Unit
) {
    IconButton(
        onClick = onBackPress
    ) {
        Icon(Icons.Default.ArrowBack, "Back")
    }
}