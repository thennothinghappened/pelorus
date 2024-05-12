package org.orca.pelorus.ui.components.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalTime
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.ui.theme.sizing

/**
 * A displayed calendar event.
 */
@Composable
fun CalendarEvent(event: CalendarEventData) {
    CalendarEvent(
        title = event.activity?.name ?: event.event.title,
        staffName = "${event.staff.firstName} ${event.staff.lastName}",
        startTime = event.event.start,
        finishTime = event.event.finish
    )
}

/**
 * A displayed calendar event.
 */
@Composable
fun CalendarEvent(
    title: String,
    staffName: String,
    startTime: LocalTime,
    finishTime: LocalTime,
    modifier: Modifier = Modifier
) {

    Card(modifier) {
        Column(Modifier.fillMaxWidth().padding(sizing.paddingCardInnerSmall)) {

            Row {
                Text(title)
                Spacer(Modifier.weight(1f))
                Text(staffName)
            }

            Row {
                Spacer(Modifier.weight(1f))
                Text("$startTime - $finishTime")
            }

        }
    }

}
