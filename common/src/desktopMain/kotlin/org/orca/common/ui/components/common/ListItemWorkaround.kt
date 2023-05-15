package org.orca.common.ui.components.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ListItemWorkaround(
    leadingContent: (@Composable () -> Unit)?,
    headlineText: @Composable () -> Unit,
    supportingText: (@Composable () -> Unit)?,
    trailingContent: (@Composable () -> Unit)?,
//    colors: ListItemColors,
    modifier: Modifier
) = ListItem(
    leadingContent = leadingContent,
    headlineText = headlineText,
    supportingText = supportingText,
    trailingContent = trailingContent,
//    colors = colors,
    modifier = modifier
)