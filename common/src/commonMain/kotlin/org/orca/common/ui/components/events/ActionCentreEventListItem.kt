package org.orca.common.ui.components.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.orca.common.ui.components.common.FlairedCard
import org.orca.kotlass.data.ActionCentreEventAttendanceStatus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun ActionCentreEventListItem(
    name: String,
    dateLine: String,
    attendanceMode: ActionCentreEventAttendanceStatus,
    onClick: () -> Unit
) {
    FlairedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 16.dp),
        flairColor = when (attendanceMode) {
            ActionCentreEventAttendanceStatus.PENDING -> MaterialTheme.colorScheme.inverseOnSurface
            ActionCentreEventAttendanceStatus.ATTENDING -> Color.Green
            ActionCentreEventAttendanceStatus.AFTER_CONSENT_DATE -> Color.Red
        },
        onClick = onClick
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(
                name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                dateLine,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}