package org.orca.pelorus.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.lyricist.strings
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.orca.pelorus.ui.theme.sizing


object LoginScreen : Screen {
    @Composable
    override fun Content() {
        Column {
            Column(Modifier.width(IntrinsicSize.Min)) {
                Text(
                    strings.loginWelcome,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    strings.loginTagline,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(sizing.spacerLarge))


        }
    }

    @Composable
    private fun LoginOption() {
        val navigator = LocalNavigator.currentOrThrow

        Card(
            modifier = Modifier.clickable {
//                navigator.push()
            }
        ) {

        }
    }

}