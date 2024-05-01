package org.orca.pelorus.prefs

import org.orca.kotlass.client.CompassUserCredentials
import org.orca.trulysharedprefs.ISharedPrefs

class Prefs(private val prefs: ISharedPrefs) : IMutablePrefs {

    override fun getCompassCredentials(): CompassUserCredentials? {

        val domain = prefs.getStringOrNull(PrefKey.CompassDomain.name) ?: return null
        val userId = prefs.getIntOrNull(PrefKey.CompassUserId.name) ?: return null
        val cookie = prefs.getStringOrNull(PrefKey.CompassCookie.name) ?: return null

        return CompassUserCredentials(
            domain = domain,
            userId = userId,
            cookie = cookie
        )

    }

    override fun setCompassCredentials(credentials: CompassUserCredentials) {

        prefs.editSync {
            putString(PrefKey.CompassDomain.name, credentials.domain)
            putInt(PrefKey.CompassUserId.name, credentials.userId)
            putString(PrefKey.CompassCookie.name, credentials.cookie)
        }

    }

    override fun clearCompassCredentials() {

        prefs.editSync {
            remove(PrefKey.CompassDomain.name)
            remove(PrefKey.CompassUserId.name)
            remove(PrefKey.CompassCookie.name)
        }

    }

}

/**
 * Keys for the preferences file.
 */
private enum class PrefKey {

    /**
     * The domain of the Compass server instance.
     */
    CompassDomain,

    /**
     * The User ID of the user we're logged in as.
     */
    CompassUserId,

    /**
     * The auth cookie of our user.
     */
    CompassCookie

}
