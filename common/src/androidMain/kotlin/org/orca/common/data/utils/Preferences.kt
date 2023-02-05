package org.orca.common.data.utils

import android.content.SharedPreferences

actual class Preferences(
    private val p: SharedPreferences
) {

    private val e = p.edit()

    actual fun get(key: String, def: String): String =
        p.getString(key, def)!!

    actual fun getInt(key: String, def: Int): Int =
        p.getInt(key, def)

    actual fun put(key: String, value: String) {
        e.putString(key, value)
        e.commit()
    }

    actual fun putInt(key: String, value: Int) {
        e.putInt(key, value)
        e.commit()
    }
}