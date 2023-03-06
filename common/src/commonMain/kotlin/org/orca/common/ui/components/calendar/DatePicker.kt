package org.orca.common.ui.components.calendar

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DatePickerDialog(
    visible: Boolean,
    onClose: () -> Unit,
    onComplete: (LocalDate) -> Unit
) {
//    Dialog(
//        onCloseRequest = onClose,
//        visible = visible
//    ) {
//        Text("yes")
//        Button(onClick = {
//            onComplete(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
//        }) {
//            Text("Submit")
//        }
//    }
}