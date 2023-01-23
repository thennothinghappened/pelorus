package org.orca.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.ui.RichText
import kotlinx.coroutines.runBlocking
import org.orca.common.data.TestCredentials
import org.orca.common.ui.components.HtmlText
import org.orca.common.ui.components.parseHtmlFromString
import org.orca.common.ui.utils.WindowSize
import org.orca.kotlass.CompassApiClient

@Composable
fun App(
    windowSize: WindowSize
) {
    val compassApiClient = remember { CompassApiClient(TestCredentials) }

    var textContent: String
    runBlocking { textContent =
        compassApiClient.getLessonsByInstanceIdQuick(TestCredentials.testInstanceId).data?.lessonPlan?.fileAssetId?.let {
            compassApiClient.downloadFile(
                it
            ).data
        }.toString()
    }

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item { RichText {
            HtmlText(parseHtmlFromString(textContent), style = TextStyle(color = MaterialTheme.colorScheme.onBackground))
        } }
    }
}