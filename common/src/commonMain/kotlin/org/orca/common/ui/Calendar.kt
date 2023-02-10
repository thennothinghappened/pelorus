package org.orca.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.orca.common.data.Compass
import org.orca.common.data.formatAsVisualDate
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.ShortDivider
import org.orca.common.ui.components.calendar.ClassList
import org.orca.common.ui.components.calendar.DueLearningTasks
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.CompassApiClient

class CalendarComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickActivity: (Int, CompassApiClient.Schedule) -> Unit,
    val onClickLearningTask: (String) -> Unit
) : ComponentContext by componentContext {

    fun poll() =
        compass.manualPoll(compass.calendarSchedule)

    private fun getDay() =
        compass.calendarSchedule.startDate.value

    fun setDay(date: LocalDate) {
        compass.calendarSchedule.setDate(date)
        poll()
    }

    fun goBackDay() {
        getDay()?.let { setDay(it.minus(1, DateTimeUnit.DAY)) }
    }

    fun goForwardDay() {
        getDay()?.let { setDay(it.plus(1, DateTimeUnit.DAY)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarContent(
    component: CalendarComponent,
    windowSize: WindowSize
) {
    val viewedDay by component.compass.calendarSchedule.startDate.collectAsStateAndLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.height(50.dp)) {
                NavigationBarItem(
                    false,
                    component::goBackDay,
                    { Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous"
                    ) }
                )
                NavigationBarItem(
                    false,
                    {},
                    { Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select"
                    ) }
                )
                NavigationBarItem(
                    false,
                    component::goForwardDay,
                    { Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next"
                    ) }
                )
            }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(viewedDay?.formatAsVisualDate() ?: "")
            }
            item {
                ClassList(
                    windowSize = windowSize,
                    schedule = component.compass.calendarSchedule,
                    onClickActivity = component.onClickActivity
                )
            }
            item { ShortDivider() }
            item {
                DueLearningTasks(
                    schedule = component.compass.calendarSchedule,
                    onClickTask = component.onClickLearningTask
                )
            }
        }
    }
}