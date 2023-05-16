package org.orca.common.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import org.orca.common.ui.defaults.Sizing

@Composable
fun FlairedCard(
    modifier: Modifier = Modifier,
    flairColor: Color,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier.height(IntrinsicSize.Min),
        shape = shape,
        elevation = elevation,
        border = border,
    ) {
        Box {
            Box(
                Modifier
                    .background(flairColor)
                    .fillMaxHeight()
                    .width(Sizing.FlairedCard.FlairWidth * 2)
            )
            Card(
                content = content,
                colors = colors,
                shape = shape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Sizing.FlairedCard.FlairWidth)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlairedCard(
    modifier: Modifier = Modifier,
    flairColor: Color,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    onClick: () -> Unit,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = modifier.height(IntrinsicSize.Min),
        shape = shape,
        elevation = elevation,
        border = border,
        onClick = onClick
    ) {
        Box {
            Box(
                Modifier
                    .background(flairColor)
                    .fillMaxHeight()
                    .width(Sizing.FlairedCard.FlairWidth * 2)
            )
            Card(
                content = content,
                colors = colors,
                shape = shape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Sizing.FlairedCard.FlairWidth)
            )
        }
    }
}