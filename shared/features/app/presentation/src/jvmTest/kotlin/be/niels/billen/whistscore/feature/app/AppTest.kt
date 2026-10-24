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
                onNodeWithText("Add Round").performClick()
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
                onNodeWithText("Add Round").performClick()
                onNodeWithText("Misere - One player bids to achieve no slams").performClick()
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
                onNodeWithText("Add Round").performClick()
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
                onNodeWithText("Add Round").performClick()
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
                    val button = onNodeWithText("New game").fetchSemanticsNode()
                    // The button row spans the 800 dp centred content column (plus its 16 dp
                    // outer padding), so the first button sits (window - 832) / 2 + 24 px in.
                    val expectedLeft = wideWindow.width / 2f - 832f / 2f + 24f
                    kotlin.math.abs(button.positionInRoot.x - expectedLeft) shouldBeLessThan 4f
                }
            }

            "slides the previous screen fully offscreen when navigating forward" {
                withApp(size = wideWindow) {
                    onNodeWithText("Add Round").performClick()
                    waitForIdle()
                    onNodeWithText("Regular", substring = true).performClick()
                    waitForIdle()
                    onNodeWithText("Player 1").performClick()
                    onNodeWithText("Player 2").performClick()
                    waitForIdle()
                    val restRight = onNodeWithText("Regular").fetchSemanticsNode().let { n ->
                        n.positionInRoot.x + n.size.width
                    }
                    mainClock.autoAdvance = false
                    onNodeWithText("Next").performClick()
                    // The slide must travel past the screen's own width: 1360 px on a 1920 px window,
                    // leaving no part of the exiting screen visible.
                    mainClock.advanceTimeUntil(5000) {
                        runWithoutImplicitWait {
                            val node = onNodeWithText("Regular").fetchSemanticsNode()
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
                    onNodeWithText("Player 1").performClick()
                    onNodeWithText("Player 2").performClick()
                    waitForIdle()
                    onNodeWithText("Next").performClick()
                    waitForIdle()
                    onNodeWithText("8").performClick()
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
                // While the pop animates, the entering overview must slide smoothly to its
                // resting position: the title never makes a discontinuous jump (a
                // left-aligned container would re-centre the screen at the end of the
                // transition: a discontinuous jump of over 400 px in one frame), and it
                // settles at its centred resting position (x = 872 on a 1920 px window).
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
                                    error("the entering overview jumped from x=$previous to x=$leftEdge instead of sliding to its resting position")
                                }
                                leftEdge > 800f // reached the centred resting position
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
