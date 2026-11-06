package be.niels.billen.whistscore.feature.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class SelectableCardTest : FreeSpec() {
    init {
        "clicking the card fires onClick" {
            var clicked = false

            runSkikoComposeUiTest {
                setContent {
                    MaterialTheme {
                        SelectableCard(
                            selected = false,
                            onClick = { clicked = true },
                        ) {
                            Text(text = "Alpha")
                        }
                    }
                }
                onNodeWithText("Alpha").performClick()
            }

            clicked shouldBe true
        }

        "exposes its role as a radio button by default" {
            runSkikoComposeUiTest {
                setContent {
                    MaterialTheme {
                        SelectableCard(selected = false, onClick = { }) {
                            Text(text = "Alpha")
                        }
                    }
                }
                onNodeWithText("Alpha")
                    .fetchSemanticsNode()
                    .config
                    .getOrNull(SemanticsProperties.Role) shouldBe Role.RadioButton
            }
        }

        "exposes the given role" {
            runSkikoComposeUiTest {
                setContent {
                    MaterialTheme {
                        SelectableCard(selected = false, onClick = { }, role = Role.Checkbox) {
                            Text(text = "Alpha")
                        }
                    }
                }
                onNodeWithText("Alpha")
                    .fetchSemanticsNode()
                    .config
                    .getOrNull(SemanticsProperties.Role) shouldBe Role.Checkbox
            }
        }

        "a disabled card does not fire onClick" {
            var clicked = false

            runSkikoComposeUiTest {
                setContent {
                    MaterialTheme {
                        SelectableCard(
                            selected = false,
                            enabled = false,
                            onClick = { clicked = true },
                        ) {
                            Text(text = "Alpha")
                        }
                    }
                }
                onNodeWithText("Alpha").performClick()
            }

            clicked shouldBe false
        }
    }
}
