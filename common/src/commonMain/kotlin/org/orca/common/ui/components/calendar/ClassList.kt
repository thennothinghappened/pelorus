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
import org.orca.common.ui.components.ClassCard
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient
import java.lang.Integer.max

@Composable
fun ClassList(
    modifier: Modifier = Modifier,
    windowSize: WindowSize,
    schedule: IFlowKotlassClient.Pollable.Schedule,
    onClickActivity: (Int, IFlowKotlassClient.Pollable.Schedule) -> Unit,
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
        NetStates(scheduleState) { entries ->
            val classes = entries.filterIsInstance<IFlowKotlassClient.ScheduleEntry.ActivityEntry>()

            if (classes.isEmpty()) {
                Text("None today!", style = MaterialTheme.typography.bodySmall)
            }

            if (!experimentalClassList) {
                classes.forEachIndexed { index, it ->
                    ClassCard(it, Modifier.height(65.dp)) {
                        onClickActivity(index, schedule)
                    }
                }
            } else {
                // calculate school start time for the day
                val schoolStartTime = _schoolStartTime.toInstant(date)

                // calculate spacing based on time between classes
                val spacing = classes.mapIndexed { index, current ->

                    if (current.event.start == null || current.event.finish == null)
                        return@mapIndexed listOf(0.dp, 0.dp)

                    // make sure we have values
                    if (index == 0)
                        return@mapIndexed listOf(
                            max((current.event.start!! - schoolStartTime).inWholeMinutes.toInt(), 0).dp,
                            (current.event.finish!! - current.event.start!!).inWholeMinutes.toInt().dp
                        )

                    // get the previous class
                    val previous = classes[index-1].event

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

                classes.forEachIndexed { index, it ->
                    ClassCard(it, Modifier
                        .padding(top = spacing[index][0])
                        .height(spacing[index][1])
                    ) {
                        onClickActivity(index, schedule)
                    }
                }
            }
        }
    }
}

