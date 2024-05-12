package org.orca.pelorus.data.services.authed

import androidx.compose.runtime.Composable
import org.orca.pelorus.screens.tabs.calendar.CalendarScreenModel
import org.orca.pelorus.screens.tabs.calendar.CalendarTab
import org.orca.pelorus.screens.tabs.home.HomeScreenModel
import org.orca.pelorus.screens.tabs.home.HomeTab
import kotlin.reflect.KClass

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

    @Composable
    fun calendarScreenModel(): CalendarScreenModel

}
