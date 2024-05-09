package org.orca.pelorus.ui.components.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.orca.pelorus.ui.theme.sizing

@Composable
fun CalendarEvent(
    title: String,
    staffName: String,
    modifier: Modifier = Modifier
) {

    Card(modifier) {
        Column(Modifier.fillMaxWidth().padding(sizing.paddingCardInnerSmall)) {

            Row {
                Text(title)
                Spacer(Modifier.weight(1f))
                Text(staffName)
            }

            Row {
                Spacer(Modifier.weight(1f))
            }

        }
    }

}
