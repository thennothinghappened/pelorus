package org.orca.pelorus.data.services.authed

import org.orca.kotlass.client.CompassApiClient
import org.orca.kotlass.client.CompassUserCredentials

class AuthedServices(
    credentials: CompassUserCredentials
) : IAuthedServices {

    override val client = CompassApiClient(credentials)

}
