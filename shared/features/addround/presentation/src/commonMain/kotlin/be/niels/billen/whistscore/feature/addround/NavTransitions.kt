package be.niels.billen.whistscore.feature.addround

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene

fun <T : Any> slideTransition(scale: Int): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform =
    {
        slideInHorizontally(
            initialOffsetX = { it * scale },
            animationSpec = tween(durationMillis = ANIMATION_DURATION),
        ) togetherWith
            slideOutHorizontally(
                targetOffsetX = { -it * scale },
                animationSpec = tween(durationMillis = ANIMATION_DURATION),
            )
    }

private const val ANIMATION_DURATION = 250
