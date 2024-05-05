package org.orca.pelorus.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orca.kotlass.client.CompassApiResult
import org.orca.kotlass.data.user.UserDetails
import org.orca.pelorus.data.di.authedServices
import org.orca.pelorus.screens.AuthenticatedScreen

object HomeScreen : AuthenticatedScreen, Tab {

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

        Column {

            val client = authedServices.client
            var response: CompassApiResult<UserDetails>? by remember { mutableStateOf(null) }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    response = client.getMyUserDetails()
                }
            }

            when (val r = response) {

                null -> {
                    CircularProgressIndicator()
                }

                is CompassApiResult.Failure -> {
                    Text("Failed to fetch user details:\n${r.error}")
                }

                is CompassApiResult.Success -> {

                    val details = r.data

                    Text("Welcome to Compass, ${details.firstName}!")

                }

            }

        }

    }

    private fun readResolve(): Any = HomeScreen

}