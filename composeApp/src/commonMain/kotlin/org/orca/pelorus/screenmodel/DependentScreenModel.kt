package org.orca.pelorus.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

/**
 * A screen model which has dependencies to resolve.
 */
abstract class DependentScreenModel<T : ScreenModel> {

    protected val navigator: Navigator
        @Composable
        get() = LocalNavigator.currentOrThrow

    /**
     * Instantiate this screen model.
     */
    @Composable
    abstract fun instantiate(): T

    /**
     * Get an instance of this screen model scoped to a given screen.
     */
    context (Screen)
    @Composable
    abstract fun getForScreen(): T

    /**
     * Get a shared navigator-scope instance of this screen model.
     */
    @Composable
    abstract fun getForNavigator(): T

}
