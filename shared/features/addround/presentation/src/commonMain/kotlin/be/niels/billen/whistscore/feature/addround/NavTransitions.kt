package be.niels.billen.whistscore.feature.addround

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

@OptIn(ExperimentalAnimationApi::class)
fun <T : NavKey> pushSlideTransition(): (AnimatedContentTransitionScope<Scene<T>>) -> ContentTransform = {
    slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(targetOffsetX = { -it })
}

@OptIn(ExperimentalAnimationApi::class)
fun <T : NavKey> popSlideTransition(): (AnimatedContentTransitionScope<Scene<T>>) -> ContentTransform = {
    slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
}
