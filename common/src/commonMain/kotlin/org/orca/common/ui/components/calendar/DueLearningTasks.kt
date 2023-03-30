package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.formatAsHourMinute
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.CornersCard
import org.orca.common.ui.components.NetStates
import org.orca.kotlass.FlowKotlassClient
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.KotlassClient
import org.orca.kotlass.dummy.createDummyFlowsClient

@Composable
fun DueLearningTasks(
    modifier: Modifier = Modifier,
    schedule: IFlowKotlassClient.Pollable.Schedule,
    onClickTask: (String) -> Unit
) {
    val scheduleState by schedule.state.collectAsStateAndLifecycle()

    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Due Tasks", style = MaterialTheme.typography.labelMedium)
        NetStates(scheduleState) { state ->

            if (state.learningTasks.isEmpty()) {
                Text("None today!", style = MaterialTheme.typography.bodySmall)
            }

            state.learningTasks.forEach { task ->
                val event = task.event

                CornersCard(
                    event.title,
                    "",
                    "Due ${event.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time?.formatAsHourMinute()}",
                    "",
                    modifier = Modifier.height(65.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    onClick = { onClickTask(
                        event.title
                    ) }
                )
            }
        }
    }
}