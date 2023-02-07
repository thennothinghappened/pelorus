package org.orca.common.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.orca.kotlass.CompassApiClient

@Composable
fun <T> NetStates(
    state: CompassApiClient.State<T>,
    loadingState: @Composable () -> Unit = { CircularProgressIndicator() },
    errorState: @Composable (error: Throwable) -> Unit = { error -> ErrorRenderer(error) },
    invalidState: @Composable () -> Unit = { Text("Invalid State!") },
    notInitiatedState: @Composable () -> Unit = { Text("Not yet initiated!") },
    result: @Composable (T) -> Unit
) {
    when (state) {
        is CompassApiClient.State.NotInitiated -> notInitiatedState()
        is CompassApiClient.State.Loading -> loadingState()
        is CompassApiClient.State.Error -> errorState(state.error)
        is CompassApiClient.State.Success -> result(state.data)
        else -> invalidState()
    }
}