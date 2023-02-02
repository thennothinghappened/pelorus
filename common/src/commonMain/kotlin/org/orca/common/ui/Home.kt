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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.halilibo.richtext.ui.material3.Material3RichText
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jsoup.Jsoup
import org.orca.common.ui.components.*
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.data.NewsItem

class HomeComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickActivity: (String) -> Unit
) : ComponentContext by componentContext {


}


@Composable
fun HomeContent(
    component: HomeComponent,
    modifier: Modifier = Modifier,
    windowSize: WindowSize
) {
    val scheduleState by component.compass.schedule.collectAsState()
    val newsfeedState by component.compass.newsfeed.collectAsState()
//    val activitiesState by component.compass.activities.collectAsState()

    LazyColumn {
        when (windowSize) {
            WindowSize.EXPANDED -> {
                item {
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            ClassList(
                                windowSize = windowSize,
                                _scheduleState = scheduleState,
                                onClickActivity = component.onClickActivity
                            )
                            Divider()
                            TodoTaskList()
                        }
                        Newsfeed(modifier = Modifier.weight(1f), newsfeedState = newsfeedState)
                    }
                }
            }
            else -> {
                item {
                    ClassList(
                        windowSize = windowSize,
                        _scheduleState = scheduleState,
                        onClickActivity = component.onClickActivity
                    )
                }
                item {
                    Divider()
                }
                item {
                    TodoTaskList()
                }
                item {
                    Divider()
                }
                item {
                    Newsfeed(newsfeedState = newsfeedState)
                }
            }
        }
    }
}

@Composable
fun ClassList(
    modifier: Modifier = Modifier,
    windowSize: WindowSize,
    _scheduleState: CompassApiClient.State<Array<CompassApiClient.ScheduleEntry>>,
    onClickActivity: (String) -> Unit
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        NetStates(
            _scheduleState,
            {
                CircularProgressIndicator()
            },
            {
                ErrorRenderer((_scheduleState as CompassApiClient.State.Error).error)
            }
        ) {
            val scheduleState = _scheduleState as CompassApiClient.State.Success
            val classes = scheduleState.data
            Text("Schedule", style = MaterialTheme.typography.labelMedium)
            when (windowSize) {
                WindowSize.EXPANDED -> {
                    classes.forEach {
                        val it = it.event
                        ClassCard(
                            it.longTitleWithoutTime,
                            "",
                            it.managerId.toString(),
                            it.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time
                        ) {
                            if (it.instanceId != null) onClickActivity(it.instanceId!!)
                        }
                    }
                }
                else -> {
                    classes.forEach {
                        val it = it.event
                        ClassCard(
                            it.longTitleWithoutTime,
                            "",
                            it.managerId.toString(),
                            it.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time
                        ) {
                            if (it.instanceId != null) onClickActivity(it.instanceId!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodoTaskList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Text("Uncompleted Tasks", style = MaterialTheme.typography.labelMedium)
        CornersCard(
            "Book Essay",
            "Submissions: 0/1",
            "English",
            "Due 4:00"
        )
    }
}

@Composable
fun Newsfeed(
    modifier: Modifier = Modifier,
    newsfeedState: CompassApiClient.State<List<NewsItem>>
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text("Newsfeed", style = MaterialTheme.typography.labelMedium)
        NetStates(
            newsfeedState,
            { CircularProgressIndicator() },
            { ErrorRenderer((newsfeedState as CompassApiClient.State.Error).error) }
        ) {
            (newsfeedState as CompassApiClient.State.Success).data.forEach {
                BaseCard(modifier = Modifier.fillMaxWidth()) {
                    Material3RichText(modifier = Modifier.padding(8.dp)) {
                        HtmlText(Jsoup.parse(it.content1.toString()).body())
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}