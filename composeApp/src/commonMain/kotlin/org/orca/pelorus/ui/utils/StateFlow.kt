package org.orca.pelorus.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Shorthand to collect the current value in the composition without writing `collectAsState().value`.
 */
@Composable
fun <T> StateFlow<T>.collectValueWithLifecycle(
    context: CoroutineContext = EmptyCoroutineContext
): T = collectAsStateWithLifecycle(context).value

/**
 * `collectAsState()` but with respect to the Android lifecycle if we are
 * on Android.
 */
@Composable
expect fun <T> StateFlow<T>.collectAsStateWithLifecycle(context: CoroutineContext): State<T>
