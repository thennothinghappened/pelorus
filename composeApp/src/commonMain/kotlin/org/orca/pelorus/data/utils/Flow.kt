package org.orca.pelorus.data.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transform
import org.orca.pelorus.data.repository.Response

/**
 * Returns a flow that contains only [Response.Result] final results.
 */
fun <T> Flow<Response<T>>.filterIsResult() = filterIsInstance<Response.Result<T>>()

/**
 * Map the given flow if it is a result, otherwise pass a loading value along the chain.
 */
inline fun <T, R> Flow<Response<T>>.mapResultOrLoading(
    crossinline transform: suspend (value: Response.Result<T>) -> Response<R>
): Flow<Response<R>> = map {
    if (it is Response.Result) {
        transform(it)
    } else {
        Response.Loading()
    }
}

/**
 * Transform a response result in a flow.
 */
inline fun <T, R> Flow<Response<T>>.foldResponse(
    crossinline transformSuccess: suspend (value: Response.Success<T>) -> R,
    crossinline transformFailure: suspend (value: Response.Failure<T>) -> R,
    crossinline transformLoading: suspend (value: Response.Loading<T>) -> R
): Flow<R> = map {
    when (it) {
        is Response.Success -> transformSuccess(it)
        is Response.Failure -> transformFailure(it)
        is Response.Loading -> transformLoading(it)
    }
}

/**
 * Combine two result flows.
 */
inline fun <T1, T2, R> Flow<Response<T1>>.combineOrLoading(
    flow: Flow<Response<T2>>,
    crossinline transform: suspend (a: Response.Result<T1>, b: Response.Result<T2>) -> Response<R>
): Flow<Response<R>> = combine(flow) { aResponse, bResponse ->

    val a = aResponse.resultOrElse { return@combine Response.Loading() }
    val b = bResponse.resultOrElse { return@combine Response.Loading() }

    transform(a, b)

}

/**
 * Combine two result flows.
 */
inline fun <T1, T2, R> Flow<Response<T1>>.combineSuccessOrPass(
    flow: Flow<Response<T2>>,
    crossinline transform: suspend (a: T1, b: T2) -> Response<R>
): Flow<Response<R>> = combine(flow) { aResponse, bResponse ->

    val a = aResponse
        .resultOrElse { return@combine Response.Loading() }
        .getOrElse { return@combine Response.Failure(it) }

    val b = bResponse
        .resultOrElse { return@combine Response.Loading() }
        .getOrElse { return@combine Response.Failure(it) }

    transform(a, b)

}

