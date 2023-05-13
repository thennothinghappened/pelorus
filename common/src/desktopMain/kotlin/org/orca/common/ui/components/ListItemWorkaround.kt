package org.orca.common.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@ExperimentalMaterial3Api
@Composable
actual fun ListItemWorkaround(
    headlineText: @Composable () -> Unit,
    supportingText: (@Composable () -> Unit)?,
    trailingContent: (@Composable () -> Unit)?,
    modifier: Modifier
) = ListItem(
    headlineText = headlineText,
    supportingText = supportingText,
    trailingContent = trailingContent,
    modifier = modifier
)