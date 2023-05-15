package org.orca.common.data.utils

import kotlinx.datetime.LocalTime

expect class Preferences {
    fun getString(key: String, def: String): String
    fun getInt(key: String, def: Int): Int
    fun getBoolean(key: String, def: Boolean): Boolean
    fun putString(key: String, value: String)
    fun putInt(key: String, value: Int)
    fun putBoolean(key: String, value: Boolean)
}

fun Preferences.get(preference: DefaultPreferences.Preference.BooleanPreference): Boolean =
    getBoolean(preference.url, preference.default)

fun Preferences.get(preference: DefaultPreferences.Preference.StringPreference): String =
    getString(preference.url, preference.default)

fun Preferences.get(preference: DefaultPreferences.Preference.IntPreference): Int =
    getInt(preference.url, preference.default)

fun Preferences.get(preference: DefaultPreferences.Preference.LocalTimePreference): LocalTime =
    LocalTime.parse(getString(preference.url, preference.default.toString()))

fun Preferences.put(preference: DefaultPreferences.Preference.BooleanPreference, value: Boolean) =
    putBoolean(preference.url, value)

fun Preferences.put(preference: DefaultPreferences.Preference.StringPreference, value: String) =
    putString(preference.url, value)

fun Preferences.put(preference: DefaultPreferences.Preference.IntPreference, value: Int) =
    putInt(preference.url, value)

fun Preferences.put(preference: DefaultPreferences.Preference.LocalTimePreference, value: LocalTime) =
    putString(preference.url, value.toString())

object DefaultPreferences {
    sealed class Preference<T>(val url: String, val default: T) {
        class BooleanPreference(url: String, default: Boolean) : Preference<Boolean>(url, default)
        class StringPreference(url: String, default: String) : Preference<String>(url, default)
        class IntPreference(url: String, default: Int) : Preference<Int>(url, default)
        class LocalTimePreference(url: String, default: LocalTime) : Preference<LocalTime>(url, default)
    }

    object Credentials {
        private const val url = "credentials/"
        val cookie = Preference.StringPreference(url + "cookie", "")
        val domain = Preference.StringPreference(url + "domain", "")
        val userId = Preference.IntPreference(url + "userId", -1)
        val schoolStartTime = Preference.LocalTimePreference(url + "schoolStartTime", LocalTime(9, 0, 0))
    }
    object Api {
        private const val url = "api/"
        val verifyCredentials = Preference.BooleanPreference(url + "verifyCredentials", true)
        val useDevMode = Preference.BooleanPreference(url + "useDevMode", false)
    }

    object App {
        private const val url = "app/"
        val experimentalClassList = Preference.BooleanPreference(url + "experimentalClassList", true)
        val dontReplaceStack = Preference.BooleanPreference(url + "dontReplaceStack", false)
    }
}