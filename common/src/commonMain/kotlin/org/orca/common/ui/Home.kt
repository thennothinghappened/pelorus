package org.orca.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.halilibo.richtext.ui.material3.Material3RichText
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

class Home(
    componentContext: ComponentContext,
    val compass: Compass
) : ComponentContext by componentContext {

    init {
        println("made new home component!!!!!\n\n")
    }
}

@Composable
fun HomeContent(
    component: Home,
    modifier: Modifier = Modifier,
    windowSize: WindowSize
) {
    val scheduleState by component.compass.schedule.collectAsState()
    val newsfeedState by component.compass.newsfeed.collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        when (windowSize) {
            WindowSize.EXPANDED -> {
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        ClassList(windowSize = windowSize, scheduleState = scheduleState)
                        Divider()
                        TodoTaskList()
                    }
                    Newsfeed(modifier = Modifier.weight(1f), newsfeedState = newsfeedState)
                }
            }
            else -> {
                ClassList(windowSize = windowSize, scheduleState = scheduleState)
                Divider()
                TodoTaskList()
                Divider()
                Newsfeed(newsfeedState = newsfeedState)
            }
        }
    }
}

@Composable
fun ClassList(
    modifier: Modifier = Modifier,
    windowSize: WindowSize,
    scheduleState: Compass.NetType<Array<CalendarEvent>>
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
                                it.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time,
                                {}
                            )
                        }
                    }
                    else -> {
                        classes.forEach {
                            ClassCard(
                                it.longTitleWithoutTime,
                                "wtf compass",
                                it.managerId.toString(),
                                it.start?.toLocalDateTime(TimeZone.currentSystemDefault())?.time,
                                {}
                            )
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