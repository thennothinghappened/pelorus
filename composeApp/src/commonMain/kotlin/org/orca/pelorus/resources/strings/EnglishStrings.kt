package org.orca.pelorus.resources.strings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = Locales.ENGLISH, default = true)
val EnglishStrings = Strings(
    hi = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Green)) {
            append("hi!!")
        }
    }
)