package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import org.orca.common.data.toInstant
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient
import java.lang.Integer.max

enum class ScheduleHolderType {
    allDay,
    normal
}

@Composable
fun ClassList(
    modifier: Modifier = Modifier,
    windowSize: WindowSize,
    schedule: IFlowKotlassClient.Pollable.Schedule,
    onClickActivity: (Int, ScheduleHolderType, IFlowKotlassClient.Pollable.Schedule) -> Unit,
    onClickEvent: (Int, ScheduleHolderType, IFlowKotlassClient.Pollable.Schedule) -> Unit,
    experimentalClassList: Boolean,
    _schoolStartTime: LocalTime,
    date: LocalDate
) {
    val scheduleState by schedule.state.collectAsState()

    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Schedule", style = MaterialTheme.typography.labelMedium)

        NetStates(scheduleState) { state ->

            if (state.normal.isEmpty() && state.allDay.isEmpty()) {
                Text("None today!", style = MaterialTheme.typography.bodySmall)
                return@NetStates
            }

            state.allDay.forEachIndexed { index, current ->
                ClassCard(
                    current,
                    Modifier.height(48.dp),
                    onClick = if (current is IFlowKotlassClient.ScheduleEntry.Event) {
                        { onClickEvent(index, ScheduleHolderType.normal, schedule) }
                    } else {
                        { onClickActivity(index, ScheduleHolderType.normal, schedule) }
                    }
                )
            }

            if (!experimentalClassList) {
                state.normal.forEachIndexed { index, current ->
                    ClassCard(current, Modifier.height(65.dp)) {
                        onClickActivity(index, ScheduleHolderType.normal, schedule)
                    }
                }
            } else {
                // calculate school start time for the day
                val schoolStartTime = _schoolStartTime.toInstant(date)

                // calculate spacing based on time between classes
                val spacing = state.normal.mapIndexed { index, current ->

                    if (current.event.start == null || current.event.finish == null)
                        return@mapIndexed listOf(0.dp, 0.dp)

                    // make sure we have values
                    if (index == 0)
                        return@mapIndexed listOf(
                            max((current.event.start!! - schoolStartTime).inWholeMinutes.toInt(), 0).dp,
                            (current.event.finish!! - current.event.start!!).inWholeMinutes.toInt().dp
                        )

                    // get the previous class
                    val previous = state.normal[index-1].event

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

                state.normal.forEachIndexed { index, it ->
                    ClassCard(it, Modifier
                        .padding(top = spacing[index][0])
                        .height(spacing[index][1]),
                        onClick = if (it is IFlowKotlassClient.ScheduleEntry.Event) {
                            { onClickEvent(index, ScheduleHolderType.normal, schedule) }
                        } else {
                            { onClickActivity(index, ScheduleHolderType.normal, schedule) }
                        }
                    )
                }
            }
        }
    }
}

