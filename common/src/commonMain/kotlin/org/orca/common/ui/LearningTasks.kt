package org.orca.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.Compass
import org.orca.common.data.formatAsDateTime
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.BaseCard
import org.orca.common.ui.components.CornersCard
import org.orca.common.ui.components.NetStates
import org.orca.kotlass.data.LearningTask
import org.orca.kotlass.data.LearningTaskSubmissionStatus

class LearningTasksComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickLearningTask: (Int) -> Unit
) : ComponentContext by componentContext

@Composable
fun LearningTasksContent(
    component: LearningTasksComponent
) {
    val learningTasksState by component.compass.defaultLearningTasks.state.collectAsStateAndLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            NetStates(learningTasksState) { taskList ->
                Column {
                    // get the categories
                    val subjects = taskList.map { it.activityId }.distinct()
                    var currentIndex = 0

                    subjects.forEach { subject ->
                        val list = taskList.filter { it.activityId == subject }

                        Text(list[0].subjectName)
                        list.forEach { task ->
                            LearningTaskCard(task, currentIndex, component.onClickLearningTask)
                            currentIndex ++
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LearningTaskCard(
    learningTask: LearningTask,
    learningTaskIndex: Int,
    onClickLearningTask: (Int) -> Unit
) {
    BaseCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClickLearningTask(learningTaskIndex) }
    ) {
        Row {
            Box(modifier = Modifier
                .height(60.dp)
                .width(10.dp)
                .background(
                    when (learningTask.students[0].submissionStatus) {
                        LearningTaskSubmissionStatus.PENDING -> MaterialTheme.colorScheme.inverseOnSurface
                        LearningTaskSubmissionStatus.SUBMITTED_LATE -> Color.Yellow
                        LearningTaskSubmissionStatus.SUBMITTED_ON_TIME -> Color.Green
                        LearningTaskSubmissionStatus.OVERDUE -> Color.Red
                    }
                )
            )
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row {
                    Text(
                        learningTask.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Row {
                    Text(
                        learningTask.dueDateTimestamp?.toLocalDateTime(TimeZone.currentSystemDefault())?.formatAsDateTime() ?: "No due date",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    Box {
                        Text(learningTask.categoryId.toString())
                    }
                }
            }
        }
    }
}