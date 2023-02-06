package org.orca.common.data.utils

expect class Preferences {
    fun get(key: String, def: String): String
    fun getInt(key: String, def: Int): Int
    fun getBoolean(key: String, def: Boolean): Boolean
    fun put(key: String, value: String)
    fun putInt(key: String, value: Int)
    fun putBoolean(key: String, value: Boolean)
}

fun Preferences.get(preference: DefaultPreferences.Preference.BooleanPreference): Boolean =
    getBoolean(preference.url, preference.default)

fun Preferences.get(preference: DefaultPreferences.Preference.StringPreference): String =
    get(preference.url, preference.default)

fun Preferences.get(preference: DefaultPreferences.Preference.IntPreference): Int =
    getInt(preference.url, preference.default)

fun Preferences.put(preference: DefaultPreferences.Preference.BooleanPreference, value: Boolean) =
    putBoolean(preference.url, value)

fun Preferences.put(preference: DefaultPreferences.Preference.StringPreference, value: String) =
    put(preference.url, value)

fun Preferences.put(preference: DefaultPreferences.Preference.IntPreference, value: Int) =
    putInt(preference.url, value)

object DefaultPreferences {
    sealed class Preference<T>(val url: String, val default: T) {
        class BooleanPreference(url: String, default: Boolean) : Preference<Boolean>(url, default)
        class StringPreference(url: String, default: String) : Preference<String>(url, default)
        class IntPreference(url: String, default: Int) : Preference<Int>(url, default)
    }

    object Credentials {
        private val url = "credentials/"
        val cookie = Preference.StringPreference(url + "cookie", "")
        val domain = Preference.StringPreference(url + "domain", "")
        val userId = Preference.IntPreference(url + "userId", -1)
    }
    object Api {
        private val url = "api/"
        val verifyCredentials = Preference.BooleanPreference(url + "verifyCredentials", true)
    }
}