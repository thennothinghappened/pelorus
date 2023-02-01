package org.orca.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorRenderer(
    error: Throwable
) {
    Column {
        Text("An error occurred!", style = MaterialTheme.typography.headlineMedium)
        Text(error.message.toString(), style = MaterialTheme.typography.titleLarge)
        error.stackTrace.forEach { Text(it.toString(), style = MaterialTheme.typography.bodySmall) }
    }

}