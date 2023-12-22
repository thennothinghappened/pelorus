package org.orca.pelorus.ui.theme.transitions

import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import cafe.adriel.voyager.transitions.ScreenTransitionContent

@Composable
fun FadeScaleTransition(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = spring(stiffness = Spring.StiffnessMedium),
    content: ScreenTransitionContent = { it.Content() }
) {
    ScreenTransition(
        navigator = navigator,
        modifier = modifier,
        content = content,
        transition = {
            scaleIn(animationSpec = animationSpec) + fadeIn(animationSpec = animationSpec) togetherWith
            scaleOut(animationSpec = animationSpec) + fadeOut(animationSpec = animationSpec)
        }
    )
}