package org.orca.common.data

import org.orca.kotlass.CompassClientCredentials
import org.orca.common.data.utils.Preferences

private object credentialLocations {
    val cookie = "credentials/cookie"
    val domain = "credentials/domain"
    val userId = "credentials/userId"
}

fun getClientCredentials(
    preferences: Preferences
): CompassClientCredentials? {
    val cookie = preferences.get(credentialLocations.cookie, "")
    val domain = preferences.get(credentialLocations.domain, "")
    val userId = preferences.getInt(credentialLocations.userId, -1)

    println("$cookie, $domain, $userId")

    if (
        cookie == "" ||
        domain == "" ||
        userId == -1
        )
        return null

    return object : CompassClientCredentials {
        override val cookie = cookie
        override val domain = domain
        override val userId = userId
    }
}

fun setClientCredentials(
    preferences: Preferences,
    compassClientCredentials: CompassClientCredentials
) {
    preferences.put(credentialLocations.cookie, compassClientCredentials.cookie)
    preferences.put(credentialLocations.domain, compassClientCredentials.domain)
    preferences.putInt(credentialLocations.userId, compassClientCredentials.userId)
}

fun clearClientCredentials(
    preferences: Preferences
) {
    preferences.put(credentialLocations.cookie, "")
    preferences.put(credentialLocations.domain, "")
    preferences.putInt(credentialLocations.userId, -1)
}