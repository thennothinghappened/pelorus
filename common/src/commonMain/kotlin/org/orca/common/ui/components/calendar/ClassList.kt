package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import org.orca.common.data.toInstant
import org.orca.common.ui.components.common.ErrorRenderer
import org.orca.common.ui.defaults.Padding
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient
import java.lang.Integer.max

enum class ScheduleHolderType {
    ALL_DAY,
    NORMAL
}

fun LazyListScope.classList(
    modifier: Modifier = Modifier,
    windowSize: WindowSize,
    scheduleState: IFlowKotlassClient.State<IFlowKotlassClient.Pollable.Schedule.ScheduleStateHolder>,
    onClickActivity: (Int, ScheduleHolderType) -> Unit,
    onClickEvent: (Int, ScheduleHolderType) -> Unit,
    experimentalClassList: Boolean,
    _schoolStartTime: LocalTime,
    date: LocalDate
) {
    item {
        Text("Schedule", style = MaterialTheme.typography.labelMedium)

        Spacer(Modifier.height(Padding.SpacerInner))
    }

    when (scheduleState) {
        is IFlowKotlassClient.State.NotInitiated -> {
            // TODO: we'll move polling starting into on first view, maybe.
        }

        is IFlowKotlassClient.State.Loading -> {
            item {
                // We can expect school days to usually be 6 hours, and we're already measuring in minutes.dp!
                Box(Modifier
                    .height((60 * 6).dp)
                    .fillMaxWidth()
                ) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
        }

        is IFlowKotlassClient.State.Error -> {
            item {
                ErrorRenderer(scheduleState.error)
            }
        }

        is IFlowKotlassClient.State.Success -> {
            val data = scheduleState.data

            classListContent(
                normal = data.normal,
                allDay = data.allDay,
                onClickActivity = onClickActivity,
                onClickEvent = onClickEvent,
                experimentalClassList = experimentalClassList,
                _schoolStartTime = _schoolStartTime,
                date = date
            )
        }
    }
}

fun LazyListScope.classListContent(
    normal: List<IFlowKotlassClient.ScheduleEntry>,
    allDay: List<IFlowKotlassClient.ScheduleEntry>,
    onClickActivity: (Int, ScheduleHolderType) -> Unit,
    onClickEvent: (Int, ScheduleHolderType) -> Unit,
    experimentalClassList: Boolean,
    _schoolStartTime: LocalTime,
    date: LocalDate
) {

    item {
        Spacer(Modifier.height(Padding.SpacerInner))
    }

    if (normal.isEmpty() && allDay.isEmpty()) {
        item {
            Text("None today!", style = MaterialTheme.typography.bodySmall)
        }

        return
    }

    items(allDay) { current ->
        val index = allDay.indexOf(current)

        ClassCard(
            current,
            Modifier.height(48.dp),
            onClick = if (current is IFlowKotlassClient.ScheduleEntry.Event) {
                { onClickEvent(index, ScheduleHolderType.NORMAL) }
            } else {
                { onClickActivity(index, ScheduleHolderType.NORMAL) }
            }
        )
    }

    if (!experimentalClassList) {
        items(normal) { current ->
            val index = normal.indexOf(current)

            Column(Modifier.padding(vertical = 8.dp)) {
                ClassCard(current, Modifier.height(65.dp)) {
                    onClickActivity(index, ScheduleHolderType.NORMAL)
                }
            }
        }
    } else {
        // calculate school start time for the day
        val schoolStartTime = _schoolStartTime.toInstant(date)

        // calculate spacing based on time between classes
        val spacing = normal.mapIndexed { index, current ->

            if (current.event.start == null || current.event.finish == null)
                return@mapIndexed listOf(0.dp, 0.dp)

            // make sure we have values
            if (index == 0)
                return@mapIndexed listOf(
                    max((current.event.start!! - schoolStartTime).inWholeMinutes.toInt(), 0).dp,
                    (current.event.finish!! - current.event.start!!).inWholeMinutes.toInt().dp
                )

            // get the previous class
            val previous = normal[index - 1].event

            // make sure it has a finish time
            if (previous.finish == null) return@mapIndexed listOf(
                0.dp,
                (current.event.finish!! - current.event.start!!).inWholeMinutes.toInt().dp
            )

            // compare the finish time with the current class' start time
            return@mapIndexed listOf(
                max((current.event.start!! - previous.finish!!).inWholeMinutes.toInt(), 0).dp,
                (current.event.finish!! - current.event.start!!).inWholeMinutes.toInt().dp
            )
        }

        items(normal) { current ->
            val index = normal.indexOf(current)

            ClassCard(current, Modifier
                .padding(top = spacing[index][0])
                .height(spacing[index][1]),
                onClick = if (current is IFlowKotlassClient.ScheduleEntry.Event) {
                    { onClickEvent(index, ScheduleHolderType.NORMAL) }
                } else {
                    { onClickActivity(index, ScheduleHolderType.NORMAL) }
                }
            )
        }
    }
}