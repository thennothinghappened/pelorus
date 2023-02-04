package org.orca.common.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.orca.kotlass.CompassApiClient

@Composable
fun <T> NetStates(
    state: CompassApiClient.State<T>,
    loading: @Composable () -> Unit,
    error: @Composable (error: Throwable) -> Unit,
    invalid: @Composable () -> Unit = { Text("Invalid State!") },
    notInitiated: @Composable () -> Unit = { Text("Not yet initiated!") },
    result: @Composable (T) -> Unit
) {
    when (state) {
        is CompassApiClient.State.NotInitiated -> notInitiated()
        is CompassApiClient.State.Loading -> loading()
        is CompassApiClient.State.Error -> error(state.error)
        is CompassApiClient.State.Success -> @Suppress("UNCHECKED_CAST") result(state.data)
        else -> invalid()
    }
}