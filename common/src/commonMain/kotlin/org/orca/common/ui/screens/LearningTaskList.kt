package org.orca.common.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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
import org.orca.common.ui.components.common.FlairedCard
import org.orca.common.ui.components.common.NetStates
import org.orca.common.ui.components.learningtasks.LearningTaskFilterChip
import org.orca.common.ui.defaults.Colours
import org.orca.common.ui.defaults.Font
import org.orca.common.ui.defaults.Padding
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.data.LearningTask
import org.orca.kotlass.data.LearningTaskSubmissionStatus

class LearningTaskListComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickLearningTask: (Int, Int) -> Unit,
    activityFilter: Set<Int> = setOf(-1),
    statusFilter: Set<LearningTaskSubmissionStatus> = setOf(
        LearningTaskSubmissionStatus.PENDING,
        LearningTaskSubmissionStatus.SUBMITTED_LATE,
        LearningTaskSubmissionStatus.SUBMITTED_ON_TIME,
        LearningTaskSubmissionStatus.OVERDUE
    )
) : ComponentContext by componentContext {

    private val _state = stateKeeper.consume("LEARNING_TASKS_STATE") ?: State(activityFilter, statusFilter)

    private val _activityFilter: MutableStateFlow<Set<Int>> = MutableStateFlow(_state.activityFilter)
    val activityFilter: StateFlow<Set<Int>> = _activityFilter

    private val _statusFilter: MutableStateFlow<Set<LearningTaskSubmissionStatus>> = MutableStateFlow(_state.statusFilter)
    val statusFilter: StateFlow<Set<LearningTaskSubmissionStatus>> = _statusFilter

    fun setActivityFilter(value: Set<Int>) {
        _state.activityFilter = value
        _activityFilter.value = value
    }

    fun addActivityFilter(value: Int) {
        _state.activityFilter = _state.activityFilter.plus(value)
        _activityFilter.value = _state.activityFilter
    }

    fun removeActivityFilter(value: Int) {
        _state.activityFilter = _state.activityFilter.minus(value)
        _activityFilter.value = _state.activityFilter
    }

    fun setStatusFilter(value: Set<LearningTaskSubmissionStatus>) {
        _state.statusFilter = value
        _statusFilter.value = value
    }

    fun addStatusFilter(value: LearningTaskSubmissionStatus) {
        _state.statusFilter = _state.statusFilter.plus(value)
        _statusFilter.value = _state.statusFilter
    }

    fun removeStatusFilter(value: LearningTaskSubmissionStatus) {
        _state.statusFilter = _state.statusFilter.minus(value)
        _statusFilter.value = _state.statusFilter
    }

    init {
        stateKeeper.register("LEARNING_TASKS_STATE") { State(activityFilter, statusFilter) }
    }

    @Parcelize
    private class State(
        var activityFilter: Set<Int>,
        var statusFilter: Set<LearningTaskSubmissionStatus>
    ) : Parcelable
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LearningTaskListContent(
    component: LearningTaskListComponent
) {
    val learningTasksState by component.compass.defaultLearningTasks.state.collectAsStateAndLifecycle()
    val activityFilter by component.activityFilter.collectAsStateAndLifecycle()
    val statusFilter by component.statusFilter.collectAsStateAndLifecycle()

    NetStates(learningTasksState) { taskList ->

        val subjectNames = taskList
            .map { it.key to it.value[0].subjectName }
            .associate { it.first to it.second }

        if (activityFilter.contains(-1)) {
            // got default config so add all of them
            component.setActivityFilter(subjectNames.keys)
        }

        Scaffold { paddingValues ->

            val filteredTaskList =
                taskList
                    .filter { activityFilter.contains(it.key) }
                    .map { subject ->
                        subject.key to subject.value.filter { task ->
                            statusFilter.contains(task.students[0].submissionStatus)
                        }
                    }
                    .associate {
                        it.first to it.second
                    }

            LazyColumn(
                modifier = Modifier.padding(paddingValues)
            ) {

                item {
                    Column {
                        // Subject filtering row //
                        Box(Modifier.fillMaxWidth()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Padding.ChipListSpacing),
                                modifier = Modifier.align(Alignment.TopCenter)
                            ) {
                                // Select all //
                                item {
                                    LearningTaskFilterChip(
                                        selected = activityFilter.containsAll(subjectNames.keys),
                                        onClick = {
                                            if (activityFilter.containsAll(subjectNames.keys)) {
                                                component.setActivityFilter(emptySet())
                                            } else {
                                                component.setActivityFilter(subjectNames.keys)
                                            }
                                        },
                                        label = "All"
                                    )
                                }
                                // All subjects //
                                items(subjectNames.toList()) { subject ->
                                    LearningTaskFilterChip(
                                        selected = activityFilter.contains(subject.first),
                                        onClick = {
                                            if (activityFilter.contains(subject.first)) {
                                                component.removeActivityFilter(subject.first)
                                            } else {
                                                component.addActivityFilter(subject.first)
                                            }
                                        },
                                        label = subject.second
                                    )
                                }
                            }
                        }

                        // Status filtering row //
                        Box(Modifier.fillMaxWidth()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(Padding.ChipListSpacing),
                                modifier = Modifier.align(Alignment.TopCenter)
                            ) {
                                items(LearningTaskSubmissionStatus.values()) { status ->
                                    LearningTaskFilterChip(
                                        selected = statusFilter.contains(status),
                                        onClick = {
                                            if (statusFilter.contains(status)) {
                                                component.removeStatusFilter(status)
                                            } else {
                                                component.addStatusFilter(status)
                                            }
                                        },
                                        label = learningTaskStatusNames[status]!!,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = getLearningTaskColours(status),
                                            selectedLabelColor = if (getLearningTaskColours(status).luminance() >= 0.2)
                                                Colours.TopBarBackground else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                filteredTaskList.forEach { subject ->

                    // Header for each subject
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
                        ) {
                            Text(
                                subjectNames[subject.key]!!,
                                modifier = Modifier.padding(4.dp),
                                style = Font.infoSmall
                            )
                        }
                    }

                    // TODO: weird behaviour here, sometimes items don't appear until recomposition triggered by user.
                    items(subject.value, key = { task -> task.id }) { task ->
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

@Composable
fun LearningTaskCard(
    learningTask: LearningTask,
    onClickLearningTask: (Int, Int) -> Unit,
    categories: IFlowKotlassClient.Pollable.TaskCategories
) {
    FlairedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp),
        flairColor = getLearningTaskColours(learningTask.students[0].submissionStatus),
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

private val learningTaskStatusNames = mapOf(
    LearningTaskSubmissionStatus.PENDING to "Pending",
    LearningTaskSubmissionStatus.SUBMITTED_LATE to "Late",
    LearningTaskSubmissionStatus.SUBMITTED_ON_TIME to "On time",
    LearningTaskSubmissionStatus.OVERDUE to "Overdue"
)

@Composable
fun getLearningTaskColours(submissionStatus: LearningTaskSubmissionStatus): Color =
    when (submissionStatus) {
        LearningTaskSubmissionStatus.PENDING -> MaterialTheme.colorScheme.inverseOnSurface
        LearningTaskSubmissionStatus.SUBMITTED_LATE -> Colours.Yellow
        LearningTaskSubmissionStatus.SUBMITTED_ON_TIME -> Colours.Green
        LearningTaskSubmissionStatus.OVERDUE -> Colours.Red
    }