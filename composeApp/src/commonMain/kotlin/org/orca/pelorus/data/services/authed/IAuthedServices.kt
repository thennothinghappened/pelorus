package org.orca.pelorus.data.services.authed

import androidx.compose.runtime.Composable
import org.orca.pelorus.screens.home.HomeScreenModel

/**
 * The main app authenticated-scope services provider.
 *
 * This mostly provides screen models as the UI-facing side of operations, but those
 * are given the dependencies they need implicitly by shared repositories without the UI needing
 * to be involved in this.
 */
interface IAuthedServices {

    @Composable
    fun homeScreenModel(): HomeScreenModel

}
