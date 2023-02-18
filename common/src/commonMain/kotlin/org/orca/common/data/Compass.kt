package org.orca.common.data

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.kotlass.FlowKotlassClient
import org.orca.kotlass.IFlowKotlassClient.*
import org.orca.kotlass.KotlassClient

class Compass(
    credentials: KotlassClient.CompassClientCredentials,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : FlowKotlassClient(credentials, scope), InstanceKeeper.Instance {
    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    private val _viewedEntry: MutableStateFlow<ScheduleEntry.ActivityEntry?> = MutableStateFlow(null)
    val viewedEntry: StateFlow<ScheduleEntry.ActivityEntry?> = _viewedEntry

    // calendar
    val calendarSchedule = Pollable.Schedule(refreshIntervals.schedule, startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

    init {
        beginPolling(defaultNewsfeed)
        beginPolling(defaultSchedule)
        beginPolling(defaultLearningTasks)
        manualPoll(calendarSchedule)
        manualPoll(defaultTaskCategories)
    }

    fun setViewedEntry(scheduleEntryIndex: Int, schedule: Pollable.Schedule = defaultSchedule) {
        if (schedule.state.value !is State.Success) return

        // make sure to filter to only grab things which hold activities!
        _viewedEntry.value = (schedule.state.value as State.Success<List<ScheduleEntry>>).data
            .filterIsInstance<ScheduleEntry.ActivityEntry>()[scheduleEntryIndex]
    }

}