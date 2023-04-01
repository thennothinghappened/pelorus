package org.orca.common.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowScope

@Composable
actual fun DialogBox(
    visible: Boolean,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        visible = visible,
        onCloseRequest = onCloseRequest
    ) {
        content()
    }
}