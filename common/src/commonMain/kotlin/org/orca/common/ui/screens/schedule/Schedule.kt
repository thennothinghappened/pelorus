package org.orca.common.ui.screens.schedule

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import kotlinx.datetime.*
import org.orca.common.data.formatAsVisualDate
import org.orca.common.ui.components.calendar.ScheduleHolderType
import org.orca.common.ui.components.calendar.classList
import org.orca.common.ui.components.calendar.dueLearningTasks
import org.orca.common.ui.components.common.ShortDivider
import org.orca.common.ui.defaults.Font
import org.orca.common.ui.defaults.Padding
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.IFlowKotlassClient

fun LazyListScope.daySchedule(
    windowSize: WindowSize,
    scheduleState: IFlowKotlassClient.State<IFlowKotlassClient.Pollable.Schedule.ScheduleStateHolder>,
    date: LocalDate?,
    experimentalClassList: Boolean,
    schoolStartTime: LocalTime,
    onClickActivity: (Int, ScheduleHolderType) -> Unit,
    onClickEvent: (Int, ScheduleHolderType) -> Unit,
    onClickLearningTask: (String) -> Unit
) {
    item {
        Text(date?.formatAsVisualDate() ?: "", style = Font.title)
    }

    classList(
        windowSize = windowSize,
        scheduleState = scheduleState,
        onClickActivity = onClickActivity,
        onClickEvent = onClickEvent,
        experimentalClassList = experimentalClassList,
        _schoolStartTime = schoolStartTime,
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
        onClickTask = onClickLearningTask
    )
}