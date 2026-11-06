package be.niels.billen.whistscore.feature.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.SkikoComposeUiTest
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class ConfirmDialogTest : FreeSpec() {

    init {
        "confirming fires onConfirm and closes the dialog" {
            var confirmed = false
            runSkikoComposeUiTest {
                withConfirmDialog(
                    onConfirm = { confirmed = true },
                ) {
                    onNodeWithText("Start new game").performClick()
                    waitForIdle()
                    onNodeWithText("Start new game").assertDoesNotExist()
                }
            }
            confirmed shouldBe true
        }

        "cancelling closes the dialog without confirming" {
            var confirmed = false
            runSkikoComposeUiTest {
                withConfirmDialog(
                    onConfirm = { confirmed = true },
                ) {
                    onNodeWithText("Cancel").performClick()
                    waitForIdle()
                    onNodeWithText("Cancel").assertDoesNotExist()
                }
            }
            confirmed shouldBe false
        }

        "tapping outside closes the dialog without confirming" {
            var confirmed = false
            runSkikoComposeUiTest {
                withConfirmDialog(
                    onConfirm = { confirmed = true },
                ) {
                    onRoot().performTouchInput {
                        val corner = Offset(10f, 10f)
                        down(corner)
                        up()
                    }
                    waitForIdle()
                    onNodeWithText("Cancel").assertDoesNotExist()
                }
            }
            confirmed shouldBe false
        }
    }
}

@OptIn(ExperimentalTestApi::class)
private suspend fun SkikoComposeUiTest.withConfirmDialog(
    onConfirm: () -> Unit,
    block: suspend SkikoComposeUiTest.() -> Unit,
) {
    setContent {
        val visible = remember { mutableStateOf(true) }

        if (visible.value) {
            ConfirmDialog(
                title = "New game",
                body = "Starting a new game will discard all recorded rounds and reset the scoreboard.",
                confirmText = "Start new game",
                dismissText = "Cancel",
                onConfirm = {
                    visible.value = false
                    onConfirm()
                },
                onDismiss = { visible.value = false },
            )
        }
    }
    block()
}
