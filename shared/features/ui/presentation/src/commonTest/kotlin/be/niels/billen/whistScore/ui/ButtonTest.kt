package be.niels.billen.whistScore.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import be.niels.billen.whistScore.ui
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class ButtonTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun button_displaysContent() {
        var clicked = false
        composeTestRule.setContent {
            Button(onClick = { clicked = true }) {
                Text("Click me")
            }
        }
        // Verify button is displayed
        composeTestRule.onNodeWithText("Click me").assertExists()
        // Simulate click
        composeTestRule.onNodeWithText("Click me").performClick()
        assertThat(clicked).isTrue()
    }
}