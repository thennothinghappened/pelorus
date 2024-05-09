package org.orca.pelorus.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.objects.CalendarEventWithStaff
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.userdetails.IUserDetailsRepository
import org.orca.pelorus.data.usecases.GetCalendarEventsWithStaffUseCase
import org.orca.pelorus.data.utils.toLocalDateTime

class HomeScreenModel(
    private val userDetailsRepository: IUserDetailsRepository,
    private val getCalendarEventsWithStaff: GetCalendarEventsWithStaffUseCase
) : ScreenModel {

    private val calendarEventsWithStaff =
        getCalendarEventsWithStaff(Clock.System.now().toLocalDateTime().date)

    val state: StateFlow<State> = userDetailsRepository.userDetails
        .combine(calendarEventsWithStaff) { userDetailsResponse, calendarEventsResponse ->

            val userDetails = userDetailsResponse
                .resultOrElse { return@combine State.Loading }
                .getOrElse { return@combine State.Failure(it) }

            val calendarEvents = calendarEventsResponse
                .resultOrElse { return@combine State.Loading }
                .getOrElse { return@combine State.Failure(it) }

            State.Success(
                user = userDetails,
                events = calendarEvents
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
        data class Success(val user: UserDetails, val events: List<CalendarEventWithStaff>) : State
    }

}
