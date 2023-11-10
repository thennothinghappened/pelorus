package org.orca.common.ui.preview.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.orca.common.ui.theme.AppTheme
import org.orca.common.ui.screens.ActivityContent
import org.orca.htmltext.HtmlText

@Preview
@Composable
private fun ActivityPreview() {
    AppTheme {
        Surface {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                ActivityContent(
                    "",
                    "Teacher TEACHER",
                    "Replacement TEACHER",
                    "Room 1",
                    "Room 2",
                    lessonPlan = {
                        HtmlText("Lesson plan.")
                    }
                )
            }
        }
    }
}