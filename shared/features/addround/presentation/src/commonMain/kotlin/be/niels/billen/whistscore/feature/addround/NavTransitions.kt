package be.niels.billen.whistscore.feature.addround

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation3.scene.Scene

fun <T : Any> slideTransition(scale: Int): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    slideInHorizontally(
        initialOffsetX = { it * scale },
        animationSpec = tween(durationMillis = animationDuration)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { -it * scale },
        animationSpec = tween(durationMillis = animationDuration)
    )
}

private const val animationDuration = 250