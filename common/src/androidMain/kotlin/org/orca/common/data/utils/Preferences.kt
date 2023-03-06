package org.orca.common.data.utils

import android.content.SharedPreferences

actual class Preferences(
    private val p: SharedPreferences
) {

    private val e = p.edit()

    actual fun getString(key: String, def: String): String =
        p.getString(key, def)!!

    actual fun getInt(key: String, def: Int): Int =
        p.getInt(key, def)

    actual fun getBoolean(key: String, def: Boolean): Boolean =
        p.getBoolean(key, def)

    actual fun putString(key: String, value: String) {
        e.putString(key, value)
        e.commit()
    }

    actual fun putInt(key: String, value: Int) {
        e.putInt(key, value)
        e.commit()
    }

    actual fun putBoolean(key: String, value: Boolean) {
        e.putBoolean(key, value)
        e.commit()
    }
}