package org.orca.pelorus.resources.strings

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings
import org.orca.pelorus.utils.Platform
import org.orca.pelorus.utils.platform

@LyricistStrings(languageTag = Locales.ENGLISH, default = true)
val EnglishStrings = Strings(
    loginWelcome = "Welcome to Pelorus!",
    loginTagline = "Choose an option to login:",

    loginCookieTitle = "Login via Cookie",
    loginCookieDescription = buildAnnotatedString {
        append("(For debugging purposes) Use your ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Compass credentials")
        }
        append(" from your browser to log into Pelorus.")
    },

    loginCookieLongDescription = buildAnnotatedString {
        when (platform) {
            is Platform.Mobile -> {
                append("On a Desktop, open the Compass website, and press (Windows or Linux): ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Ctrl+Shift+I")
                }

                append(" or (MacOS): ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Cmd+Option+I")
                }
            }
            is Platform.Desktop -> when (platform) {
                is Platform.Desktop.MacOS -> {
                    append("Open the Compass website, and press ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Cmd+Option+I")
                    }
                }
                else -> {
                    append("Open the Compass website, and press ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Ctrl+Shift+I")
                    }
                }
            }
        }

        append(" to access the Developer Tools, which will be needed to get the below information.")
    },

    loginCookieFieldCookie = "Compass Login Cookie",
    loginCookieFieldUserId = "Compass User ID",
    loginCookieFieldDomain = "Compass Instance Domain",

    loginCookieHelpTitle = "Where to find this",
    loginCookieHelpCookie = buildAnnotatedString { append("todo!") },
    loginCookieHelpUserId = buildAnnotatedString { append("todo!") },
    loginCookieHelpDomain = buildAnnotatedString { append("todo!") }
)