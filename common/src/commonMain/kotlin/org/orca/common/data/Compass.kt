package org.orca.common.data

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        beginPollingNewsfeed()
        beginPollingSchedule()
    }

    private val _viewedEntry: MutableStateFlow<ScheduleEntry.ActivityEntry?> = MutableStateFlow(null)
    val viewedEntry: StateFlow<ScheduleEntry.ActivityEntry?> = _viewedEntry

    fun setViewedEntry(scheduleEntryIndex: Int) {
        if (schedule.value !is State.Success) return

        _viewedEntry.value =
            @Suppress("UNCHECKED_CAST") // we always know the entry index supplied is an ActivityEntry.
            (schedule.value as State.Success<List<ScheduleEntry.ActivityEntry>>).data[scheduleEntryIndex]
    }

}