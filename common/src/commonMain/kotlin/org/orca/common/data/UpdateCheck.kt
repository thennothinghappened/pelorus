package org.orca.common.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.orca.common.BuildDetails
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

    val ourVersion = SemVer.parseString(BuildDetails.APP_VERSION).fold(
        onFailure = {
            throw Exception(
                "This really shouldn't ever happen!" +
                "If it did, something went *catastrophically* wrong." +
                "Or I can't type. Either possible.")
        },
        onSuccess = { it }
    )
    val newVersion = SemVer.parseString(res.tag_name).fold(
        onFailure = {
            // TODO: Handle case where I fail typing the tag name. For now, we pretend the request failed.
            return NetResponse.RequestFailure(req.status)
        },
        onSuccess = { it }
    )

    return ourVersion.compareTo(newVersion).let {
        when {
            // Up to date
            it == 0 -> NetResponse.Success(Pair(true, null))
            // Out of date
            it < 0 ->
                if (!res.prerelease || allowPrereleases)
                    NetResponse.Success(Pair(false, res))
                else
                    NetResponse.Success(Pair(true, null))
            // Newer than current release
            else -> NetResponse.Success(Pair(true, null))
        }
    }
}

data class SemVer(
    val major: UInt,
    val minor: UInt,
    val patch: UInt,
    val suffix: String? = null
) : Comparable<SemVer> {

    companion object {
        /**
         * Attempts to parse [str] in the form `major.minor.patch[-SUFFIX]`.
         */
        fun parseString(str: String): Result<SemVer> {
            val split = str.split('.', limit = 3)

            if (split.size != 3) {
                return Result.failure(Exception("Input has incorrect number of '.' delimiters.`"))
            }

            val majorStr = split[0]
            val minorStr = split[1]

            val patchSplit = split[2].split('-', limit = 2)
            val patchStr = patchSplit[0]
            val suffix = if (patchSplit.size == 1) null else patchSplit[1]

            val major = majorStr.toUIntOrNull() ?: return Result.failure(Exception("Major version $majorStr invalid."))
            val minor = minorStr.toUIntOrNull() ?: return Result.failure(Exception("Minor version $minorStr invalid."))
            val patch = patchStr.toUIntOrNull() ?: return Result.failure(Exception("Patch version $patchStr invalid."))

            return Result.success(SemVer(major, minor, patch, suffix))
        }
    }

    override fun compareTo(other: SemVer): Int {

        if (other.major != this.major) {
            return (this.major - other.major).toInt()
        }

        if (other.minor != this.minor) {
            return (this.minor - other.minor).toInt()
        }

        if (other.patch != this.patch) {
            return (this.patch - other.patch).toInt()
        }

        return 0
    }

    override fun toString(): String {
        return "$major.$minor.$patch${if (suffix != null) "-$suffix" else ""}"
    }
}