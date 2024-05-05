package org.orca.pelorus.data.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.pelorus.data.services.authed.AuthedServices
import org.orca.pelorus.data.services.authed.IAuthedServices

/**
 * The instance of the Compass API client used by repositories.
 */
val LocalAuthedServices = staticCompositionLocalOf<IAuthedServices> {
    error("Not in authenticated context!!!")
}

/**
 * Shorthand access for the authenticated services instance.
 */
val authedServices: IAuthedServices
    @Composable
    get() = LocalAuthedServices.current

/**
 * Context with authenticated application services.
 */
@Composable
fun WithAuthedServices(
    authedServices: IAuthedServices,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAuthedServices provides authedServices,
        content = content
    )
}
