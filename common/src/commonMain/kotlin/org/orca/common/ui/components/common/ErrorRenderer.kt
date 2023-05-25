package org.orca.common.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.dp
import io.ktor.http.*
import org.orca.common.ui.defaults.Font
import org.orca.common.ui.defaults.Padding
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
    Column {
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
                    "Check that your client is up-to-date, and if so, please report this on GitHub below!",
                    error.cause
                )
                GitHubIssueButton(
                    "JSON convert exception",
                    "### Error\n```\n${error.cause}\n```\n\n### Extra information\n*Optional, or remove this*",
                    GitHubLinks.Repos.kotlass
                )
            }

            else -> {
                ErrorRenderer(
                    "Something has gone wrong and Pelorus was not able to handle it.",
                    "Please report this error on GitHub with the below information.",
                    error
                )
                GitHubIssueButton(
                    "<A brief description of what went wrong...>",
                    "*<A brief description of what you were doing when the error occurred...>*\n\n### Error\n```\n${error}\n```\n\n### Extra information\n" +
                            "*Optional, or remove this*",
                    GitHubLinks.Repos.pelorus
                )
            }
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

private object GitHubLinks {
    const val user = "thennothinghappened"
    object Repos {
        const val pelorus = "pelorus"
        const val kotlass = "kotlass"
    }
}

@Composable
fun GitHubIssueButton(
    title: String,
    body: String,
    repo: String,
    uriHandler: UriHandler = LocalUriHandler.current
) {
    val link = "https://github.com/${GitHubLinks.user}/${repo}/issues/new".encodeURLPath() +
            "?title=" + title.encodeURLParameter(true) +
            "&body=" + body.encodeURLParameter(true)

    Column(
        Modifier
            .padding(Padding.ScaffoldInner)
            .fillMaxWidth()
    ) {
        Text(
            "While not required, reporting can make sure this gets fixed ASAP!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Button(
            onClick = {
                uriHandler.openUri(link)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Report on GitHub", style = Font.button)
        }
    }
}