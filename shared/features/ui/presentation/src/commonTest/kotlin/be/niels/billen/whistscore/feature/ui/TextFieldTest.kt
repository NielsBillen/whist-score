package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class TextFieldTest : FreeSpec() {

    init {
        "typing reports the new text" {
            val value = mutableStateOf(TextFieldValue(""))

            runSkikoComposeUiTest {
                setContent {
                    UiTheme {
                        TextField(value = value.value, onValueChange = { value.value = it })
                    }
                }
                onNode(hasSetTextAction()).performTextInput("Alpha")
            }

            value.value.text shouldBe "Alpha"
        }

        "a disabled text field exposes no text input action" {
            val value = mutableStateOf(TextFieldValue(""))

            runSkikoComposeUiTest {
                setContent {
                    UiTheme {
                        TextField(
                            value = value.value,
                            onValueChange = { value.value = it },
                            enabled = false,
                        )
                    }
                }
                onNode(hasSetTextAction()).assertDoesNotExist()
            }

            value.value.text shouldBe ""
        }

        "keeps its astryx md height inside a taller container" {
            val value = mutableStateOf(TextFieldValue(""))

            runSkikoComposeUiTest {
                setContent {
                    UiTheme {
                        Box(modifier = Modifier.size(width = 400.dp, height = 600.dp)) {
                            TextField(value = value.value, onValueChange = { value.value = it })
                        }
                    }
                }
                onNode(hasSetTextAction()).assertHeightIsEqualTo(40.dp)
            }
        }

        "shows the placeholder only while empty" {
            val value = mutableStateOf(TextFieldValue(""))

            runSkikoComposeUiTest {
                setContent {
                    UiTheme {
                        TextField(
                            value = value.value,
                            onValueChange = { value.value = it },
                            placeholder = "Name",
                        )
                    }
                }

                onNodeWithText("Name").assertIsDisplayed()
                onNode(hasSetTextAction()).performTextInput("Alpha")
                onNodeWithText("Name").assertDoesNotExist()
            }
        }
    }
}
