package org.orca.common.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.orca.kotlass.CompassApiClient

@Composable
fun NetStates(
    state: CompassApiClient.State<*>?,
    loading: @Composable () -> Unit,
    error: @Composable () -> Unit,
    invalid: @Composable () -> Unit = { Text("Invalid State!") },
    result: @Composable () -> Unit
) {
    when (state) {
        is CompassApiClient.State.NotInitiated<*> -> { Text("uhm.") }
        is CompassApiClient.State.Loading<*> -> loading()
        is CompassApiClient.State.Error<*> -> error()
        is CompassApiClient.State.Success<*> -> result()
        else -> invalid()
    }
}