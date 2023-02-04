package org.orca.common.ui.components

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.formatAsHourMinute
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.data.Activity

@Composable
fun ClassCard(
    scheduleEntry: CompassApiClient.ScheduleEntry.ActivityEntry,
    onClick: () -> Unit = {}
) {
    val activity by scheduleEntry.activity.collectAsState()
    val bannerUrl by scheduleEntry.bannerUrl.collectAsState()
    val time = scheduleEntry.event.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time?.formatAsHourMinute() ?: ""

    if (activity is CompassApiClient.State.Success<Activity>) {
        CornersCard(
            (activity as CompassApiClient.State.Success<Activity>).data.subjectName,
            "Room ${(activity as CompassApiClient.State.Success<Activity>).data.locationName}",
            (activity as CompassApiClient.State.Success<Activity>).data.managerTextReadable,
            time,
            onClick = onClick
        )
    }
    else
        CornersCard(
            scheduleEntry.event.longTitleWithoutTime,
            "loading",
            "loading",
            time,
            onClick = onClick
        )
}