package org.orca.common.ui.preview.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDateTime
import org.orca.common.ui.theme.AppTheme
import org.orca.common.ui.screens.ActionCentreEventComponent
import org.orca.common.ui.screens.ActionCentreEventSession

@Preview
@Composable
fun ActionCentreEventComponentPreview() {
    AppTheme {
        Surface(Modifier.fillMaxSize()) {
            ActionCentreEventComponent(
                "To do stuff",
                listOf(
                    ActionCentreEventSession(
                        "Somewhere",
                        LocalDateTime(2023, 1, 1, 1, 0, 0, 0),
                        LocalDateTime(2023, 1, 1, 2, 0, 0, 0)
                    )
                ),
                "We gonna do some stuff.",
                "Anything",
                "Nothing"
            )
        }
    }
}