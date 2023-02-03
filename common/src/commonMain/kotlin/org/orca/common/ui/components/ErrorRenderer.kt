package org.orca.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun ErrorRenderer(
    error: Throwable
) {
    Text(buildAnnotatedString {
        withStyle(MaterialTheme.typography.headlineMedium.toParagraphStyle()) {
            append("An error occurred!")
        }
        withStyle(MaterialTheme.typography.titleLarge.toParagraphStyle()) {
            append(error.message.toString())
        }
        withStyle(MaterialTheme.typography.bodySmall.toParagraphStyle()) {
            error.stackTrace.forEach { append(it.toString() + "\n") }
        }
    })

}