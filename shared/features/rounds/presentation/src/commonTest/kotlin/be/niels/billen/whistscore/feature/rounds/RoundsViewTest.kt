package be.niels.billen.whistscore.feature.rounds

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import be.niels.billen.whistscore.feature.core.DEFAULT_PLAYERS
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Players
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.game.Game
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@OptIn(ExperimentalTestApi::class)
class RoundsViewTest : FreeSpec() {

    private val players = DEFAULT_PLAYERS

    private val game = Game(
        rounds = Rounds(
            persistentListOf(
                Round.Regular(playerIds = persistentSetOf(PlayerId.Player1)),
                Round.Regular(playerIds = persistentSetOf(PlayerId.Player2)),
            ),
        ),
    )

    init {
        "shows a header cell per player" {
            withRoundsView(game = game, players = players) {
                onNodeWithText("Player 1").assertIsDisplayed()
                onNodeWithText("Player 2").assertIsDisplayed()
                onNodeWithText("Player 3").assertIsDisplayed()
                onNodeWithText("Player 4").assertIsDisplayed()
            }
        }

        "shows the round number and points for each round" {
            withRoundsView(game = game, players = players) {
                onNodeWithText("1").assertIsDisplayed()
                onNodeWithText("2").assertIsDisplayed()
                onAllNodesWithText("+7").fetchSemanticsNodes().size shouldBe 6
                onAllNodesWithText("-21").fetchSemanticsNodes().size shouldBe 2
            }
        }

        "deleting a round dispatches the action for that index" {
            var deletedIndex = -1
            withRoundsView(game = game, players = players, onAction = { action ->
                if (action is RoundsViewAction.DeleteRound) deletedIndex = action.index
            }) {
                onAllNodesWithContentDescription("Delete round")[1].performClick()
            }
            deletedIndex shouldBe 1
        }

        "shows a message when no rounds have been played" {
            withRoundsView(game = Game.DEFAULT, players = players) {
                onNodeWithText("No rounds have been played").assertIsDisplayed()
            }
        }
    }
}

@OptIn(ExperimentalTestApi::class)
private fun withRoundsView(
    game: Game,
    players: Players,
    onAction: (RoundsViewAction) -> Unit = {},
    block: suspend ComposeUiTest.() -> Unit,
) {
    runSkikoComposeUiTest {
        setContent {
            RoundsView(game = game, players = players, onAction = onAction)
        }
        block()
    }
}
