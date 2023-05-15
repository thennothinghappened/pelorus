package org.orca.common.ui.components.common

import androidx.compose.runtime.Composable

@Composable
expect fun DialogBox(
    visible: Boolean,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit
)