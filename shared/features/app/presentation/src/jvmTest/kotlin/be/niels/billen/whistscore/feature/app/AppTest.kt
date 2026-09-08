package be.niels.billen.whistscore.feature.app

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import be.niels.billen.whistscore.feature.game.FakeGameRepository
import be.niels.billen.whistscore.feature.players.FakePlayerRepository
import be.niels.billen.whistscore.feature.game.GameRepository
import be.niels.billen.whistscore.feature.players.PlayerRepository
import be.niels.billen.whistscore.feature.addround.AddRoundViewModel
import be.niels.billen.whistscore.feature.editplayers.EditPlayersViewModel
import be.niels.billen.whistscore.feature.overview.OverviewViewModel
import be.niels.billen.whistscore.feature.players.PlayersViewModel
import be.niels.billen.whistscore.feature.rounds.RoundsViewModel
import io.kotest.core.spec.style.FreeSpec
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
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
                onNodeWithText("Add Round").performClick()
                onNodeWithText("Select round type").assertIsDisplayed()
            }
        }

        "cancelling add round returns to the overview" {
            withApp {
                onNodeWithText("Add Round").performClick()
                onNodeWithText("Back").performClick()
                onNodeWithText("Whist score").assertIsDisplayed()
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
                setContent { App() }
                block()
            }
        } finally {
            stopKoin()
        }
    }

    private val testAppModule = module {
        single { FakePlayerRepository() }.bind<PlayerRepository>()
        single { FakeGameRepository() }.bind<GameRepository>()
        viewModelOf(::AppViewModel)
        viewModelOf(::OverviewViewModel)
        viewModelOf(::AddRoundViewModel)
        viewModelOf(::EditPlayersViewModel)
        viewModelOf(::RoundsViewModel)
        viewModelOf(::PlayersViewModel)
    }
}
