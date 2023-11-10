package org.orca.common.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.*
import org.orca.common.data.Compass
import org.orca.common.data.utils.collectAsStateAndLifecycle
import org.orca.common.ui.components.calendar.*
import org.orca.common.ui.utils.WindowSize
import org.orca.common.ui.screens.schedule.daySchedule

class CalendarComponent(
    componentContext: ComponentContext,
    val compass: Compass,
    val onClickActivity: (Int, ScheduleHolderType) -> Unit,
    val onClickEvent: (Int, ScheduleHolderType) -> Unit,
    val onClickLearningTask: (String) -> Unit,
    val experimentalClassList: Boolean,
    val schoolStartTime: LocalTime
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

@Composable
fun CalendarContent(
    component: CalendarComponent,
    windowSize: WindowSize
) { 
    val viewedDay by component.compass.calendarSchedule.startDate.collectAsStateAndLifecycle()
    var datePickerVisible by remember { mutableStateOf(false) }

    val scheduleState by component.compass.calendarSchedule.state.collectAsStateAndLifecycle()

    DatePickerDialog(
        visible = datePickerVisible,
        onClose = { datePickerVisible = false },
        startDate = viewedDay!!
    ) {
        component.setDay(it)
        datePickerVisible = false
    }

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
                    { datePickerVisible = true },
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
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                daySchedule(
                    windowSize = windowSize,
                    scheduleState = scheduleState,
                    date = viewedDay,
                    experimentalClassList = component.experimentalClassList,
                    schoolStartTime = component.schoolStartTime,
                    onClickActivity = component.onClickActivity,
                    onClickEvent = component.onClickEvent,
                    onClickLearningTask = component.onClickLearningTask
                )
            }
        }
    }
}