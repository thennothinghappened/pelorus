package org.orca.pelorus.screens.tabs.home

import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.objects.CalendarEventData
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.userdetails.IUserDetailsRepository
import org.orca.pelorus.data.usecases.GetCalendarEventsWithStaffAndActivityUseCase
import org.orca.pelorus.data.utils.combineSuccessOrPass
import org.orca.pelorus.data.utils.foldResponse
import org.orca.pelorus.data.utils.toLocalDateTime

class HomeScreenModel(
    userDetailsRepository: IUserDetailsRepository,
    getCalendarEventsWithStaff: GetCalendarEventsWithStaffAndActivityUseCase
) : ScreenModel {

    private val date = Clock.System.now().toLocalDateTime().date

    val state: StateFlow<State> = getCalendarEventsWithStaff(date)
        .combineSuccessOrPass(userDetailsRepository.userDetails) { calendarEvents, userDetails ->
            Response.Success(State.Success(
                calendarDate = date,
                user = userDetails,
                events = calendarEvents
            ))
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
        data class Success(val calendarDate: LocalDate, val user: UserDetails, val events: List<CalendarEventData>) : State
    }

}
