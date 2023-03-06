package org.orca.common.data.utils

actual class Preferences {
    private val p = java.util.prefs.Preferences.userRoot()

    actual fun getString(key: String, def: String): String =
        p.get(key, def)

    actual fun getInt(key: String, def: Int): Int =
        p.getInt(key, def)

    actual fun getBoolean(key: String, def: Boolean): Boolean =
        p.getBoolean(key, def)

    actual fun putString(key: String, value: String) =
        p.put(key, value)

    actual fun putInt(key: String, value: Int) =
        p.putInt(key, value)

    actual fun putBoolean(key: String, value: Boolean) =
        p.putBoolean(key, value)
}