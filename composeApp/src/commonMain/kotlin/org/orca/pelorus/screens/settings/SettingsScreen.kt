package org.orca.pelorus.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.orca.pelorus.data.di.LocalAuthenticator
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.ui.theme.sizing

/**
 * The application settings screen.
 */
object SettingsScreen : AuthenticatedScreen, Tab {

    override val options: TabOptions
        @Composable
        get() {
            // TODO: string resources!
            val title = "Settings"
            val icon = rememberVectorPainter(Icons.Default.Settings)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {

        val authModel = LocalAuthenticator.current
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(sizing.spacerMedium)
        ) {

            ListItem(
                headlineContent = {
                    Text("Log out")
                },
                modifier = Modifier
                    .clickable(onClick = authModel::logout)
            )

        }

    }

    private fun readResolve(): Any = SettingsScreen

}