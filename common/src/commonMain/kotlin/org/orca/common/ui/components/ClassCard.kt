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

@Composable
fun ClassCard(
    className: String,
    roomName: String,
    teacherName: String,
    time: LocalTime?,
    onClick: () -> Unit = {}
) = CornersCard(
    className,
    "Room $roomName",
    teacherName,
    if (time != null) "${time.hour}:${time.minute}" else "",
    onClick = onClick
)