package org.orca.pelorus.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalTime
import org.orca.pelorus.ui.components.calendar.CalendarEvent
import org.orca.pelorus.ui.theme.PelorusAppTheme
import org.orca.pelorus.ui.theme.sizing

@Composable
@Preview
private fun CalendarEventPreview() {

    PelorusAppTheme {
        Surface(Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier.padding(sizing.paddingContainerInner),
                verticalArrangement = Arrangement.spacedBy(sizing.spacerMedium)
            ) {

                repeat(5) {

                    CalendarEvent(
                        title = "Test event",
                        staffName = "Teacher",
                        startTime = LocalTime(0, 0, 0, 0),
                        finishTime = LocalTime(0, 0, 0, 0)
                    )

                }

            }

        }
    }

}
