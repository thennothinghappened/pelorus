package org.orca.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.halilibo.richtext.ui.Heading
import com.halilibo.richtext.ui.material3.Material3RichText
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jsoup.Jsoup
import org.orca.common.data.Compass
import org.orca.common.data.formatAsVisualDate
import org.orca.common.data.timeAgo
import org.orca.common.ui.components.*
import org.orca.common.ui.components.calendar.ClassList
import org.orca.common.ui.components.calendar.DueLearningTasks
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.data.NewsItem

class HomeComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickActivity: (Int) -> Unit
) : ComponentContext by componentContext


@Composable
fun HomeContent(
    component: HomeComponent,
    modifier: Modifier = Modifier,
    windowSize: WindowSize
) {
    val scheduleState by component.compass.defaultSchedule.state.collectAsState()
    val newsfeedState by component.compass.defaultNewsfeed.state.collectAsState()
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
                                scheduleState = scheduleState,
                                onClickActivity = component.onClickActivity
                            )
                            ShortDivider()
                            DueLearningTasks(scheduleState = scheduleState)
                        }
                        Newsfeed(modifier = Modifier.weight(1f), newsfeedState = newsfeedState)
                    }
                }
            }
            else -> {
                item {
                    ClassList(
                        windowSize = windowSize,
                        scheduleState = scheduleState,
                        onClickActivity = component.onClickActivity
                    )
                }
                item { ShortDivider() }
                item {
                    DueLearningTasks(scheduleState = scheduleState)
                }
                item { ShortDivider() }
                item {
                    Newsfeed(newsfeedState = newsfeedState)
                }
            }
        }
    }
}

@Composable
fun Newsfeed(
    modifier: Modifier = Modifier,
    newsfeedState: CompassApiClient.State<List<NewsItem>>
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Newsfeed", style = MaterialTheme.typography.labelMedium)
        NetStates(
            newsfeedState,
            { CircularProgressIndicator() },
            { ErrorRenderer((newsfeedState as CompassApiClient.State.Error).error) }
        ) { list ->
            list.forEach {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Material3RichText(modifier = Modifier.padding(8.dp)) {
                        Heading(4, it.title)
                        Heading(9, "${it.userName} - ${it.postDateTime?.timeAgo()}")
                        HtmlText(Jsoup.parse(it.content1.toString()).body())
                    }
                }
            }
        }
    }
}