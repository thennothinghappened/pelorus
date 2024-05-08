package org.orca.pelorus.data.repository

import org.orca.kotlass.client.CompassApiError
import org.orca.kotlass.client.CompassApiResult

/**
 * A response from a repository.
 */
sealed interface Response<T> {

    /**
     * A final result from a repository.
     */
    sealed class Result<T> : Response<T> {

        inline fun <R> fold(
            onSuccess: (value: T) -> R,
            onFailure: (error: RepositoryError) -> R
        ) = when(this) {
            is Success -> onSuccess(data)
            is Failure -> onFailure(error)
        }

        inline fun <R : T> getOrElse(
            onFailure: (error: RepositoryError) -> R
        ) = when(this) {
            is Success -> data
            is Failure -> onFailure(error)
        }

    }

    data class Loading<T>(val progress: T? = null) : Response<T>

    /**
     * A successful response from the remote.
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * A failure response from the remote.
     */
    data class Failure<T>(val error: RepositoryError) : Result<T>()

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

fun <T> CompassApiResult<T>.asResponse(): Response<T> = when (this) {
    is CompassApiResult.Failure -> this.asResponse()
    is CompassApiResult.Success -> this.asResponse()
}

/**
 * Convert a failed API result to our form.
 */
fun <T, R> CompassApiResult.Failure<T>.asResponse() = Response.Failure<R>(
    RepositoryError.RemoteClientError(this.error)
)

/**
 * Convert a successful API result to our form.
 */
fun <T> CompassApiResult.Success<T>.asResponse() = Response.Success(this.data)


inline fun <R, T> CompassApiResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (error: CompassApiError) -> R
) = when(this) {
    is CompassApiResult.Success -> onSuccess(this.data)
    is CompassApiResult.Failure -> onFailure(this.error)
}

inline fun <R, T : R> CompassApiResult<T>.getOrElse(
    onFailure: (error: CompassApiError) -> R
) = when(this) {
    is CompassApiResult.Success -> this.data
    is CompassApiResult.Failure -> onFailure(this.error)
}

