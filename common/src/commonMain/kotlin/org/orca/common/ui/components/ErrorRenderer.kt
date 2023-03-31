package org.orca.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorRenderer(
    error: Throwable
) {
    Column {
        Text("An error has occurred!", style = MaterialTheme.typography.headlineSmall)

        // Categorize by error type to give more useful information to the user
        when (error) {
            is io.ktor.client.plugins.HttpRequestTimeoutException -> {
                Text(
                    "Failed to connect to Compass servers: Request timeout",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Check your internet connection. If the error persists, check if you can reach the Compass website.")
            }

            is io.ktor.serialization.JsonConvertException -> {
                Text(
                    "Kotlass encountered unexpected input when reading a response from Compass.",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Check that your client is up-to-date, and if so, report this error on GitHub with the below information.")
                Card {
                    Text(error.cause.toString(), Modifier.padding(8.dp))
                }
            }

            else -> {
                Text(
                    "Something has gone wrong and Pelorus was not able to handle it.",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Please report this error on GitHub with the below information.")
                Card {
                    Text(error.toString(), Modifier.padding(8.dp))
                }
            }
        }
    }
}

