package org.orca.common.ui

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDate
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.CompassClientCredentials
import org.orca.kotlass.data.CData
import org.orca.kotlass.data.CalendarEvent

class Compass(
    credentials: CompassClientCredentials,
    refreshIntervals: RefreshIntervals = RefreshIntervals()
) : InstanceKeeper.Instance {
    val client = CompassApiClient(credentials)
    val scope = CoroutineScope(Dispatchers.IO)

    private val _schedule: MutableStateFlow<NetType<Array<CalendarEvent>>> = MutableStateFlow(NetType.Loading())
    val schedule: StateFlow<NetType<Array<CalendarEvent>>> = _schedule

    init {
        println("NEW INSTANCE")

        scope.launch {
            while (true) {
                println("doing thingo\n\n")
                val reply = client.getCalendarEventsByUser(
                    LocalDate(2023, 1, 30)
                )
                if (reply.error == null)
                    _schedule.value = NetType.Result(reply.data!!)
                else
                    _schedule.value = NetType.Error()
                delay(refreshIntervals.schedule)
            }
        }
    }

    sealed interface NetType<T> {
        data class Loading<T>(val loading: Boolean = true) : NetType<T>
        data class Error<T>(val error: Boolean = true) : NetType<T>
        data class Result<T>(val data: T) : NetType<T>
    }

    override fun onDestroy() {

    }

    data class RefreshIntervals(
        val schedule: Long = 2 * 60 * 1000,
        val newsfeed: Long = 10 * 60 * 1000,
    )

}