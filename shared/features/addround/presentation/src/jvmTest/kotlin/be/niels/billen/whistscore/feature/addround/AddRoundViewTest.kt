package be.niels.billen.whistscore.feature.addround

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
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
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalTestApi::class)
class AddRoundViewTest : FreeSpec() {

    private val regularTwoPlayers = AddRoundState(
        roundType = RoundType.Regular,
        players = persistentSetOf(PlayerId.Player1, PlayerId.Player2),
    )

    init {
        "backing out of the round type screen cancels the round" {
            var canceled = false
            withAddRound(onCancel = { canceled = true }) {
                onNodeWithText("Back").performClick()
                waitForIdle()
            }
            canceled shouldBe true
        }

        "the round type selection offers every round type" {
            withAddRound {
                onNodeWithText("Regular", substring = true).assertIsDisplayed()
                onNodeWithText("Treble", substring = true).assertIsDisplayed()
                onNodeWithText("5. Misere").assertIsDisplayed()
                onNodeWithText("Pass", substring = true).assertIsDisplayed()
            }
        }

        "changing the round type navigates to the player selection" {
            withAddRound {
                onNodeWithText("Regular", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Player 1").assertIsDisplayed()
            }
        }

        "selecting a round type that skips player selection lands directly on the summary" {
            withAddRound {
                onNodeWithText("Pass", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Summary").assertIsDisplayed()
            }
        }

        "a multi-player selection offers a next button that confirms the players" {
            var confirmed = false
            withAddRound(
                initialState = AddRoundState(roundType = RoundType.Regular),
                onAction = { action -> confirmed = action == AddRoundAction.ConfirmPlayers },
            ) {
                onNodeWithText("Player 1").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()
                waitForIdle()
            }
            confirmed shouldBe true
        }

        "the next button is disabled while no player is selected" {
            withAddRound(initialState = AddRoundState(roundType = RoundType.Regular)) {
                onNodeWithText("Next").assertIsNotEnabled()
            }
        }

        "a single-player selection shows no next button" {
            withAddRound(initialState = AddRoundState(roundType = RoundType.Misere)) {
                onNodeWithText("Next").assertDoesNotExist()
            }
        }

        "changing the player selection re-renders the current screen" {
            withAddRound(initialState = AddRoundState(roundType = RoundType.Regular)) {
                onNodeWithText("Player 1").assertIsEnabled()
                onNodeWithText("Player 2").assertIsEnabled()
                onNodeWithText("Player 3").assertIsEnabled()

                onNodeWithText("Player 1").performClick()
                waitForIdle()
                onNodeWithText("Player 2").performClick()
                waitForIdle()
                onNodeWithText("Player 3").assertIsNotEnabled()

                onNodeWithText("Player 1").performClick()
                waitForIdle()
                onNodeWithText("Player 3").assertIsEnabled()
            }
        }

        "selecting a single player advances to the bid achieved screen" {
            withAddRound(initialState = AddRoundState(roundType = RoundType.Misere)) {
                onNodeWithText("Player 1").performClick()
                waitForIdle()
                onNodeWithText("Bid Achieved").assertIsDisplayed()
            }
        }

        "the summary saves the built round" {
            var saved: Round? = null
            withAddRound(
                initialState = regularTwoPlayers.copy(slams = 8),
                onSave = { saved = it },
            ) {
                onNodeWithText("Save").performClick()
                waitForIdle()
            }
            saved shouldBe Round.Regular(persistentSetOf(PlayerId.Player1, PlayerId.Player2), slams = 8)
        }

        "the summary shows the round points for every player in the game" {
            withAddRound(initialState = regularTwoPlayers.copy(slams = 8)) {
                for (name in listOf("Player 1", "Player 2", "Player 3", "Player 4")) {
                    onNodeWithText(name).assertIsDisplayed()
                }
            }
        }
    }

    private fun withAddRound(
        initialState: AddRoundState = AddRoundState(),
        onCancel: () -> Unit = {},
        onSave: (Round) -> Unit = {},
        onAction: (AddRoundAction) -> Unit = {},
        block: suspend ComposeUiTest.() -> Unit,
    ) {
        val testModule = module {
            single { FakeGameRepository() }.bind<GameRepository>()
            single { FakePlayerRepository() }.bind<PlayerRepository>()
            viewModel { AddRoundViewModel(get(), get(), initialState) }
        }

        startKoin { modules(testModule) }
        try {
            runSkikoComposeUiTest {
                setContent {
                    val viewModel = koinViewModel<AddRoundViewModel>()
                    val state by viewModel.state.collectAsState()
                    AddRoundView(
                        onCancel = onCancel,
                        onSave = onSave,
                        onAction = { action -> onAction(action); viewModel.onAction(action) },
                        effects = viewModel.effects,
                        state = state,
                        initialState = initialState,
                    )
                }
                block()
            }
        } finally {
            stopKoin()
        }
    }
}
