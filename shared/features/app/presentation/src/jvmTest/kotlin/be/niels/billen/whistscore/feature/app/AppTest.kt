package be.niels.billen.whistscore.feature.app

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import be.niels.billen.whistscore.feature.addround.AddRoundViewModel
import be.niels.billen.whistscore.feature.editplayers.EditPlayersViewModel
import be.niels.billen.whistscore.feature.game.FakeGameRepository
import be.niels.billen.whistscore.feature.game.GameRepository
import be.niels.billen.whistscore.feature.overview.OverviewViewModel
import be.niels.billen.whistscore.feature.players.FakePlayerRepository
import be.niels.billen.whistscore.feature.players.PlayerRepository
import be.niels.billen.whistscore.feature.players.PlayersViewModel
import be.niels.billen.whistscore.feature.rounds.RoundsViewModel
import io.kotest.core.spec.style.FreeSpec
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalTestApi::class)
class AppTest : FreeSpec() {
    init {
        "shows the overview at startup" {
            withApp {
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }

        "add round shows the add round screen" {
            withApp {
                onNodeWithText("Add round").performClick()
                onNodeWithText("Select round type").assertIsDisplayed()
            }
        }

        "cancelling add round returns to the overview" {
            withApp {
                onNodeWithText("Add round").performClick()
                onNodeWithText("Back").performClick()
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }

        "saving a full regular round returns to the overview" {
            withApp {
                onNodeWithText("Add round").performClick()
                onNodeWithText("Regular", substring = true).performClick()
                waitForIdle()

                onNodeWithText("Player 1").performClick()
                onNodeWithText("Player 2").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()

                waitForIdle()
                onNodeWithText("Select slams").assertIsDisplayed()
                onNodeWithText("8").performClick()
                waitForIdle()
                onNodeWithText("Summary").assertIsDisplayed()
                onNodeWithText("Save").performClick()
                waitForIdle()
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }

        "saving a pass round returns to the overview" {
            withApp {
                onNodeWithText("Add round").performClick()
                onNodeWithText("Pass", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Summary").assertIsDisplayed()
                onNodeWithText("Save").performClick()
                waitForIdle()
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }

        "saving a misere round returns to the overview" {
            withApp {
                onNodeWithText("Add round").performClick()
                onNodeWithText("Misere").performClick()
                waitForIdle()

                onNodeWithText("Player 1").performClick()
                waitForIdle()
                onNodeWithText("Bid Achieved").assertIsDisplayed()
                onNodeWithText("Yes").performClick()
                waitForIdle()
                onNodeWithText("Summary").assertIsDisplayed()
                onNodeWithText("Save").performClick()
                waitForIdle()
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }

        "backing out of the player selection returns to the round type selection" {
            withApp {
                onNodeWithText("Add round").performClick()
                onNodeWithText("Regular", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Back").performClick()
                waitForIdle()
                onNodeWithText("Select round type").assertIsDisplayed()
                onNodeWithText("Regular", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Player 1").assertIsDisplayed()
            }
        }

        "backing out of the summary returns to the slams selection" {
            withApp {
                onNodeWithText("Add round").performClick()
                onNodeWithText("Regular", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Player 1").performClick()
                onNodeWithText("Player 2").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("8").performClick()
                waitForIdle()
                onNodeWithText("Back").performClick()
                waitForIdle()
                onNodeWithText("Select slams").assertIsDisplayed()
            }
        }

        "edit players shows the edit players screen" {
            withApp {
                onNodeWithContentDescription("Edit players").performClick()
                onNodeWithText("Edit player names").assertIsDisplayed()
            }
        }

        "saving the players returns to the overview" {
            withApp {
                onNodeWithContentDescription("Edit players").performClick()
                onNodeWithText("Save").performClick()
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }
    }

    private fun withApp(block: suspend ComposeUiTest.() -> Unit) {
        startKoin { modules(testAppModule) }
        try {
            runComposeUiTest {
                setContent { App(viewModel = AppViewModel(FakeGameRepository())) }
                block()
            }
        } finally {
            stopKoin()
        }
    }

    // Lambda forms, not viewModelOf(::X): the reified inline overload emits an $$inlined bridge
    // class whose SourceFile points at Koin's ViewModelOf.kt, which the dependency analysis
    // plugin can't map to a source set and crashes on (upstream bug #1951).
    private val testAppModule =
        module {
            single { FakePlayerRepository() }.bind<PlayerRepository>()
            single { FakeGameRepository() }.bind<GameRepository>()
            viewModel { OverviewViewModel(get()) }
            viewModel { AddRoundViewModel(get(), get()) }
            viewModel { EditPlayersViewModel(get(), get()) }
            viewModel { RoundsViewModel(get(), get()) }
            viewModel { PlayersViewModel(get(), get()) }
        }
}
