package org.orca.common.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CornersCard(
    title: String,
    topRight: String,
    bottomLeft: String,
    bottomRight: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke? = null,
    onClick: () -> Unit
) {
    Card(colors = colors, onClick = onClick, modifier = modifier, border = border) { StringCornersCardContent(
        title,
        topRight,
        bottomLeft,
        bottomRight
    ) }
}

@Composable
fun OutlinedCornersCard(
    title: String,
    topRight: String,
    bottomLeft: String,
    bottomRight: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    border: BorderStroke = CardDefaults.outlinedCardBorder(),
    onClick: () -> Unit
) {
    CornersCard(title, topRight, bottomLeft, bottomRight, modifier, colors, border, onClick)
}

@Composable
fun CornersCard(
    title: String,
    topRight: String,
    bottomLeft: String,
    bottomRight: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke? = null
) {
    Card(colors = colors, modifier = modifier, border = border) { StringCornersCardContent(
        title,
        topRight,
        bottomLeft,
        bottomRight
    ) }
}

@Composable
fun OutlinedCornersCard(
    title: String,
    topRight: String,
    bottomLeft: String,
    bottomRight: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    border: BorderStroke = CardDefaults.outlinedCardBorder()
) {
    CornersCard(title, topRight, bottomLeft, bottomRight, modifier, colors, border)
}

@Composable
private fun StringCornersCardContent(
    title: String,
    topRight: String,
    bottomLeft: String,
    bottomRight: String
) {
    Box(Modifier.padding(8.dp).fillMaxSize()) {
        Text(
            title,
            modifier = Modifier.align(Alignment.TopStart),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            overflow = TextOverflow.Clip,
            maxLines = 1
        )
        Text(
            topRight,
            modifier = Modifier.align(Alignment.TopEnd),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Clip,
            maxLines = 1
        )
        Text(
            bottomLeft,
            modifier = Modifier.align(Alignment.BottomStart),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Clip,
            maxLines = 1
        )
        Text(
            bottomRight,
            modifier = Modifier.align(Alignment.BottomEnd),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Clip,
            maxLines = 1
        )
    }
//    Row(
//        Modifier
//            .padding(8.dp)
//    ) {
//        Text(
//            title,
//            style = MaterialTheme.typography.labelLarge,
//            fontWeight = FontWeight.ExtraBold,
//            overflow = TextOverflow.Clip,
//            maxLines = 1
//        )
//        Spacer(Modifier.weight(1f))
//        Text(
//            topRight,
//            style = MaterialTheme.typography.labelMedium,
//            fontWeight = FontWeight.Bold,
//            overflow = TextOverflow.Clip,
//            maxLines = 1
//        )
//    }
//    Row(
//        Modifier
//            .padding(8.dp)
//    ) {
//        Text(
//            bottomLeft,
//            style = MaterialTheme.typography.labelMedium,
//            fontWeight = FontWeight.Bold,
//            overflow = TextOverflow.Clip,
//            maxLines = 1
//        )
//        Spacer(Modifier.weight(1f))
//        Text(
//            bottomRight,
//            style = MaterialTheme.typography.labelMedium,
//            fontWeight = FontWeight.Bold,
//            overflow = TextOverflow.Clip,
//            maxLines = 1
//        )
//    }
}