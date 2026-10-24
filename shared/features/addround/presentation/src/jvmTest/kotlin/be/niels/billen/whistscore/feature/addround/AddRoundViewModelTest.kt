package be.niels.billen.whistscore.feature.addround

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.RoundType
import be.niels.billen.whistscore.feature.game.FakeGameRepository
import be.niels.billen.whistscore.feature.players.FakePlayerRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.engine.coroutines.backgroundScope
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AddRoundViewModelTest : FreeSpec() {

    init {
        coroutineTestScope = true
        beforeSpec { Dispatchers.setMain(StandardTestDispatcher()) }
        afterSpec { Dispatchers.resetMain() }

        "selecting a round type clears the old selection and advances to its first screen" - {
            "a regular round goes to the player selection" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Regular))

                    viewModel.state.value.roundType shouldBe RoundType.Regular
                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SELECT_PLAYERS)
                }
            }

            "a pass round skips straight to the summary" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Pass))

                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SUMMARY)
                }
            }

            "a solo slim round goes to the player selection" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.SoloSlim))

                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SELECT_PLAYERS)
                }
            }

            "switching from a regular to a single-player round clears the players" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Regular))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player2, true))

                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Misere))

                    viewModel.state.value.players.isEmpty() shouldBe true
                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SELECT_PLAYERS)
                }
            }

            "switching between two multi-player rounds keeps the players" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Regular))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player2, true))

                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Treble))

                    viewModel.state.value.players.size shouldBe 2
                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SELECT_PLAYERS)
                }
            }
        }

        "a single-player selection is exclusive and advances immediately" - {
            "selecting a second player replaces the first" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Misere))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    effects.awaitItem()

                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player3, true))

                    viewModel.state.value.players shouldBe persistentSetOf(PlayerId.Player3)
                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SELECT_BID_ACHIEVED)
                }
            }

            "deselecting the only player removes it without advancing" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Misere))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    effects.awaitItem()

                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, false))

                    viewModel.state.value.players.isEmpty() shouldBe true
                    effects.expectNoEvents()
                }
            }
        }

        "confirming the players advances according to how the round is won" - {
            "a regular round goes to the slam selection" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Regular))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player2, true))

                    viewModel.onAction(AddRoundAction.ConfirmPlayers)

                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SELECT_SLAMS)
                }
            }

            "an abandonce round goes to the bid-achieved selection" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Abandonce))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    effects.awaitItem()

                    viewModel.onAction(AddRoundAction.ConfirmPlayers)

                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SELECT_BID_ACHIEVED)
                }
            }

            "a pass round goes straight to the summary" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Pass))
                    effects.awaitItem()

                    viewModel.onAction(AddRoundAction.ConfirmPlayers)

                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SUMMARY)
                }
            }
        }

        "choosing the outcome completes the round state" - {
            "choosing the slam count goes to the summary with a regular round" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Regular))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player2, true))
                    viewModel.onAction(AddRoundAction.ConfirmPlayers)
                    effects.awaitItem()

                    viewModel.onAction(AddRoundAction.SetSlams(8))

                    viewModel.state.value.round shouldBe Round.Regular(
                        persistentSetOf(PlayerId.Player1, PlayerId.Player2),
                        slams = 8,
                    )
                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SUMMARY)
                }
            }

            "a missed abandonce bid builds a failed single-player round" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Abandonce))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.ConfirmPlayers)
                    effects.awaitItem()

                    viewModel.onAction(AddRoundAction.SetBidAchieved(false))

                    viewModel.state.value.round shouldBe Round.Abandonce(PlayerId.Player1, playerWon = false)
                    effects.awaitItem() shouldBe AddRoundEffect.NextScreen(AddRoundScreen.SUMMARY)
                }
            }
        }

        "backing out emits the previous-screen effect" {
            withFixture {
                viewModel.onAction(AddRoundAction.PreviousScreen)

                effects.awaitItem() shouldBe AddRoundEffect.PreviousScreen
            }
        }

        "a slam count outside the legal range is rejected" - {
            "below zero" {
                shouldThrow<IllegalArgumentException> {
                    AddRoundAction.SetSlams(-1)
                }
            }

            "above thirteen" {
                shouldThrow<IllegalArgumentException> {
                    AddRoundAction.SetSlams(14)
                }
            }
        }

        "acting before a round type is chosen is rejected" - {
            "confirming the players" {
                withFixture {
                    shouldThrow<IllegalStateException> {
                        viewModel.onAction(AddRoundAction.ConfirmPlayers)
                    }
                }
            }

            "selecting a player" {
                withFixture {
                    shouldThrow<IllegalStateException> {
                        viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    }
                }
            }
        }

        "an unfinished round does not build a round" - {
            "a regular round without slams builds nothing" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Regular))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player2, true))
                    viewModel.onAction(AddRoundAction.ConfirmPlayers)
                    effects.awaitItem()

                    viewModel.state.value.round shouldBe null
                }
            }

            "a single-player round without a bid outcome builds nothing" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Misere))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.SetPlayerSelected(PlayerId.Player1, true))
                    effects.awaitItem()
                    viewModel.onAction(AddRoundAction.ConfirmPlayers)
                    effects.awaitItem()

                    viewModel.state.value.round shouldBe null
                }
            }

            "a pass round builds a pass round without any input" {
                withFixture {
                    viewModel.onAction(AddRoundAction.SetRoundType(RoundType.Pass))
                    effects.awaitItem()

                    viewModel.state.value.round shouldBe Round.PassRound
                }
            }
        }
    }

    private class Fixture {
        val gameRepository = FakeGameRepository()
        val playerRepository = FakePlayerRepository()
        val viewModel = AddRoundViewModel(gameRepository, playerRepository)
        lateinit var effects: ReceiveTurbine<AddRoundEffect>
    }

    private suspend fun TestScope.withFixture(block: suspend Fixture.() -> Unit) {
        turbineScope {
            val fixture = Fixture()
            fixture.effects = fixture.viewModel.effects.testIn(backgroundScope)
            fixture.block()
        }
    }
}
