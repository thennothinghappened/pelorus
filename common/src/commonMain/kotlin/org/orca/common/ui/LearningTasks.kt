package org.orca.common.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.data.Compass
import org.orca.common.data.formatAsDateTime
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.FlairedCard
import org.orca.common.ui.components.NetStates
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.data.LearningTask
import org.orca.kotlass.data.LearningTaskSubmissionStatus

class LearningTasksComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickLearningTask: (Int, Int) -> Unit
) : ComponentContext by componentContext

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LearningTasksContent(
    component: LearningTasksComponent
) {
    val learningTasksState by component.compass.defaultLearningTasks.state.collectAsStateAndLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
//        topBar = {
//            OutlinedTextField(
//                searchQuery,
//                { searchQuery = it }
//            )
//        }
    ) { paddingValues ->

        NetStates(learningTasksState) { taskList ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {

                taskList.forEach { subject ->

                    stickyHeader {
                        ElevatedCard(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                subject.value[0].subjectName,
                                Modifier.padding(4.dp)
                            )
                        }
                    }

                    subject.value.forEach { task ->
                        item {
                            LearningTaskCard(
                                task,
                                component.onClickLearningTask,
                                component.compass.defaultTaskCategories
                            )
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
    onClickLearningTask: (Int, Int) -> Unit,
    categories: IFlowKotlassClient.Pollable.TaskCategories
) {
    FlairedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 16.dp),
        flairColor = getLearningTaskColours(learningTask.students[0].submissionStatus).copy(0.6f),
        onClick = { onClickLearningTask(learningTask.activityId, learningTask.id) }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row {
                Text(
                    learningTask.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Row {
                Text(
                    learningTask.dueDateTimestamp?.toLocalDateTime(TimeZone.currentSystemDefault())
                        ?.formatAsDateTime()
                        ?: "No due date",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                val categoriesState by categories.state.collectAsStateAndLifecycle()

                NetStates(categoriesState) { list ->
                    val category = list.find { it.categoryId == learningTask.categoryId }!!

                    OutlinedCard(
                        colors = CardDefaults.cardColors(
                            Color(category.categoryColour),
                            Color.White
                        )
                    ) {
                        Column(modifier = Modifier.padding(4.dp, 2.dp)) {
                            Text(category.categoryName, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getLearningTaskColours(submissionStatus: LearningTaskSubmissionStatus): Color =
    when (submissionStatus) {
        LearningTaskSubmissionStatus.PENDING -> MaterialTheme.colorScheme.inverseOnSurface
        LearningTaskSubmissionStatus.SUBMITTED_LATE -> Color.Yellow
        LearningTaskSubmissionStatus.SUBMITTED_ON_TIME -> Color.Green
        LearningTaskSubmissionStatus.OVERDUE -> Color.Red
    }