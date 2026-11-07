package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.unit.dp
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@OptIn(ExperimentalTestApi::class)
class ButtonTest : FreeSpec() {
    init {
        "a focused button draws the astryx ring outside its bounds" {
            runSkikoComposeUiTest {
                setContent {
                    // room for the ring, which draws 5px beyond the fill
                    Button(onClick = {}, modifier = Modifier.padding(24.dp)) { Text("Save") }
                }

                val node = onNodeWithText("Save")
                node.performClick()

                val bounds = node.fetchSemanticsNode().boundsInRoot
                val left = bounds.left.toInt()
                val right = bounds.right.toInt()
                val midY = ((bounds.top + bounds.bottom) / 2).toInt()
                val pixels = captureToImage().toPixelMap()

                // ring: 2px, starting 3px away from the fill — its middle sits 4px out
                pixels.get(left - 4, midY) shouldBe Style.Colors.accent
                pixels.get(right + 4, midY) shouldBe Style.Colors.accent
                // the 3px gap stays empty
                pixels.get(left - 1, midY) shouldNotBe Style.Colors.accent
            }
        }
    }
}
