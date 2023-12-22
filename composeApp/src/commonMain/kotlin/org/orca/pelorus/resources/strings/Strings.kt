package org.orca.pelorus.resources.strings

import androidx.compose.ui.text.AnnotatedString

data class Strings(
    val loginWelcome: String,
    val loginTagline: String,

    val loginCookieTitle: String,
    val loginCookieDescription: AnnotatedString,
    val loginCookieLongDescription: AnnotatedString,
    val loginCookieFieldCookie: String,
    val loginCookieFieldUserId: String,
    val loginCookieFieldDomain: String,
    val loginCookieHelpTitle: String,
    val loginCookieHelpCookie: AnnotatedString,
    val loginCookieHelpUserId: AnnotatedString,
    val loginCookieHelpDomain: AnnotatedString,
)

object Locales {
    const val ENGLISH = "en"
}