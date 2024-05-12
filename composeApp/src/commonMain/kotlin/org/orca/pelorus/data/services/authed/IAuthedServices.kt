package org.orca.pelorus.data.services.authed

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.orca.pelorus.data.utils.toLocalDate
import org.orca.pelorus.screens.tabs.calendar.CalendarScreenModel
import org.orca.pelorus.screens.tabs.home.HomeScreenModel

/**
 * The main app authenticated-scope services provider.
 *
 * This mostly provides screen models as the UI-facing side of operations, but those
 * are given the dependencies they need implicitly by shared repositories without the UI needing
 * to be involved in this.
 */
interface IAuthedServices {

    context(Screen)
    @Composable
    fun homeScreenModel(): HomeScreenModel

    context(Screen)
    @Composable
    fun calendarScreenModel(date: LocalDate): CalendarScreenModel

    context(Screen)
    @Composable
    fun calendarScreenModel() = calendarScreenModel(Clock.System.now().toLocalDate())

}
