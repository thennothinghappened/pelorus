package org.orca.common.data

import org.orca.common.data.utils.DefaultPreferences
import org.orca.kotlass.CompassClientCredentials
import org.orca.common.data.utils.Preferences
import org.orca.common.data.utils.get
import org.orca.common.data.utils.put

fun getClientCredentials(
    preferences: Preferences
): CompassClientCredentials? {
    val cookie = preferences.get(DefaultPreferences.Credentials.cookie)
    val domain = preferences.get(DefaultPreferences.Credentials.domain)
    val userId = preferences.get(DefaultPreferences.Credentials.userId)

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
    preferences.put(DefaultPreferences.Credentials.cookie, compassClientCredentials.cookie)
    preferences.put(DefaultPreferences.Credentials.domain, compassClientCredentials.domain)
    preferences.put(DefaultPreferences.Credentials.userId, compassClientCredentials.userId)
}

fun clearClientCredentials(
    preferences: Preferences
) {
    preferences.put(DefaultPreferences.Credentials.cookie, "")
    preferences.put(DefaultPreferences.Credentials.domain, "")
    preferences.put(DefaultPreferences.Credentials.userId, -1)
}