package org.orca.common.data.utils

expect class Preferences {
    fun get(key: String, def: String): String
    fun getInt(key: String, def: Int): Int
    fun put(key: String, value: String)
    fun putInt(key: String, value: Int)
}