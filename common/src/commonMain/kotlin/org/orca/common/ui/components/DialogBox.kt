package org.orca.common.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun DialogBox(
    visible: Boolean,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit
)