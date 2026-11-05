package be.niels.billen.whistscore.feature.ui

import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class AppShellTest : FreeSpec() {

    init {
        "shows the title and the content, and runs the actions" {
            var actionInvocations = 0

            runSkikoComposeUiTest {
                setContent {
                    UiTheme {
                        AppShell(
                            title = "Whist score",
                            actions = {
                                IconButton(onClick = { actionInvocations++ }) {
                                    Text(text = "Action")
                                }
                            },
                        ) {
                            Text(text = "Content")
                        }
                    }
                }

                onNodeWithText("Whist score").assertIsDisplayed()
                onNodeWithText("Content").assertIsDisplayed()
                onNodeWithText("Action").performClick()
            }

            actionInvocations shouldBe 1
        }
    }
}
