package org.orca.pelorus.data.serializable

import org.orca.kotlass.client.CompassUserCredentials
import java.io.Serializable

/**
 * Copy of Kotlass's [CompassUserCredentials] but serializable as it doesn't have a serializer.
 */
data class SerializableCompassUserCredentials(
    val domain: String,
    val userId: Int,
    val cookie: String
) : Serializable {

    /**
     * Convert to the regular user credentials.
     */
    fun toNormal() = CompassUserCredentials(
        domain = domain,
        userId = userId,
        cookie = cookie
    )

}

/**
 * Convert to the serializable form.
 */
fun CompassUserCredentials.toSerializable() = SerializableCompassUserCredentials(
    domain = this.domain,
    userId = this.userId,
    cookie = this.cookie
)
