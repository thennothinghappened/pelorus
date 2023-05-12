package org.orca.common.ui.components.newsfeed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Newsfeed(
    modifier: Modifier = Modifier,
    feedContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Newsfeed", style = MaterialTheme.typography.labelMedium)
        feedContent()
    }
}