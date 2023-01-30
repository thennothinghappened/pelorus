package org.orca.common.ui

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDate
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.CompassClientCredentials
import org.orca.kotlass.data.CalendarEvent
import org.orca.kotlass.data.NetResponse
import org.orca.kotlass.data.NewsItem

class Compass(
    credentials: CompassClientCredentials,
    refreshIntervals: RefreshIntervals = RefreshIntervals()
) : InstanceKeeper.Instance {
    private val client = CompassApiClient(credentials)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _schedule: MutableStateFlow<NetType<Array<CalendarEvent>>> = MutableStateFlow(NetType.Loading())
    val schedule: StateFlow<NetType<Array<CalendarEvent>>> = _schedule
    val scheduleEnabled = true

    private val _newsfeed: MutableStateFlow<NetType<List<NewsItem>>> = MutableStateFlow(NetType.Loading())
    val newsfeed: StateFlow<NetType<List<NewsItem>>> = _newsfeed
    val newsfeedEnabled = true

    private suspend fun pollNewsfeedUpdate() {
        // Don't start a new request if there's already one loading.
        if (newsfeed is NetType.Loading<*>) return
        _newsfeed.value = NetType.Loading()

        val reply = client.getMyNewsFeedPaged()
        if (reply !is NetResponse.Success)
            _newsfeed.value = NetType.Error(Throwable(reply.toString()))
        else
            _newsfeed.value = NetType.Result(reply.data.data)

    }

    fun manualPollNewsfeedUpdate() {
        scope.launch {
            pollNewsfeedUpdate()
        }
    }

    private suspend fun pollScheduleUpdate() {
        // Don't start a new request if there's already one loading.
        if (schedule is NetType.Loading<*>) return

        val reply = client.getCalendarEventsByUser()
        if (reply !is NetResponse.Success)
            _schedule.value = NetType.Error(Throwable(reply.toString()))
        else
            _schedule.value = NetType.Result(reply.data)
    }

    fun manualPollScheduleUpdate() {
        scope.launch {
            pollScheduleUpdate()
        }
    }

    init {
        println("NEW INSTANCE")

        scope.launch {
            while (newsfeedEnabled) {
                pollNewsfeedUpdate()
                delay(refreshIntervals.newsfeed)
            }
        }

        scope.launch {
            while (scheduleEnabled) {
                pollScheduleUpdate()
                delay(refreshIntervals.schedule)
            }
        }
    }

    sealed interface NetType<T> {
        class Loading<T> : NetType<T>
        data class Error<T>(val error: Throwable) : NetType<T>
        data class Result<T>(val data: T) : NetType<T>
    }

    override fun onDestroy() {

    }

    data class RefreshIntervals(
        val schedule: Long = 2 * 60 * 1000,
        val newsfeed: Long = 10 * 60 * 1000,
    )

}