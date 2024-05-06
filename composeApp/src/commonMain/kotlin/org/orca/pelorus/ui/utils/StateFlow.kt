package org.orca.pelorus.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Shorthand to collect the current value in the composition without writing `collectAsState().value`.
 */
@Composable
fun <T> StateFlow<T>.collectValue(
    context: CoroutineContext = EmptyCoroutineContext
): T = collectAsState(value, context).value
