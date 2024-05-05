package org.orca.pelorus.data.services.authed

import org.orca.kotlass.client.CompassApiClient

/**
 * The main app authenticated-scope services.
 */
interface IAuthedServices {

    /**
     * Test example Compass client instance.
     */
    val client: CompassApiClient

}
