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

    var title = scheduleEntry.event.longTitleWithoutTime
    val time = scheduleEntry.event.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time?.formatAsHourMinute() ?: ""
    var teacher = ""
    var room = ""

    if (activity is CompassApiClient.State.Success<Activity>) {
        title = (activity as CompassApiClient.State.Success<Activity>).data.subjectName
        teacher = (activity as CompassApiClient.State.Success<Activity>).data.managerTextReadable
        room = (activity as CompassApiClient.State.Success<Activity>).data.locationName
    }
    CornersCard(
        title,
        "Room $room",
        teacher,
        time,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.background
        )
    )
}