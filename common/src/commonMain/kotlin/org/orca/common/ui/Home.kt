package org.orca.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.common.ui.components.ClassCard
import org.orca.common.ui.components.CornersCard
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.data.CalendarEvent

class Home(
    componentContext: ComponentContext,
    val compass: Compass
) : ComponentContext by componentContext {

    init {
        println("aww shiiiid here we go agaaaain\n\n")
    }
}

@Composable
fun HomeContent(
    component: Home,
    modifier: Modifier = Modifier,
    windowSize: WindowSize
) {
    val scheduleState by component.compass.schedule.collectAsState()

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
                    Newsfeed(modifier = Modifier.weight(1f))
                }
            }
            else -> {
                ClassList(windowSize = windowSize, scheduleState = scheduleState)
                Divider()
                TodoTaskList()
                Divider()
                Newsfeed()
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
                val classes = (scheduleState as Compass.NetType.Result<Array<CalendarEvent>>).data
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
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text("Newsfeed", style = MaterialTheme.typography.labelMedium)
        CornersCard(
            "egg",
            "",
            "",
            ""
        )
    }
}