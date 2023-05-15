package org.orca.common.ui.components.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ListItemWorkaround(
    leadingContent: (@Composable () -> Unit)? = null,
    headlineText: @Composable () -> Unit,
    supportingText: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
//    colors: ListItemColors = ListItemDefaults.colors(),
    modifier: Modifier = Modifier
)