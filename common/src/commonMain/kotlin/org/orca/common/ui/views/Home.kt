package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.pullRefresh
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.datetime.*
import org.orca.common.data.Compass
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.calendar.ScheduleHolderType
import org.orca.common.ui.components.common.ShortDivider
import org.orca.common.ui.components.newsfeed.newsfeed
import org.orca.common.ui.defaults.Padding
import org.orca.common.ui.utils.WindowSize
import org.orca.common.ui.views.schedule.daySchedule
import org.orca.kotlass.IFlowKotlassClient

class HomeComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickActivity: (Int, ScheduleHolderType) -> Unit,
    val onClickEvent: (Int, ScheduleHolderType) -> Unit,
    val onClickLearningTask: (String) -> Unit,
    val experimentalClassList: Boolean,
    val schoolStartTime: LocalTime
) : ComponentContext by componentContext {

    fun refreshSchedule() {
        compass.manualPoll(compass.defaultSchedule)
    }

    fun refreshNewsfeed() {
        compass.manualPoll(compass.defaultNewsfeed)
    }

}

@Composable
fun HomeContent(
    component: HomeComponent,
    modifier: Modifier = Modifier,
    windowSize: WindowSize
) {
    val newsfeedState by component.compass.defaultNewsfeed.state.collectAsStateAndLifecycle()
    val scheduleState by component.compass.defaultSchedule.state.collectAsStateAndLifecycle()

    val newsfeedRefreshing = newsfeedState is IFlowKotlassClient.State.Loading
    val scheduleRefreshing = scheduleState is IFlowKotlassClient.State.Loading

    val date = remember {
            Clock
                .System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
    }

    var selectedNewsItem: Int? by remember { mutableStateOf(null) }

    when (windowSize) {
        WindowSize.EXPANDED -> {
            Row(Modifier.fillMaxSize()) {

                /** TODO:
                 * waiting on https://github.com/JetBrains/compose-multiplatform/issues/653
                 * for pull to refresh to work on desktop. For now, we can live with it.
                 **/

                val schedulePullRefreshState = rememberPullRefreshState(
                    refreshing = scheduleRefreshing,
                    onRefresh = component::refreshSchedule
                )

                val newsfeedPullRefreshState = rememberPullRefreshState(
                    refreshing = newsfeedRefreshing,
                    onRefresh = component::refreshNewsfeed
                )

                Box(Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(schedulePullRefreshState),
                        contentPadding = PaddingValues(Padding.ScaffoldInner)
                    ) {
                        daySchedule(
                            windowSize = windowSize,
                            scheduleState = scheduleState,
                            date = date,
                            experimentalClassList = component.experimentalClassList,
                            schoolStartTime = component.schoolStartTime,
                            onClickActivity = component.onClickActivity,
                            onClickEvent = component.onClickEvent,
                            onClickLearningTask = component.onClickLearningTask
                        )
                    }

                    PullRefreshIndicator(
                        refreshing = scheduleRefreshing,
                        state = schedulePullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }

                Box(Modifier.weight(1f)) {

                    LazyColumn(
                        modifier = Modifier.pullRefresh(newsfeedPullRefreshState),
                        contentPadding = PaddingValues(Padding.ScaffoldInner)
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

                    PullRefreshIndicator(
                        refreshing = newsfeedRefreshing,
                        state = newsfeedPullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }

        WindowSize.MEDIUM, WindowSize.COMPACT -> {
            val pullRefreshState = rememberPullRefreshState(
                refreshing =
                    (scheduleState is IFlowKotlassClient.State.Loading) ||
                    (newsfeedState is IFlowKotlassClient.State.Loading),
                onRefresh = {
                    component.refreshSchedule()
                    component.refreshNewsfeed()
                }
            )

            Box(Modifier.fillMaxSize()) {
                LazyColumn(
                    contentPadding = PaddingValues(Padding.ScaffoldInner),
                    modifier = Modifier.pullRefresh(pullRefreshState)
                ) {

                    daySchedule(
                        windowSize = windowSize,
                        scheduleState = scheduleState,
                        date = date,
                        experimentalClassList = component.experimentalClassList,
                        schoolStartTime = component.schoolStartTime,
                        onClickActivity = component.onClickActivity,
                        onClickEvent = component.onClickEvent,
                        onClickLearningTask = component.onClickLearningTask
                    )

                    item {
                        Spacer(Modifier.height(Padding.Divider))
                        ShortDivider()
                        Spacer(Modifier.height(Padding.Divider))
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

                PullRefreshIndicator(
                    refreshing = scheduleRefreshing || newsfeedRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}