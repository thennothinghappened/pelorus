package org.orca.pelorus.data.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import org.orca.pelorus.data.services.root.IRootServices

/**
 * Composition provider for the application root services.
 */
val LocalRootServices = staticCompositionLocalOf<IRootServices> {
    error("Missing the root services for the application, how did we get here?")
}

/**
 * Shorthand access for the root services instance.
 */
val rootServices: IRootServices
    @Composable
    get() = LocalRootServices.current

/**
 * Provider for top-level services that need to be accessed throughout the application
 * such as preferences.
 *
 * Arguments passed to this function are platform-dependent dependencies.
 */
@Composable
fun WithRootServices(
    rootServices: IRootServices,
    content: @Composable () -> Unit
) = CompositionLocalProvider(
    LocalRootServices provides rootServices,
    content = content
)
