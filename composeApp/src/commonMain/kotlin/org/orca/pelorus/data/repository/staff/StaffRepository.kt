package org.orca.pelorus.data.repository.staff

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimePeriod
import org.orca.kotlass.client.requests.IUsersClient
import org.orca.pelorus.cache.Staff
import org.orca.pelorus.data.repository.RepositoryError
import org.orca.pelorus.data.repository.Response
import org.orca.pelorus.data.repository.getOrElse
import org.orca.kotlass.data.user.User as NetworkUser

class StaffRepository(
    private val localStaffDataSource: ILocalStaffDataSource,
    private val remoteClient: IUsersClient,
    private val ioContext: CoroutineDispatcher = Dispatchers.IO
) : IStaffRepository {

    override suspend fun get(id: Int): Response.Result<Staff?> = localStaffDataSource
        .get(id)
        .fold(
            onData = { Response.Success(it) },
            onNotCached = {
                fetch().fold(
                    onFailure = {
                        Response.Failure(it)
                    },
                    onSuccess = {
                        localStaffDataSource.update(it)
                        get(id)
                    }
                )
            }
        )

    override suspend fun fetch(): Response.Result<List<Staff>> =
        Response.Success(
            withContext(ioContext) { remoteClient.getAllStaff() }
                .getOrElse {
                    return Response.Failure(RepositoryError.RemoteClientError(it))
                }
                .map { it.asStaff() }
        )

}

/**
 * Convert the Compass User to our user type.
 */
private fun NetworkUser.asStaff(): Staff = Staff(
    id = id,
    codeName = codeName,
    firstName = firstName,
    lastName = lastName,
    photoUrl = null
)
