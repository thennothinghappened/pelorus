package org.orca.pelorus.ui.utils

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle as androidCollectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

/**
 * `collectAsState()` but with respect to the Android lifecycle if we are
 * on Android.
 */
@Composable
actual fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    context: CoroutineContext
) = androidCollectAsStateWithLifecycle(context = context)
