package org.orca.pelorus.data.services.authed

import androidx.compose.runtime.Composable
import org.orca.pelorus.screens.home.HomeScreenModel

/**
 * The main app authenticated-scope services.
 */
interface IAuthedServices {

    @Composable
    fun homeScreenModel(): HomeScreenModel

}
