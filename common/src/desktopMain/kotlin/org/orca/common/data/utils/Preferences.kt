package org.orca.common.data.utils

actual class Preferences {
    private val p = java.util.prefs.Preferences.userRoot()

    actual fun get(key: String, def: String): String =
        p.get(key, def)

    actual fun getInt(key: String, def: Int): Int =
        p.getInt(key, def)

    actual fun put(key: String, value: String) =
        p.put(key, value)

    actual fun putInt(key: String, value: Int) =
        p.putInt(key, value)
}