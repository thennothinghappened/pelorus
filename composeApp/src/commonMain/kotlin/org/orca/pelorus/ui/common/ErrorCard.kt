package org.orca.pelorus.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

    var expanded by remember { mutableStateOf(false) }

    ErrorCard(
        title = title,
        error = error,
        solution = solution,
        expanded = expanded,
        onExpand = { expanded = !expanded }
    )
}

/**
 * A card that displays the passed error with title message. Can be expanded on click.
 */
@Composable
fun ErrorCard(
    title: String,
    error: String,
    solution: String? = null
) {
    ErrorCard(
        title = title,
        error = error,
        solution = solution,
        expanded = true
    )
}

/**
 * A card that displays the passed error with title message and may be expanded.
 */
@Composable
private fun ErrorCard(
    title: String,
    error: String,
    solution: String? = null,
    expanded: Boolean = true,
    onExpand: () -> Unit = {}
) {
    Card(
        onClick = onExpand,
        colors = CardDefaults
            .cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(Modifier.padding(sizing.paddingCardInner)) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            AnimatedVisibility(expanded) {
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
