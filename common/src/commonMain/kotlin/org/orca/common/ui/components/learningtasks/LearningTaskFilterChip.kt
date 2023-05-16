package org.orca.common.ui.components.learningtasks

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import org.orca.common.ui.defaults.Font

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningTaskFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors()
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(label, style = Font.filterChip)
        },
        colors = colors
    )
}