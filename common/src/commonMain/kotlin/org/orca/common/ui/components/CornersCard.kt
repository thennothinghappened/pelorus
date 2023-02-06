package org.orca.common.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalTime

@Composable
fun CornersCard(
    title: String,
    topRight: String,
    bottomLeft: String,
    bottomRight: String,
    colors: CardColors = CardDefaults.cardColors(),
    onClick: () -> Unit = {}
) {
    BaseCard(colors = colors, onClick = onClick) {
        Row(
            Modifier
                .padding(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
            Spacer(Modifier.weight(1f))
            Text(
                topRight,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
        Row(
            Modifier
                .padding(8.dp)
        ) {
            Text(
                bottomLeft,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
            Spacer(Modifier.weight(1f))
            Text(
                bottomRight,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
    }
}