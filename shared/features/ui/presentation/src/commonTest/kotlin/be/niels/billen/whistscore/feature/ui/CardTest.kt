package be.niels.billen.whistscore.feature.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import io.kotest.core.spec.style.FreeSpec

@OptIn(ExperimentalTestApi::class)
class CardTest : FreeSpec() {
    init {
        "renders its content" {
            runSkikoComposeUiTest {
                setContent {
                    MaterialTheme {
                        Card {
                            Text(text = "Alpha")
                        }
                    }
                }
                onNodeWithText("Alpha").assertIsDisplayed()
            }
        }
    }
}
