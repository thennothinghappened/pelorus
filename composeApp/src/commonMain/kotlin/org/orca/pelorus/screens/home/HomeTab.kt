package org.orca.pelorus.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.orca.pelorus.data.di.authedServices
import org.orca.pelorus.screens.AuthenticatedScreen
import org.orca.pelorus.ui.utils.collectValue

object HomeTab : AuthenticatedScreen, Tab {

    override val options: TabOptions
        @Composable
        get() {
            // TODO: string resources!
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {

        val screenModel = authedServices.homeScreenModel()
        val state = screenModel.state.collectValue()

        Column {

            when (state) {

                is HomeScreenModel.State.Loading -> {
                    CircularProgressIndicator()
                }

                is HomeScreenModel.State.Success -> {
                    Text(state.currentUser.toString())
                }

            }

        }

    }

    private fun readResolve(): Any = HomeTab

}
