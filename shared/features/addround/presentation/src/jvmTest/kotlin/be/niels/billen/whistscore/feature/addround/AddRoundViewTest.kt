package be.niels.billen.whistscore.feature.addround

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.RoundType
import be.niels.billen.whistscore.feature.game.FakeGameRepository
import be.niels.billen.whistscore.feature.game.GameRepository
import be.niels.billen.whistscore.feature.players.FakePlayerRepository
import be.niels.billen.whistscore.feature.players.PlayerRepository
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentSetOf
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf

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

        "backing out of the round type screen cancels the round" {
            var canceled = false
            withInternalAddRound(onCancel = { canceled = true }) { _ ->
                onNodeWithText("Back").performClick()
                waitForIdle()
            }
            canceled shouldBe true
        }

        "a multi-player selection offers a next button that confirms the players" {
            var nextScreen = false
            withInternalAddRound(onAction = { action -> nextScreen = action == AddRoundAction.ConfirmPlayers }) { viewModel ->
                driveToPlayersScreen(viewModel, singlePlayer = false)
                onNodeWithText("Next").performClick()
                waitForIdle()
            }
            nextScreen shouldBe true
        }

        "a single-player selection shows no next button" {
            withInternalAddRound { viewModel ->
                driveToPlayersScreen(viewModel, singlePlayer = true)
                onNodeWithText("Next").assertDoesNotExist()
            }
        }

        "the summary saves the built round" {
            var saved: Round? = null
            withInternalAddRound(onSave = { saved = it }) { viewModel ->
                driveToSummary(viewModel)
                onNodeWithText("Save").performClick()
                waitForIdle()
            }
            saved shouldBe Round.Regular(persistentSetOf(PlayerId.Player1), slams = 8)
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
        onNodeWithText("Player 1").assertIsDisplayed()
    }

    private fun withInternalAddRound(
        onCancel: () -> Unit = {},
        onSave: (Round) -> Unit = {},
        onAction: (AddRoundAction) -> Unit = {},
        block: suspend ComposeUiTest.(AddRoundViewModel) -> Unit,
    ) {
        startKoin { modules(testModule) }
        try {
            runSkikoComposeUiTest {
                val viewModel = GlobalContext.get().get<AddRoundViewModel>()
                setContent {
                    val state by viewModel.state.collectAsState()
                    AddRoundView(
                        onCancel = onCancel,
                        onSave = onSave,
                        onAction = { action -> onAction(action); viewModel.onAction(action) },
                        effects = viewModel.effects,
                        state = state,
                    )
                }
                block(viewModel)
            }
        } finally {
            stopKoin()
        }
    }

    private suspend fun ComposeUiTest.driveToPlayersScreen(viewModel: AddRoundViewModel, singlePlayer: Boolean) {
        viewModel.onAction(
            AddRoundAction.SetRoundType(if (singlePlayer) RoundType.Misere else RoundType.Regular),
        )
        viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
        waitForIdle()
        onNodeWithText("Player 1").assertIsDisplayed()
        onNodeWithText("Back").assertIsDisplayed()
    }

    private suspend fun ComposeUiTest.driveToSummary(viewModel: AddRoundViewModel) {
        driveToPlayersScreen(viewModel, singlePlayer = false)
        viewModel.onAction(AddRoundAction.ConfirmPlayers)
        waitForIdle()
        viewModel.onAction(AddRoundAction.SetSlams(8))
        waitForIdle()
        onNodeWithText("Save").assertIsDisplayed()
    }

    private val testModule = module {
        single { FakeGameRepository() }.bind<GameRepository>()
        single { FakePlayerRepository() }.bind<PlayerRepository>()
        viewModelOf(::AddRoundViewModel)
    }
}
