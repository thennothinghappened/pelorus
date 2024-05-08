package org.orca.pelorus.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import org.orca.pelorus.cache.CalendarEvent
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.calendar.ICalendarRepository
import org.orca.pelorus.data.repository.staff.IStaffRepository
import org.orca.pelorus.data.repository.userdetails.IUserDetailsRepository
import org.orca.pelorus.utils.toLocalDateTime

class HomeScreenModel(
    private val userDetailsRepository: IUserDetailsRepository,
    private val staffRepository: IStaffRepository,
    private val calendarRepository: ICalendarRepository
) : ScreenModel {

    private val todayCalendarEventsFlow = calendarRepository
        .getEventsForDate(Clock.System.now().toLocalDateTime().date)

    val state: StateFlow<State> = userDetailsRepository.userDetails
        .map {
            println(it)
            it
        }
        .combine(todayCalendarEventsFlow) { userDetailsResponse, calendarEventsResponse ->

            if (userDetailsResponse !is Response.Result) {
                return@combine State.Loading
            }

            if (calendarEventsResponse !is Response.Result) {
                return@combine State.Loading
            }

            val userDetails = userDetailsResponse
                .getOrElse { return@combine State.Failure(it) }

            val calendarEvents = calendarEventsResponse
                .getOrElse { return@combine State.Failure(it) }

            State.Success(
                currentUser = userDetails,
                todayEvents = calendarEvents
            )
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = State.Loading
        )

    sealed interface State {
        data object Loading : State
        data class Failure(val error: RepositoryError) : State
        data class Success(val currentUser: UserDetails, val todayEvents: List<CalendarEvent>) : State
    }

}
