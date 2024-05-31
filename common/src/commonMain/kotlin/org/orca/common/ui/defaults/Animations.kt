package org.orca.common.ui.defaults

import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale

object Animations {
    val zoomIn = fade() + scale(
        frontFactor = 0.95f,
        backFactor = 1.15f
    )
}