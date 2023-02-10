package org.orca.common.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun FlairedCard(
    modifier: Modifier,
    flairColor: Color,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            flairColor
        ),
        elevation = elevation,
        border = border,
    ) {
        Card(
            content = content,
            colors = colors,
            shape = shape,
            modifier = Modifier
                .padding(12.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlairedCard(
    modifier: Modifier,
    flairColor: Color,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    onClick: () -> Unit,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            flairColor
        ),
        elevation = elevation,
        border = border,
        onClick = onClick
    ) {
        Card(
            content = content,
            colors = colors,
            shape = shape,
            modifier = Modifier
                .padding(12.dp, 0.dp, 0.dp, 0.dp)
                .fillMaxWidth()
        )
    }
}