package org.orca.common.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.orca.common.ui.Compass

@Composable
fun NetStates(
    state: Compass.NetType<*>?,
    loading: @Composable () -> Unit,
    error: @Composable () -> Unit,
    invalid: @Composable () -> Unit = { Text("Invalid State!") },
    result: @Composable () -> Unit
) {
    when (state) {
        is Compass.NetType.Loading<*> -> loading()
        is Compass.NetType.Error<*> -> error()
        is Compass.NetType.Result<*> -> result()
        else -> invalid()
    }
}