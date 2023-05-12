package org.orca.common.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlinx.datetime.*
import org.orca.common.data.Compass
import org.orca.common.data.formatAsVisualDate
import org.orca.common.data.timeAgo
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.*
import org.orca.common.ui.components.calendar.ClassList
import org.orca.common.ui.components.calendar.DueLearningTasks
import org.orca.common.ui.components.calendar.ScheduleHolderType
import org.orca.common.ui.components.newsfeed.NewsfeedItem
import org.orca.common.ui.components.newsfeed.Newsfeed
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient
import org.orca.kotlass.data.NewsItem
import org.orca.htmltext.HtmlText

class HomeComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickActivity: (Int, ScheduleHolderType, IFlowKotlassClient.Pollable.Schedule) -> Unit,
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
    val date = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.formatAsVisualDate() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        when (windowSize) {
            WindowSize.EXPANDED -> {
                item {
                    Text(date)
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            ClassList(
                                windowSize = windowSize,
                                schedule = component.compass.defaultSchedule,
                                onClickActivity = component.onClickActivity,
                                experimentalClassList = component.experimentalClassList,
                                _schoolStartTime = component.schoolStartTime,
                                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                            )
                            ShortDivider()
                            DueLearningTasks(
                                schedule = component.compass.defaultSchedule,
                                onClickTask = component.onClickLearningTask
                            )
                        }
                        Newsfeed(modifier = Modifier.weight(1f), newsfeedState = newsfeedState, compass = component.compass)
                    }
                }
            }
            else -> {
                item {
                    ClassList(
                        windowSize = windowSize,
                        schedule = component.compass.defaultSchedule,
                        onClickActivity = component.onClickActivity,
                        experimentalClassList = component.experimentalClassList,
                        _schoolStartTime = component.schoolStartTime,
                        date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    )
                }
                item { ShortDivider() }
                item {
                    DueLearningTasks(
                        schedule = component.compass.defaultSchedule,
                        onClickTask = component.onClickLearningTask
                    )
                }
                item { ShortDivider() }
                item {
                    Newsfeed(newsfeedState = newsfeedState, compass = component.compass)
                }
            }
        }
    }
}

@Composable
private fun Newsfeed(
    modifier: Modifier = Modifier,
    newsfeedState: IFlowKotlassClient.State<List<NewsItem>>,
    compass: Compass
) {
    Newsfeed(modifier) {
        NetStates(newsfeedState) { list ->
            list.forEach {
                NewsfeedItem(
                    it.title,
                    it.userName,
                    lazyPainterResource(compass.buildDomainUrlString(it.userImageUrl)),
                    it.postDateTime,
                    it.content1.toString(),
                    it.attachments.map { Pair(it.name, compass.buildDomainUrlString(it.uiLink)) }
                )
            }
        }
    }
}