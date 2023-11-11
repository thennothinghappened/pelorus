package org.orca.common.ui.components.calendar

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.utils.formatAsHourMinute
import org.orca.common.ui.components.common.CornersCard
import org.orca.common.ui.components.common.ErrorRenderer
import org.orca.common.ui.defaults.Padding
import org.orca.kotlass.IFlowKotlassClient

fun LazyListScope.dueLearningTasks(
    scheduleState: IFlowKotlassClient.State<IFlowKotlassClient.Pollable.Schedule.ScheduleStateHolder>,
    onClickTask: (String) -> Unit
) {

    item {
        Text("Due Tasks", style = MaterialTheme.typography.labelMedium)
    }

    when (scheduleState) {
        is IFlowKotlassClient.State.NotInitiated -> {
            // TODO: we'll move polling starting into on first view, maybe.
        }

        is IFlowKotlassClient.State.Loading -> {
            // TODO: nice loading thing here maybe?
        }

        is IFlowKotlassClient.State.Error -> {
            item {
                ErrorRenderer(scheduleState.error)
            }
        }

        is IFlowKotlassClient.State.Success -> {
            dueLearningTasksContent(
                tasks = scheduleState.data.learningTasks,
                onClickTask = onClickTask
            )
        }
    }
}

fun LazyListScope.dueLearningTasksContent(
    tasks: List<IFlowKotlassClient.ScheduleEntry.LearningTask>,
    onClickTask: (String) -> Unit
) {

    if (tasks.isEmpty()) {
        item {
            Text("None today!", style = MaterialTheme.typography.bodySmall)
        }

        return
    }

    items(tasks) { task ->
        val event = task.event

        CornersCard(
            event.title,
            "",
            "Due ${event.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time?.formatAsHourMinute()}",
            "",
            modifier = Modifier
                .height(60.dp)
                .padding(Padding.SpacerInner),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            onClick = { onClickTask(event.title) }
        )
    }
}