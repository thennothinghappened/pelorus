package org.orca.common.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.orca.kotlass.IFlowKotlassClient

@Composable
fun <T> NetStates(
    state: IFlowKotlassClient.State<T>,
    loadingState: @Composable () -> Unit = { CircularProgressIndicator() },
    errorState: @Composable (error: Throwable) -> Unit = { error -> ErrorRenderer(error) },
    invalidState: @Composable () -> Unit = { Text("Invalid State!") },
    notInitiatedState: @Composable () -> Unit = { Text("Not yet initiated!") },
    result: @Composable (T) -> Unit
) {
    when (state) {
        is IFlowKotlassClient.State.NotInitiated -> notInitiatedState()
        is IFlowKotlassClient.State.Loading -> loadingState()
        is IFlowKotlassClient.State.Error -> errorState(state.error.error)
        is IFlowKotlassClient.State.Success -> result(state.data)
        else -> invalidState()
    }
}