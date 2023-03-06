package org.orca.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.formatAsHourMinute
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.data.Activity

@Composable
fun ClassCard(
    scheduleEntry: IFlowKotlassClient.ScheduleEntry.ActivityEntry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val activity by scheduleEntry.activity.collectAsState()
    val bannerUrl by scheduleEntry.bannerUrl.collectAsState()

    val startTime = scheduleEntry.event.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time?.formatAsHourMinute()
    val endTime = scheduleEntry.event.finish?.toLocalDateTime(TimeZone.currentSystemDefault())?.time?.formatAsHourMinute()

    var title = scheduleEntry.event.longTitleWithoutTime
    val time = "$startTime - $endTime"
    var teacher = ""
    var room = ""
    var colors = CardDefaults.cardColors()

    if (scheduleEntry.event.finish != null && scheduleEntry.event.finish!! > Clock.System.now())
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.background
        )

    if (scheduleEntry is IFlowKotlassClient.ScheduleEntry.Event)
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.background
        )

    if (activity is IFlowKotlassClient.State.Success<Activity>) {
        title = (activity as IFlowKotlassClient.State.Success<Activity>).data.subjectName ?: (activity as IFlowKotlassClient.State.Success<Activity>).data.activityDisplayName
        teacher = (activity as IFlowKotlassClient.State.Success<Activity>).data.managerTextReadable
        room = (activity as IFlowKotlassClient.State.Success<Activity>).data.locationName
    }
    CornersCard(
        title,
        "Room $room",
        teacher,
        time,
        modifier.fillMaxWidth(),
        onClick = onClick,
        colors = colors
    )
}