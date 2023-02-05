package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.formatAsHourMinute
import org.orca.common.ui.components.CornersCard
import org.orca.common.ui.components.ErrorRenderer
import org.orca.common.ui.components.NetStates
import org.orca.kotlass.CompassApiClient

@Composable
fun DueLearningTasks(
    modifier: Modifier = Modifier,
    scheduleState: CompassApiClient.State<List<CompassApiClient.ScheduleEntry>>
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Due Tasks", style = MaterialTheme.typography.labelMedium)
        NetStates(
            scheduleState,
            { CircularProgressIndicator() },
            { error -> ErrorRenderer(error) }
        ) { entries ->
            val tasks = entries.filterIsInstance<CompassApiClient.ScheduleEntry.LearningTask>()

            tasks.forEach {
                val event = it.event

                CornersCard(
                    event.title,
                    event.description,
                    "Due ${event.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time?.formatAsHourMinute()}",
                    ""
                )
            }
        }
    }
}