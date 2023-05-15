package org.orca.common.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
actual fun DialogBox(
    visible: Boolean,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    if (visible)
        Dialog(
            onDismissRequest = onCloseRequest
        ) {
            content()
        }
}