package org.orca.common.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.orca.common.BuildConfig
import org.orca.kotlass.data.NetResponse

private const val UPDATE_URL = "https://api.github.com/repos/thennothinghappened/pelorus/releases/latest"

@Serializable
data class GitHubLatestVersionResponse(
    val name: String,
    val tag_name: String,
    val prerelease: Boolean,
    val html_url: String? = null
)

/**
 * Check if we're running the most up-to-date version through GitHub
 *
 * @return A pair of:
 *
 * a) Are we up to date?
 *
 * b) If not, what is the latest?
 */
suspend fun updateCheck(allowPrereleases: Boolean = false): NetResponse<Pair<Boolean, GitHubLatestVersionResponse?>> {
    val server = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    val req = server.get(UPDATE_URL)

    if (req.status != HttpStatusCode.OK) {
        return NetResponse.RequestFailure(req.status)
    }

    val res = req.body<GitHubLatestVersionResponse>()

    server.close()

    val ourVersion = BuildConfig.APP_VERSION.lowercase()
    val newVersion = res.tag_name.lowercase()

    if (ourVersion == newVersion) {
        return NetResponse.Success(Pair(true, null))
    }

    // parse the version (badly...)
    val wereInBeta = ourVersion.contains("beta")
    val currentInBeta = newVersion.contains("beta")

    if (wereInBeta && !currentInBeta) {
        if (!res.prerelease || allowPrereleases) {
            return NetResponse.Success(Pair(false, res))
        }
    }

    val ourVersionNum = ourVersion.split('-')[0].split('.').map { it.toInt() }
    val newVersionNum = newVersion.split('-')[0].split('.').map { it.toInt() }

    // we should be able to get away with this since we won't be runnning a version *newer* than latest
    if (
        newVersionNum[0] > ourVersionNum[0] ||
        newVersionNum[1] > ourVersionNum[1] ||
        newVersionNum[2] > ourVersionNum[2]
        ) {
        if (!res.prerelease || allowPrereleases) {
            return NetResponse.Success(Pair(false, res))
        }
    }

    return NetResponse.Success(Pair(true, null))
}