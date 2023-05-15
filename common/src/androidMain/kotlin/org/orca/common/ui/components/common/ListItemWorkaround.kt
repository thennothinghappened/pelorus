package org.orca.common.ui.components.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
@ExperimentalMaterial3Api
actual fun ListItemWorkaround(
    leadingContent: (@Composable () -> Unit)?,
    headlineText: @Composable () -> Unit,
    supportingText: (@Composable () -> Unit)?,
    trailingContent: (@Composable () -> Unit)?,
    modifier: Modifier
) {
    ListItem(
        leadingContent = leadingContent,
        headlineContent = headlineText,
        supportingContent = supportingText,
        trailingContent = trailingContent,
        modifier = modifier
    )
}