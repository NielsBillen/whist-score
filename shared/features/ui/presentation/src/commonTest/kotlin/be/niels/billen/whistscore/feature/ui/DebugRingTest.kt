package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.unit.dp
import io.kotest.core.spec.style.FreeSpec

@OptIn(ExperimentalTestApi::class)
class DebugRingTest : FreeSpec() {
    init {
        "static ring draws outside bounds" {
            runSkikoComposeUiTest {
                setContent {
                    Box(
                        modifier = Modifier
                                .padding(24.dp)
                            .semantics { contentDescription = "box" }
                            .size(56.dp)
                            .drawFocusRing(16.dp, Color.Red)
                            .background(Color.Blue),
                    )
                }
                val b = onNodeWithContentDescription("box").fetchSemanticsNode().boundsInRoot
                val p = captureToImage().toPixelMap()
                val midY = ((b.top + b.bottom) / 2).toInt()
                println("STATIC left-1: " + p.get(b.left.toInt() - 1, midY))
                println("STATIC left-3: " + p.get(b.left.toInt() - 3, midY))
                println("STATIC left-4: " + p.get(b.left.toInt() - 4, midY))
                println("STATIC left-5: " + p.get(b.left.toInt() - 5, midY))
            }
        }
    }
}
