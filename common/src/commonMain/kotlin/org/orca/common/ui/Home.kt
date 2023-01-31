package org.orca.common.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jsoup.Jsoup
import org.orca.common.ui.components.BaseCard
import org.orca.common.ui.components.ClassCard
import org.orca.common.ui.components.CornersCard
import org.orca.common.ui.components.HtmlText
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.data.CalendarEvent
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
    LazyColumn {
        when (windowSize) {
            WindowSize.EXPANDED -> {
                item {
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            ClassList(
                                windowSize = windowSize,
                                scheduleState = scheduleState,
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
                        scheduleState = scheduleState,
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
    scheduleState: Compass.NetType<Array<CalendarEvent>>,
    onClickActivity: (String) -> Unit
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        when (scheduleState) {
            is Compass.NetType.Loading -> {
                CircularProgressIndicator()
            }
            is Compass.NetType.Error -> {
                Text("FAILURE")
            }
            is Compass.NetType.Result -> {
                val classes = scheduleState.data
                Text("Schedule", style = MaterialTheme.typography.labelMedium)
                when (windowSize) {
                    WindowSize.EXPANDED -> {
                        classes.forEach {
                            ClassCard(
                                it.longTitleWithoutTime,
                                "wtf compass",
                                it.managerId.toString(),
                                it.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time
                            ) {
                                if (it.instanceId != null) onClickActivity(it.instanceId!!)
                            }
                        }
                    }
                    else -> {
                        classes.forEach {
                            ClassCard(
                                it.longTitleWithoutTime,
                                "wtf compass",
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
    newsfeedState: Compass.NetType<List<NewsItem>>
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text("Newsfeed", style = MaterialTheme.typography.labelMedium)
        when (newsfeedState) {
            is Compass.NetType.Loading -> { CircularProgressIndicator() }
            is Compass.NetType.Error -> { Text(newsfeedState.error.toString()) }
            is Compass.NetType.Result -> {
                newsfeedState.data.forEach {
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
}