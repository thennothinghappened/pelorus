package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import kotlinx.datetime.*
import org.orca.common.ui.components.common.DialogBox
import org.orca.common.ui.defaults.Font
import org.orca.common.ui.defaults.Padding
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    visible: Boolean,
    startDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    onClose: () -> Unit,
    onComplete: (LocalDate) -> Unit
) {
    if (!visible) {
        return
    }

    AlertDialog(onDismissRequest = onClose) {
        val datePickerState = rememberDatePickerState(startDate
            .atTime(0, 0, 0, 0)
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds())

        Surface {
            Column(Modifier.padding(Padding.ScaffoldInner)) {
                DatePicker(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(Padding.Divider),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Select Day", style = Font.topAppBar)
                        }
                    },
                    state = datePickerState
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClose) {
                        Text("Close", style = Font.button)
                    }

                    Spacer(Modifier.width(16.dp))

                    Button({
                        when (val date = datePickerState.selectedDateMillis?.let {
                            Instant.fromEpochMilliseconds(it)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date
                        }) {
                            null -> onClose()
                            else -> onComplete(date)
                        }
                    }) {
                        Text("Select", style = Font.button)
                    }
                }
            }
        }
    }
}