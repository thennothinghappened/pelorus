package org.orca.pelorus.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import org.orca.kotlass.data.calendar.CalendarEvent
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
        .combine(todayCalendarEventsFlow) { userDetails, calendarEventsResponse ->
            return@combine when (calendarEventsResponse) {
                is Response.Loading -> State.Loading
                is Response.Success -> State.Success(userDetails, calendarEventsResponse.data)
                is Response.Failure -> State.Failure(calendarEventsResponse.error)
            }
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
