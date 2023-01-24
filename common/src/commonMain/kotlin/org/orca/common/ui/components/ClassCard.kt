package org.orca.common.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import kotlinx.datetime.LocalTime

class ClassListComponent(
    private val componentContext: ComponentContext,

) : ComponentContext by componentContext {


}

@Composable
private fun ClassItem(
    className: String,
    roomName: String,
    teacherName: String,
    time: LocalTime,
    onClick: () -> Unit
) {
    BaseCard {
        Row(
            Modifier
                .padding(8.dp)
        ) {
            Text(
                className,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
            Spacer(Modifier.weight(1f))
            Text(
                "Room ${roomName}",
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
        Row(
            Modifier
                .padding(8.dp)
        ) {
            Text(
                teacherName,
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${time.hour}:${time.minute}",
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
    }
}