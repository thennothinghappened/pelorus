package org.orca.pelorus.screens.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.orca.kotlass.client.CompassApiError
import org.orca.pelorus.cache.UserDetails
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.staff.IStaffRepository
import org.orca.pelorus.data.repository.userdetails.IUserDetailsRepository

class HomeScreenModel(
    private val userDetailsRepository: IUserDetailsRepository,
    private val staffRepository: IStaffRepository
) : ScreenModel {

    val state: StateFlow<State> = userDetailsRepository.userDetails
        .map {
            when (it) {
                is Response.Loading -> State.Loading
                is Response.Success -> State.Success(it.data)
                is Response.Failure -> State.Failure(it.error)
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
        data class Success(val currentUser: UserDetails) : State
    }

}
