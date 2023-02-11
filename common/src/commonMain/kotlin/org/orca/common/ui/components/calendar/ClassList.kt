package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.orca.common.ui.components.ClassCard
import org.orca.common.ui.components.NetStates
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient

@Composable
fun ClassList(
    modifier: Modifier = Modifier,
    windowSize: WindowSize,
    schedule: IFlowKotlassClient.Pollable.Schedule,
    onClickActivity: (Int, IFlowKotlassClient.Pollable.Schedule) -> Unit
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
            classes.forEachIndexed { index, it ->
                ClassCard(it) {
                    onClickActivity(index, schedule)
                }
            }
        }
    }
}

