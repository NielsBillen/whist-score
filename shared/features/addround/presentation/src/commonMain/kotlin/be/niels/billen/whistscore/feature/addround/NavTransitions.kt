package be.niels.billen.whistscore.feature.addround

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

/**
 * Animate the container between the two screens' sizes during a transition. Without this, the
 * container jumps to the wider screen's size for the whole animation, leaving a narrower screen
 * resting left-aligned until it snaps centred at the end. Clipping is off so the sliding screen
 * stays visible outside the shrinking container.
 */
val slideSizeTransform = SizeTransform(clip = false)

/**
 * Slide the incoming screen in from, and the outgoing screen out to, the opposite edge of the
 * window, so a screen leaves the display completely on wide windows.
 *
 * The free-function slide offsets are relative to the screen's rest position and sized to the
 * screen's own width, which only reaches the window edge for a screen that fills the window.
 * `(windowWidth + screenWidth) / 2` is the distance from the rest position to the opposite window
 * edge: for a screen that fills the window it equals the screen width, and for a screen that
 * rests centred it extends the slide by the centred margin on each side.
 */
fun <T : NavKey> pushSlideTransition(windowWidth: Int): (AnimatedContentTransitionScope<Scene<T>>) -> ContentTransform = {
    slideInHorizontally(initialOffsetX = { (windowWidth + it) / 2 }) togetherWith
        slideOutHorizontally(targetOffsetX = { -(windowWidth + it) / 2 })
}

fun <T : NavKey> popSlideTransition(windowWidth: Int): (AnimatedContentTransitionScope<Scene<T>>) -> ContentTransform = {
    slideInHorizontally(initialOffsetX = { -(windowWidth + it) / 2 }) togetherWith
        slideOutHorizontally(targetOffsetX = { (windowWidth + it) / 2 })
}
