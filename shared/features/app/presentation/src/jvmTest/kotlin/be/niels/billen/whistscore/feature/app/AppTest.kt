package be.niels.billen.whistscore.feature.app

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runSkikoComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import io.kotest.matchers.comparables.shouldBeLessThan
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

        "saving a full regular round returns to the overview" {
            withApp {
                onNodeWithText("Add Round").performClick()
                onNodeWithText("Regular", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()

                onNodeWithText("Player 1").performClick()
                onNodeWithText("Player 2").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()

                waitForIdle()
                onNodeWithText("Select slams").assertIsDisplayed()
                onNodeWithText("8").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()

                waitForIdle()
                onNodeWithText("Summary").assertIsDisplayed()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }

        "saving a pass round returns to the overview" {
            withApp {
                onNodeWithText("Add Round").performClick()
                onNodeWithText("Pass", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("Summary").assertIsDisplayed()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }

        "saving a misere round returns to the overview" {
            withApp {
                onNodeWithText("Add Round").performClick()
                onNodeWithText("Misere - One player bids to achieve no slams").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()

                onNodeWithText("Player 1").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()

                waitForIdle()
                onNodeWithText("Bid Achieved").assertIsDisplayed()
                onNodeWithText("Yes").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("Summary").assertIsDisplayed()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("Whist score").assertIsDisplayed()
            }
        }

        "backing out of the player selection returns to the round type selection" {
            withApp {
                onNodeWithText("Add Round").performClick()
                onNodeWithText("Regular", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("Back").performClick()
                waitForIdle()
                onNodeWithText("Select round type").assertIsDisplayed()
                onNodeWithText("Regular", substring = true).performClick()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("Regular").assertIsDisplayed()
            }
        }

        "backing out of the summary returns to the slams selection" {
            withApp {
                onNodeWithText("Add Round").performClick()
                onNodeWithText("Regular", substring = true).performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()
                onNodeWithText("Player 1").performClick()
                onNodeWithText("Player 2").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("8").performClick()
                waitForIdle()
                onNodeWithText("Next").performClick()
                waitForIdle()
                onNodeWithText("Back").performClick()
                waitForIdle()
                onNodeWithText("Select slams").assertIsDisplayed()
            }
        }

        "a wide window centres the 800 dp content columns" - {
            "on the add round screen" {
                withApp(size = wideWindow) {
                    onNodeWithText("Add Round").performClick()
                    waitForIdle()
                    val node = onNodeWithText("Select round type").fetchSemanticsNode()
                    val centre = node.positionInRoot.x + node.size.width / 2f
                    kotlin.math.abs(centre - wideWindow.width / 2f) shouldBeLessThan 4f
                }
            }

            "after backing out of the add round screen" {
                withApp(size = wideWindow) {
                    onNodeWithText("Add Round").performClick()
                    waitForIdle()
                    onNodeWithText("Back").performClick()
                    waitForIdle()
                    val node = onNodeWithText("Whist score").fetchSemanticsNode()
                    // The 800 dp inner content is centred; its left edge is (window - 800) / 2.
                    val expectedLeft = wideWindow.width / 2f - 800f / 2f
                    kotlin.math.abs(node.positionInRoot.x - expectedLeft) shouldBeLessThan 4f
                }
            }

            "slides the previous screen fully offscreen when navigating forward" {
                withApp(size = wideWindow) {
                    onNodeWithText("Add Round").performClick()
                    waitForIdle()
                    onNodeWithText("Regular", substring = true).performClick()
                    waitForIdle()
                    val restRight = onNodeWithText("Select round type").fetchSemanticsNode().let { n ->
                        n.positionInRoot.x + n.size.width
                    }
                    mainClock.autoAdvance = false
                    onNodeWithText("Next").performClick()
                    // The slide must travel past the screen's own width: 1360 px on a 1920 px window,
                    // leaving no part of the exiting screen visible.
                    mainClock.advanceTimeUntil(5000) {
                        runWithoutImplicitWait {
                            val node = onNodeWithText("Select round type").fetchSemanticsNode()
                            restRight - (node.positionInRoot.x + node.size.width) > 800f + 100f
                        }
                    }
                }
            }

            "slides the previous screen fully offscreen when popping back" {
                withApp(size = wideWindow) {
                    onNodeWithText("Add Round").performClick()
                    waitForIdle()
                    onNodeWithText("Regular", substring = true).performClick()
                    waitForIdle()
                    onNodeWithText("Next").performClick()
                    waitForIdle()
                    onNodeWithText("Player 1").performClick()
                    onNodeWithText("Player 2").performClick()
                    waitForIdle()
                    onNodeWithText("Next").performClick()
                    waitForIdle()
                    onNodeWithText("8").performClick()
                    waitForIdle()
                    onNodeWithText("Next").performClick()
                    waitForIdle()
                    val restLeft = onNodeWithText("Summary").fetchSemanticsNode().let { n ->
                        n.positionInRoot.x
                    }
                    mainClock.autoAdvance = false
                    onNodeWithText("Back").performClick()
                    // The slide must travel past the screen's own width: 1360 px on a 1920 px window,
                    // leaving no part of the exiting screen visible.
                    mainClock.advanceTimeUntil(5000) {
                        runWithoutImplicitWait {
                            val node = onNodeWithText("Summary").fetchSemanticsNode()
                            node.positionInRoot.x - restLeft > 800f + 100f
                        }
                    }
                }
            }
        }

        "the entering overview never rests left-aligned while the pop animates" {
            withApp(size = wideWindow) {
                onNodeWithText("Add Round").performClick()
                waitForIdle()
                mainClock.autoAdvance = false
                onNodeWithText("Back").performClick()
                // While the pop animates, the entering screen must slide within its final,
                // centred position: its title's left edge never drifts to the window edge
                // (a left-aligned container would leave it at 16 px instead of 560 px).
                // The entering screen must slide smoothly to its centred resting position. If the
                // transition container sits left-aligned, the screen settles at the window edge
                // and only reaches its resting position when the container re-centres it at the
                // end: a discontinuous 544 px jump in one frame. The slide springs move at most
                // ~230 px per frame, so a bigger per-frame move is the re-centring jump.
                var prev: Float? = null
                mainClock.advanceTimeUntil(5000) {
                    runWithoutImplicitWait {
                        val leftEdge = try {
                            onNodeWithText("Whist score").fetchSemanticsNode().positionInRoot.x
                        } catch (e: AssertionError) {
                            null
                        }
                        when {
                            leftEdge == null -> false // not composed yet, or disposed: keep going
                            else -> {
                                val previous = prev
                                prev = leftEdge
                                if (previous != null && leftEdge - previous > 400f) {
                                    error("the entering overview jumped from x=$previous to x=$leftEdge: it settled at the window edge instead of sliding to its resting position")
                                }
                                leftEdge > 500f // reached the centred resting position
                            }
                        }
                    }
                }
                mainClock.autoAdvance = true
                waitForIdle()
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

    private fun withApp(
        size: Size = Size(1024f, 768f),
        block: suspend ComposeUiTest.() -> Unit,
    ) {
        startKoin { modules(testAppModule) }
        try {
            runSkikoComposeUiTest(size = size) {
                setContent { App() }
                block()
            }
        } finally {
            stopKoin()
        }
    }

    private val wideWindow = Size(1920f, 768f)

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
