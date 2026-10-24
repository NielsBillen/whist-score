package be.niels.billen.whistscore.feature.addround

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import be.niels.billen.whistscore.feature.game.FakeGameRepository
import be.niels.billen.whistscore.feature.game.GameRepository
import io.kotest.core.spec.style.FreeSpec
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.module.dsl.viewModelOf

@OptIn(ExperimentalTestApi::class)
class AddRoundViewTest : FreeSpec() {

    init {
        "changing the player selection re-renders the current screen" {
            withAddRound {
                openPlayersScreen()
                onNodeWithText("Player 1").performClick()
                onNodeWithText("Player 2").performClick()
                waitForIdle()
                onNodeWithText("Player 3").assertIsNotEnabled()

                onNodeWithText("Player 2").performClick()
                waitForIdle()
                onNodeWithText("Player 3").assertIsEnabled()
            }
        }
    }

    private fun withAddRound(block: suspend ComposeUiTest.() -> Unit) {
        startKoin { modules(testModule) }
        try {
            runSkikoComposeUiTest {
                setContent { AddRoundView(onCancel = {}, onSave = {}) }
                block()
            }
        } finally {
            stopKoin()
        }
    }

    private suspend fun ComposeUiTest.openPlayersScreen() {
        onNodeWithText("Regular", substring = true).performClick()
        waitForIdle()
        onNodeWithText("Select players", substring = true).assertIsDisplayed()
    }

    private val testModule = module {
        single { FakeGameRepository() }.bind<GameRepository>()
        viewModelOf(::AddRoundViewModel)
    }
}
