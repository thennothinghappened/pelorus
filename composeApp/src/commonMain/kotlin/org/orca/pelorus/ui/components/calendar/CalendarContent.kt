package org.orca.pelorus.ui.components.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.util.fastForEach
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.ui.theme.sizing
import pelorus.composeapp.generated.resources.Res
import pelorus.composeapp.generated.resources.calendar_title

/**
 * Heading for the calendar date.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun CalendarHeading(date: LocalDate) {
    Text("${stringResource(Res.string.calendar_title)} - $date", style = MaterialTheme.typography.titleLarge)
}

/**
 * Reused calendar content.
 */
@Composable
fun CalendarContent(events: List<CalendarEventData>, user: UserDetails) {
    Column(
        verticalArrangement = Arrangement.spacedBy(sizing.spacerMedium)
    ) {
        events
            .filter { it.event.studentId == user.id }
            .sortedBy { it.event.start }
            .fastForEach { CalendarEvent(it) }
    }

}
