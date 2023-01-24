package org.orca.common.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier,
        colors = colors,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        content = content
    )
}