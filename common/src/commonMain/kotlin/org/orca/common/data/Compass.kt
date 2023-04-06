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
import org.orca.kotlass.dummy.DummyKotlassClient

/**
 * Extended Kotlass client for Pelorus-specific features.
 * @param credentials Client credentials
 * @param scope Coroutine scope to execute async features within.
 * @param dummy Should the client use the Kotlass Dummy client? (for testing)
 */
class Compass(
    credentials: KotlassClient.CompassClientCredentials,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    dummy: Boolean = false,
    devMode: Boolean = false
) : FlowKotlassClient(
    credentials = credentials,
    scope = scope,
    kotlassClient = if (dummy) DummyKotlassClient("example.com") else KotlassClient(credentials, devMode = devMode)
), InstanceKeeper.Instance {
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

    fun setViewedEntry(scheduleEntry: ScheduleEntry.ActivityEntry) {
        _viewedEntry.value = scheduleEntry
    }

}