package org.orca.pelorus.data.repository

import org.orca.kotlass.client.CompassApiError

/**
 * A response from the remote Compass API.
 */
sealed interface ApiResponse<T> {

    /**
     * A successful response from the remote.
     */
    data class Success<T>(val data: T) : ApiResponse<T>

    /**
     * A failure response from the remote.
     */
    data class Failure<T>(val error: CompassApiError) : ApiResponse<T>

}
