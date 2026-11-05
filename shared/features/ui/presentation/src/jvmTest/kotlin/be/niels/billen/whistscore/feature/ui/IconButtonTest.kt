package be.niels.billen.whistscore.feature.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import be.niels.billen.whistscore.feature.core.icons.Trash
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class IconButtonTest : FreeSpec() {

    init {
        "clicking fires the action" {
            var clicked = false

            runSkikoComposeUiTest {
                setContent {
                    IconButton(
                        onClick = { clicked = true },
                        icon = Trash,
                        contentDescription = "Delete round",
                    )
                }

                onNodeWithContentDescription("Delete round").performClick()
            }

            clicked shouldBe true
        }

        "a disabled icon button does not fire the action" {
            var clicked = false

            runSkikoComposeUiTest {
                setContent {
                    IconButton(
                        onClick = { clicked = true },
                        enabled = false,
                        icon = Trash,
                        contentDescription = "Delete round",
                    )
                }

                onNodeWithContentDescription("Delete round").performClick()
            }

            clicked shouldBe false
        }
    }
}
