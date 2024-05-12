package org.orca.pelorus.screens.tabs.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.userdetails.IUserDetailsRepository
import org.orca.pelorus.data.usecases.GetCalendarEventsWithStaffAndActivityUseCase
import org.orca.pelorus.data.utils.combineSuccessOrPass
import org.orca.pelorus.data.utils.foldResponse

/**
 * Screen model for the Home screen.
 *
 * @param date The date used for the calendar.
 */
class HomeScreenModel(
    val date: LocalDate,
    userDetailsRepository: IUserDetailsRepository,
    getCalendarEventsWithStaff: GetCalendarEventsWithStaffAndActivityUseCase
) : ScreenModel {

    val state: StateFlow<State> = getCalendarEventsWithStaff(date)
        .combineSuccessOrPass(userDetailsRepository.userDetails) { calendarEvents, userDetails ->
            Response.Success(State.Success(userDetails, calendarEvents))
        }
        .foldResponse(
            transformLoading = { State.Loading },
            transformFailure = { State.Failure(it.error) },
            transformSuccess = { it.data }
        )
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = State.Loading
        )

    sealed interface State {
        data object Loading : State
        data class Failure(val error: RepositoryError) : State
        data class Success(val user: UserDetails, val events: List<CalendarEventData>) : State
    }

}
