package org.orca.pelorus.data.prefs

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.orca.kotlass.client.CompassUserCredentials
import org.orca.trulysharedprefs.ISharedPrefs

class Prefs(private val prefs: ISharedPrefs) : IMutablePrefs {

    private val mutableCompassCredentials = MutableStateFlow(getCompassCredentials())
    override val compassCredentials = mutableCompassCredentials.asStateFlow()

    private val mutableVerifyValidLogin = MutableStateFlow(getVerifyValidLogin())
    override val verifyValidLogin = mutableVerifyValidLogin.asStateFlow()

    override fun setCompassCredentials(credentials: CompassUserCredentials) {

        if (getCompassCredentials() == credentials) {
            return
        }

        prefs.editSync {
            putString(Key.CompassDomain.name, credentials.domain)
            putInt(Key.CompassUserId.name, credentials.userId)
            putString(Key.CompassCookie.name, credentials.cookie)
        }

        mutableCompassCredentials.update { credentials }

    }

    override fun clearCompassCredentials() {

        prefs.editSync {
            remove(Key.CompassDomain.name)
            remove(Key.CompassUserId.name)
            remove(Key.CompassCookie.name)
        }

    }

    override fun setVerifyValidLogin(verify: Boolean) {

        prefs.editSync {
            putBoolean(Key.VerifyValidLogin.name, verify)
        }

        mutableVerifyValidLogin.update { verify }

    }

    private fun getCompassCredentials(): CompassUserCredentials? {

        val domain = prefs.getStringOrNull(Key.CompassDomain.name) ?: return null
        val userId = prefs.getIntOrNull(Key.CompassUserId.name) ?: return null
        val cookie = prefs.getStringOrNull(Key.CompassCookie.name) ?: return null

        return CompassUserCredentials(
            domain = domain,
            userId = userId,
            cookie = cookie
        )

    }

    private fun getVerifyValidLogin() =
        prefs.getBoolean(Key.VerifyValidLogin.name, true)

    /**
     * Keys for the preferences file.
     */
    private enum class Key {

        /**
         * Whether to verify our login credentials are valid on startup.
         */
        VerifyValidLogin,

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


}
