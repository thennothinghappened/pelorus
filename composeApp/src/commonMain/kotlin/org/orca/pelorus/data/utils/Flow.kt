package org.orca.pelorus.data.utils

import kotlinx.coroutines.flow.Flow
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
