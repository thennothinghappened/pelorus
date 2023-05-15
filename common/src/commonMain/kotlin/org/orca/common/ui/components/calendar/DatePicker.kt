package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wakaztahir.datetime.date.DatePicker
import com.wakaztahir.datetime.date.rememberDatePickerState
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.ui.components.common.DialogBox

@Composable
fun DatePickerDialog(
    visible: Boolean,
    startDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    onClose: () -> Unit,
    onComplete: (LocalDate) -> Unit
) {
    DialogBox(
        onCloseRequest = onClose,
        visible = visible
    ) {
        val datePickerState = rememberDatePickerState(startDate)

        Surface {
            Column {
                DatePicker(
                    title = "Select Day",
                    state = datePickerState
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button({ onClose() }) {
                        Text("Close")
                    }

                    Spacer(Modifier.width(16.dp))

                    Button({ onComplete(datePickerState.selected) }) {
                        Text("Select")
                    }
                }
            }
        }
    }
}