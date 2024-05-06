package org.orca.pelorus.data.repository

import org.orca.kotlass.client.CompassApiError

/**
 * A response from a repository.
 */
sealed interface Response<T> {

    data class Loading<T>(val progress: T? = null) : Response<T>

    /**
     * A successful response from the remote.
     */
    data class Success<T>(val data: T) : Response<T>

    /**
     * A failure response from the remote.
     */
    data class Failure<T>(val error: RepositoryError) : Response<T>

}

/**
 * An error returned by a repository.
 */
sealed interface RepositoryError {

    /**
     * An error returned by the Compass client.
     */
    data class RemoteClientError(val error: CompassApiError) : RepositoryError

    /**
     * An error indicating the requested data was not found.
     */
    data object NotFoundError : RepositoryError

}

/**
 * Convert a CompassApiError from Kotlass to our Response form.
 */
fun <T> CompassApiError.asResponse() = Response.Failure<T>(
    RepositoryError.RemoteClientError(this)
)
