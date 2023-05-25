package org.orca.common.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.http.*
import org.orca.kotlass.data.NetResponse

@Composable
fun ErrorRenderer(
    mainText: String,
    secondaryText: String,
    error: Throwable? = null
) {
    Column {
        Text("An error has occurred!", style = MaterialTheme.typography.headlineSmall)
        Text(
            mainText,
            style = MaterialTheme.typography.titleMedium
        )
        Text(secondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
        if (error != null) {
            Card {
                Text(error.toString(), Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun ErrorRenderer(
    error: Throwable
) {
    // Categorize by error type to give more useful information to the user
    when (error) {
        is io.ktor.client.plugins.HttpRequestTimeoutException -> {
            ErrorRenderer(
                "Failed to connect to Compass servers: Request timeout",
                "Check your internet connection. If the error persists, check if you can reach the Compass website."
            )
        }

        is java.net.UnknownHostException -> {
            ErrorRenderer(
                "Failed to connect to Compass servers: Failed to resolve host",
                "Check your internet connection. If the error persists, check if you can reach the Compass website."
            )
        }

        is io.ktor.serialization.JsonConvertException -> {
            ErrorRenderer(
                "Kotlass encountered unexpected input when reading a response from Compass.",
                "Check that your client is up-to-date, and if so, report this error on GitHub with the below information.",
                error.cause
            )
        }

        else -> {
            ErrorRenderer(
                "Something has gone wrong and Pelorus was not able to handle it.",
                "Please report this error on GitHub with the below information.",
                error
            )
        }
    }
}

@Composable
fun ErrorRenderer(
    response: NetResponse.Error<*>
) {
    if (response is NetResponse.RequestFailure) {
        if (response.httpStatusCode == HttpStatusCode.InternalServerError) {
            ErrorRenderer(
                "Failed to connect to Compass servers: HTTP 500",
                "Unfortunately further details of this error can't be determined. Most likely your credentials have been invalidated by Compass, so try relaunching the app.\nCompass may also be under maintenance, so please check if the website is operational.\nIf the error persists, please report the error to the Kotlass GitHub page."
            )
        }
    } else {
        ErrorRenderer(response.error)
    }
}
