package org.orca.common.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.statekeeper.consume
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val onClickLearningTask: (Int, Int) -> Unit,
    activityFilter: Int? = null
) : ComponentContext by componentContext {

    private val _state = stateKeeper.consume("LEARNING_TASKS_STATE") ?: State(activityFilter)

    private val _activityFilter: MutableStateFlow<Int?> = MutableStateFlow(_state.activityFilter)
    val activityFilter: StateFlow<Int?> = _activityFilter

    fun setActivityFilter(value: Int?) {
        _state.activityFilter = value
        _activityFilter.value = value
    }

    init {
        stateKeeper.register("LEARNING_TASKS_STATE") { State(activityFilter) }
    }

    @Parcelize
    private class State(
        var activityFilter: Int?
    ) : Parcelable
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LearningTasksContent(
    component: LearningTasksComponent
) {
    val learningTasksState by component.compass.defaultLearningTasks.state.collectAsStateAndLifecycle()
    val activityFilter by component.activityFilter.collectAsStateAndLifecycle()

    NetStates(learningTasksState) { taskList ->

        val subjectNames = taskList
            .map { it.key to it.value[0].subjectName }
            .associate { it.first to it.second }

        // ensure activityFilter is valid
        if (activityFilter != null && subjectNames[activityFilter] == null) {
            component.setActivityFilter(null)
        }

        Scaffold(
            topBar = {
                var dropdownExpanded by remember { mutableStateOf(false) }

                // Dropdown to choose a subject to filter by.
                Box {
                    TextField(
                        value = if (activityFilter == null) "All"
                                else subjectNames[activityFilter] ?: "All",
                        onValueChange = {  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                dropdownExpanded = !dropdownExpanded
                            },
                        enabled = false
                    )

                    DropdownMenu(
                        dropdownExpanded,
                        { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuItem(
                            { Text("All") },
                            {
                                dropdownExpanded = false
                                component.setActivityFilter(null)
                            }
                        )

                        subjectNames.forEach { subject ->
                            DropdownMenuItem(
                                { Text(subject.value) },
                                {
                                    dropdownExpanded = false
                                    component.setActivityFilter(subject.key)
                                }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->

            val filteredTaskList =
                if (activityFilter != null) taskList.filter { it.key == activityFilter }
                else taskList

            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {

                filteredTaskList.forEach { subject ->

                    // Header for each subject
                    stickyHeader {
                        ElevatedCard(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                subjectNames[subject.key]!!,
                                Modifier.padding(4.dp)
                            )
                        }
                    }

                    // TODO: weird behaviour here, sometimes items don't appear until recomposition triggered by user.
                    subject.value.forEach { task ->
                        item(task.id) {
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