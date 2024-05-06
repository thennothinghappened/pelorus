package org.orca.pelorus.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.orca.pelorus.ui.theme.sizing

/**
 * A card that displays the passed error with title message. Can be expanded on click.
 */
@Composable
fun ExpandableError(
    title: String,
    error: String,
    solution: String? = null
) {

    var showError by remember { mutableStateOf(false) }

    Card(
        onClick = { showError = !showError },
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(Modifier.padding(sizing.paddingCardInner)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            AnimatedVisibility(showError) {
                Column {

                    MediumHorizontalDivider()

                    Text(error, style = MaterialTheme.typography.bodyMedium)

                    if (solution == null) {
                        return@AnimatedVisibility
                    }

                    MediumHorizontalDivider()

                    Text(solution, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

}
