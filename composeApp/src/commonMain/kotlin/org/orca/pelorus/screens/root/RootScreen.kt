package org.orca.pelorus.screens.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.screens.tabs.calendar.CalendarTab
import org.orca.pelorus.screens.tabs.home.HomeTab
import org.orca.pelorus.screens.tabs.profile.ProfileTab
import org.orca.pelorus.screens.tabs.settings.SettingsTab
import org.orca.pelorus.ui.common.TabNavigationItem

/**
 * The Root screen of the authenticated application, handling the main navigation.
 */
object RootScreen : AuthenticatedScreen {

    @Composable
    override fun Content() {

        TabNavigator(HomeTab) {

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(SettingsTab)
                        TabNavigationItem(CalendarTab)
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(ProfileTab)
                    }
                }
            ) { paddingValues ->
                Box(Modifier.padding(paddingValues)) {
                    CurrentTab()
                }
            }

        }

    }

    private fun readResolve(): Any = RootScreen

}