package org.orca.common.ui.strings

import androidx.compose.ui.text.intl.Locale

/**
 * My quite possibly horrible solution for
 * i18n support. While there certainly won't actually
 * be any support for other languages than English,
 * this whole project is more of a learning experience
 * than anything either way.
 */
interface Strings {

    val login: Login
    val settings: Settings

    /**
     * Base login screen
     */
    interface Login {

        val topBarText: String
        val onboardHeading: String
        val onboardActionOptions: String

        val web: Web
        val cookie: Cookie

        /**
         * Web-based login
         */
        interface Web {
            val name: String
            val info: String
        }

        /**
         * Cookie-based login
         */
        interface Cookie {

            val name: String
            val info: String
            val errors: Errors
            val fields: Fields

            val explanation: String

            /**
             * Data entry fields
             */
            interface Fields {

                val cookie: Field
                val userId: Field
                val domain: Field

                class Field(val name: String, val info: String)
            }

            /**
             * Error explanations
             */
            interface Errors {
                val invalidInput: String
                val credentialsInvalid: String
                val checkNetwork: String
            }
        }
    }

    /**
     * Settings screen!
     */
    interface Settings {

        val verifyCredentials: Setting
        val experimentalClassList: Setting
        val useDevMode: Setting
        val checkForUpdates: Setting
        val logout: Setting

        class Setting(val name: String, val description: String)
    }
}

/**
 * Localised strings for the user's language
 */
val STRINGS: Strings = when (Locale.current.language) {
    "en" -> English
    else -> English
}