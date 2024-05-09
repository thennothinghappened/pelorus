package org.orca.pelorus.data.repository.activity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.orca.kotlass.client.requests.IActivitiesClient
import org.orca.pelorus.cache.Activity
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.getOrElse
import kotlin.coroutines.CoroutineContext
import org.orca.kotlass.data.activity.Activity as KotlassActivity

class ActivityRepository(
    private val localActivityDataSource: ILocalActivityDataSource,
    private val remoteClient: IActivitiesClient,
    private val ioContext: CoroutineContext = Dispatchers.IO
) : IActivityRepository {

    override suspend fun get(id: Int): Response.Result<Activity> = localActivityDataSource
        .get(id)
        .fold(
            onData = { Response.Success(it) },
            onNotCached = {
                fetch(id).fold(
                    onFailure = { Response.Failure(it) },
                    onSuccess = {
                        localActivityDataSource.insert(it)
                        Response.Success(it)
                    }
                )
            }
        )

    override suspend fun fetch(id: Int): Response.Result<Activity> =
        Response.Success(
            withContext(ioContext) { remoteClient.getActivity(id) }
                .getOrElse {
                    return Response.Failure(RepositoryError.RemoteClientError(it))
                }
                .asActivity(Clock.System.now())
        )

    override suspend fun fetchStandardActivities(): Response.Result<List<Activity>> {
        TODO("Not yet implemented")
    }

}

private fun KotlassActivity.asActivity(cachedAt: Instant) = Activity(
    id = id,
    name = name,
    cachedAt = cachedAt
)
