package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.*
import org.orca.common.data.Compass
import org.orca.common.data.formatAsVisualDate
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.calendar.classList
import org.orca.common.ui.components.calendar.ScheduleHolderType
import org.orca.common.ui.components.calendar.dueLearningTasks
import org.orca.common.ui.components.common.ShortDivider
import org.orca.common.ui.components.newsfeed.newsfeed
import org.orca.common.ui.defaults.Font
import org.orca.common.ui.defaults.Padding
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient

class HomeComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickActivity: (Int, ScheduleHolderType, IFlowKotlassClient.Pollable.Schedule) -> Unit,
    val onClickEvent: (Int, ScheduleHolderType, IFlowKotlassClient.Pollable.Schedule) -> Unit,
    val onClickLearningTask: (String) -> Unit,
    val experimentalClassList: Boolean,
    val schoolStartTime: LocalTime
) : ComponentContext by componentContext

@Composable
fun HomeContent(
    component: HomeComponent,
    modifier: Modifier = Modifier,
    windowSize: WindowSize
) {
    val newsfeedState by component.compass.defaultNewsfeed.state.collectAsStateAndLifecycle()
    val scheduleState by component.compass.defaultSchedule.state.collectAsStateAndLifecycle()
    val date = remember {
            Clock
                .System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                .formatAsVisualDate()
    }

    var selectedNewsItem: Int? by remember { mutableStateOf(null) }

    when (windowSize) {
        WindowSize.EXPANDED -> {
            Row(Modifier.fillMaxSize()) {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(Padding.ScaffoldInner)
                ) {

                    item {
                        Text(date, style = Font.title)
                    }

                    classList(
                        windowSize = windowSize,
                        scheduleState = scheduleState,
                        onClickActivity = { index, type ->
                            component.onClickActivity(
                                index,
                                type,
                                component.compass.defaultSchedule
                            )
                        },
                        onClickEvent = { index, type ->
                            component.onClickEvent(
                                index,
                                type,
                                component.compass.defaultSchedule
                            )
                        },
                        experimentalClassList = component.experimentalClassList,
                        _schoolStartTime = component.schoolStartTime,
                        date = Clock
                            .System
                            .now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                    )

                    item {
                        Spacer(Modifier.height(Padding.SpacerInner))
                        ShortDivider()
                        Spacer(Modifier.height(Padding.SpacerInner))
                    }

                    dueLearningTasks(
                        scheduleState = scheduleState,
                        onClickTask = component.onClickLearningTask
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(Padding.ScaffoldInner),
                    verticalArrangement = Arrangement.spacedBy(Padding.Divider)
                ) {
                    newsfeed(
                        newsfeedState = newsfeedState,
                        selectedNewsItem = selectedNewsItem,
                        buildDomainUrlString = component.compass::buildDomainUrlString,
                        onClickItem = { index ->
                            selectedNewsItem = if (index == selectedNewsItem) null else index
                        }
                    )
                }
            }
        }

        else -> {
            LazyColumn(
                contentPadding = PaddingValues(Padding.ScaffoldInner),
                verticalArrangement = Arrangement.spacedBy(Padding.Divider)
            ) {

                item {
                    Text(date, style = Font.title)
                }

                classList(
                    windowSize = windowSize,
                    scheduleState = scheduleState,
                    onClickActivity = { index, type ->
                        component.onClickActivity(
                            index,
                            type,
                            component.compass.defaultSchedule
                        )
                    },
                    onClickEvent = { index, type ->
                        component.onClickEvent(
                            index,
                            type,
                            component.compass.defaultSchedule
                        )
                    },
                    experimentalClassList = component.experimentalClassList,
                    _schoolStartTime = component.schoolStartTime,
                    date = Clock
                        .System
                        .now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                )

                item {
                    ShortDivider()
                }

                dueLearningTasks(
                    scheduleState = scheduleState,
                    onClickTask = component.onClickLearningTask
                )

                item {
                    ShortDivider()
                }

                newsfeed(
                    newsfeedState = newsfeedState,
                    selectedNewsItem = selectedNewsItem,
                    buildDomainUrlString = component.compass::buildDomainUrlString,
                    onClickItem = { index ->
                        selectedNewsItem = if (index == selectedNewsItem) null else index
                    }
                )
            }
        }
    }
}