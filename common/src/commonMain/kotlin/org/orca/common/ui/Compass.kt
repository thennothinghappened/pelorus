package org.orca.common.ui

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orca.kotlass.CompassApiClient
import org.orca.kotlass.CompassClientCredentials
import org.orca.kotlass.data.*

class Compass(
    credentials: CompassClientCredentials,
    refreshIntervals: RefreshIntervals = RefreshIntervals()
) : InstanceKeeper.Instance {
    private val client = CompassApiClient(credentials)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _schedule: MutableStateFlow<NetType<Array<CalendarEvent>>> = MutableStateFlow(NetType.Loading())
    val schedule: StateFlow<NetType<Array<CalendarEvent>>> = _schedule
    val scheduleEnabled = true
    private val scheduleStartDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val scheduleEndDate = scheduleStartDate

    private val _newsfeed: MutableStateFlow<NetType<List<NewsItem>>> = MutableStateFlow(NetType.Loading())
    val newsfeed: StateFlow<NetType<List<NewsItem>>> = _newsfeed
    val newsfeedEnabled = true

    private val _activities: MutableStateFlow<MutableMap<String, NetType<Activity>>> = MutableStateFlow(mutableMapOf())
    val activities: StateFlow<Map<String, NetType<Activity>>> = _activities

    private val _lessonPlan: MutableStateFlow<NetType<String?>> = MutableStateFlow(NetType.Loading())
    val lessonPlan: StateFlow<NetType<String?>> = _lessonPlan

    private suspend fun pollNewsfeedUpdate() {
        // Don't start a new request if there's already one loading.
        if (newsfeed is NetType.Loading<*>) return
        _newsfeed.value = NetType.Loading()

        val reply = client.getMyNewsFeedPaged()
        if (reply !is NetResponse.Success)
            _newsfeed.value = NetType.Error((reply as NetResponse.Error<*>).error)
        else
            _newsfeed.value = NetType.Result(reply.data.data)
    }

    fun manualPollNewsfeedUpdate() {
        scope.launch {
            pollNewsfeedUpdate()
        }
    }

    private suspend fun pollScheduleUpdate(
        startDate: LocalDate = scheduleStartDate,
        endDate: LocalDate = scheduleEndDate,
        preloadActivities: Boolean = false
    ) {
        // Don't start a new request if there's already one loading.
        if (schedule is NetType.Loading<*>) return

        val reply = client.getCalendarEventsByUser(startDate, endDate)
        if (reply !is NetResponse.Success)
            _schedule.value = NetType.Error(Throwable(reply.toString()))
        else {
            reply.data.sortByDescending { it.start }
            reply.data.reverse()
            _schedule.value = NetType.Result(reply.data)
            // preloading is expensive, so only do it if required!
            _activities.value = mutableMapOf()
            if (preloadActivities) {
                // load each one sequentially, so we don't worry about spamming the API.
                (schedule.value as NetType.Result).data.forEach {
                    if (it.instanceId == null) return@forEach
                    // set all their values so we can be sure they exist first
                    _activities.value[it.instanceId!!] = NetType.Loading()
                }
                // loop over each and load one at a time
                activities.value.forEach {
                    val reply = client.getLessonsByInstanceIdQuick(it.key)
                    _activities.value[it.key] =
                        if (reply is NetResponse.Success) NetType.Result(reply.data)
                        else NetType.Error((reply as NetResponse.Error<*>).error)
                }
            }
        }
    }

    fun manualPollScheduleUpdate() {
        scope.launch {
            pollScheduleUpdate()
        }
    }

    fun getLessonPlan(instanceId: String) {
        val activity = activities.value[instanceId]!! as? NetType.Result ?: return
        scope.launch {
            // check if there is a lesson plan first
            if (activity.data.lessonPlan.fileAssetId == null) {
                _lessonPlan.value = NetType.Result(null)
                return@launch
            }
            _lessonPlan.value = NetType.Loading()
            val reply = client.downloadFile(activity.data.lessonPlan.fileAssetId!!)
            _lessonPlan.value =
                if (reply is NetResponse.Success) NetType.Result(reply.data)
                else NetType.Error((reply as NetResponse.Error<*>).error)
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
                pollScheduleUpdate(preloadActivities = true)
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