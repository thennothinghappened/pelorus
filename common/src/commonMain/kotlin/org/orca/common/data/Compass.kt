package org.orca.common.data

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.CompassClientCredentials

class Compass(
    credentials: CompassClientCredentials
) : CompassApiClient(
    credentials,
    CoroutineScope(Dispatchers.IO)
    ), InstanceKeeper.Instance {
    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    private val _viewedEntry: MutableStateFlow<ScheduleEntry.ActivityEntry?> = MutableStateFlow(null)
    val viewedEntry: StateFlow<ScheduleEntry.ActivityEntry?> = _viewedEntry

    // calendar
    val calendarSchedule = Schedule(refreshIntervals.schedule)
    val viewedDay = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

    init {
        beginPollingNewsfeed()
        beginPollingSchedule()
        manualPollScheduleUpdate(schedule = calendarSchedule)
    }

    fun setViewedEntry(scheduleEntryIndex: Int, schedule: Schedule = defaultSchedule) {
        if (schedule.state.value !is State.Success) return

        _viewedEntry.value =
            @Suppress("UNCHECKED_CAST") // we always know the entry index supplied is an ActivityEntry.
            (schedule.state.value as State.Success<List<ScheduleEntry.ActivityEntry>>).data[scheduleEntryIndex]
    }

}