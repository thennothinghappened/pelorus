package org.orca.pelorus.data.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassUserCredentials

/**
 * The instance of the Compass API client used by repositories.
 */
val LocalCompassApiClient = staticCompositionLocalOf<CompassApiClient> {
    error("No CompassApiClient!!")
}

/**
 * Authenticated scope providing a Compass API client.
 */
@Composable
fun WithCompassApiClient(
    credentials: CompassUserCredentials,
    content: @Composable () -> Unit
) {
    val client = remember { CompassApiClient(credentials) }

    CompositionLocalProvider(
        LocalCompassApiClient provides client,
        content = content
    )
}
