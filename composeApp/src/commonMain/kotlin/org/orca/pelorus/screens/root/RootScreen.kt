package org.orca.pelorus.screens.root

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.screens.home.HomeScreen
import org.orca.pelorus.screens.settings.SettingsScreen
import org.orca.pelorus.ui.common.TabNavigationItem

/**
 * The Root screen of the authenticated application, handling the main navigation.
 */
object RootScreen : AuthenticatedScreen {

    @Composable
    override fun Content() {

        TabNavigator(HomeScreen) {

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TabNavigationItem(SettingsScreen)
                        TabNavigationItem(HomeScreen)
                    }
                }
            ) {
                CurrentTab()
            }

        }

    }

    private fun readResolve(): Any = RootScreen

}