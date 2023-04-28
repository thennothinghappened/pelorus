package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.CornersCard
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.data.Activity

@Composable
fun ClassCard(
    scheduleEntry: IFlowKotlassClient.ScheduleEntry,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    val startTime = scheduleEntry.event.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time
    val endTime = scheduleEntry.event.finish?.toLocalDateTime(TimeZone.currentSystemDefault())?.time

    var title = scheduleEntry.event.longTitleWithoutTime
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

    if (scheduleEntry is IFlowKotlassClient.ScheduleEntry.ActivityEntry) {

        val activity by scheduleEntry.activity.collectAsStateAndLifecycle()

        if (activity is IFlowKotlassClient.State.Success<Activity>) {
            title = (activity as IFlowKotlassClient.State.Success<Activity>).data.subjectName
                ?: (activity as IFlowKotlassClient.State.Success<Activity>).data.activityDisplayName
            teacher = (activity as IFlowKotlassClient.State.Success<Activity>).data.managerTextReadable
            room = (activity as IFlowKotlassClient.State.Success<Activity>).data.locationDetails?.longName
                ?: (activity as IFlowKotlassClient.State.Success<Activity>).data.locationName
        }
    }

    ClassCard(
        title,
        room,
        teacher,
        startTime,
        endTime,
        scheduleEntry.event.allDay,
        onClick,
        modifier,
        colors
    )
}

@Composable
private fun ClassCard(
    title: String,
    room: String,
    teacher: String,
    startTime: LocalTime?,
    endTime: LocalTime?,
    allDay: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors()
) {
    CornersCard(
        title,
        room,
        teacher,
        if (allDay) "All Day" else "$startTime - $endTime",
        modifier.fillMaxWidth(),
        onClick = onClick,
        colors = colors
    )
}