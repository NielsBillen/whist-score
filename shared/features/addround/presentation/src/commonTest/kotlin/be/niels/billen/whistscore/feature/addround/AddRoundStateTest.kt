package be.niels.billen.whistscore.feature.addround

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.RoundType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentSetOf

class AddRoundStateTest : FreeSpec() {

    private val twoPlayers = persistentSetOf(PlayerId.Player1, PlayerId.Player2)
    private val playerOne = persistentSetOf(PlayerId.Player1)
    private val regularPlayers = AddRoundState(
        roundType = RoundType.Regular,
        players = twoPlayers,
    )

    init {
        "an empty state builds no round" {
            val state = AddRoundState()

            state.round shouldBe null
        }

        "a pass round type builds the pass round regardless of the other input" {
            val state = AddRoundState(roundType = RoundType.Pass)

            state.round shouldBe Round.PassRound
        }

        "a round type without any selected player builds no round" {
            val state = AddRoundState(roundType = RoundType.Regular)

            state.round shouldBe null
        }

        "a regular round builds once its slam count is known" - {
            "with a slam count it builds the regular round" {
                val state = AddRoundState(roundType = RoundType.Regular, players = twoPlayers, slams = 8)

                state.round shouldBe Round.Regular(twoPlayers, slams = 8)
            }

            "without a slam count it builds nothing" {
                val state = AddRoundState(roundType = RoundType.Regular, players = twoPlayers)

                state.round shouldBe null
            }
        }

        "a treble round builds once its slam count is known" - {
            "with a slam count it builds the treble round" {
                val state = AddRoundState(roundType = RoundType.Treble, players = twoPlayers, slams = 8)

                state.round shouldBe Round.Treble(twoPlayers, slams = 8)
            }

            "without a slam count it builds nothing" {
                val state = AddRoundState(roundType = RoundType.Treble, players = twoPlayers)

                state.round shouldBe null
            }
        }

        "an abandonce round builds once the bid outcome is known" - {
            "a made bid builds a won abandonce" {
                val state = AddRoundState(
                    roundType = RoundType.Abandonce,
                    players = playerOne,
                    bidAchieved = true,
                )

                state.round shouldBe Round.Abandonce(PlayerId.Player1, playerWon = true)
            }

            "a missed bid builds a failed abandonce" {
                val state = AddRoundState(
                    roundType = RoundType.Abandonce,
                    players = playerOne,
                    bidAchieved = false,
                )

                state.round shouldBe Round.Abandonce(PlayerId.Player1, playerWon = false)
            }

            "without a bid outcome it builds nothing" {
                val state = AddRoundState(roundType = RoundType.Abandonce, players = playerOne)

                state.round shouldBe null
            }
        }

        "an abandonce in trump round builds the same abandonce from its first player" {
            val state = AddRoundState(
                roundType = RoundType.AbandonceInTrump,
                players = playerOne,
                bidAchieved = true,
            )

            state.round shouldBe Round.Abandonce(PlayerId.Player1, playerWon = true)
        }

        "a misere round builds once the bid outcome is known" - {
            "taking no slams builds a won misere" {
                val state = AddRoundState(roundType = RoundType.Misere, players = playerOne, bidAchieved = true)

                state.round shouldBe Round.Misere(PlayerId.Player1, playerWon = true)
            }

            "taking a slam builds a failed misere" {
                val state = AddRoundState(roundType = RoundType.Misere, players = playerOne, bidAchieved = false)

                state.round shouldBe Round.Misere(PlayerId.Player1, playerWon = false)
            }

            "without a bid outcome it builds nothing" {
                val state = AddRoundState(roundType = RoundType.Misere, players = playerOne)

                state.round shouldBe null
            }
        }

        "an open misere round builds once the bid outcome is known" - {
            "taking no slams builds a won open misere" {
                val state = AddRoundState(
                    roundType = RoundType.OpenMisere,
                    players = playerOne,
                    bidAchieved = true,
                )

                state.round shouldBe Round.OpenMisere(PlayerId.Player1, playerWon = true)
            }

            "taking a slam builds a failed open misere" {
                val state = AddRoundState(
                    roundType = RoundType.OpenMisere,
                    players = playerOne,
                    bidAchieved = false,
                )

                state.round shouldBe Round.OpenMisere(PlayerId.Player1, playerWon = false)
            }

            "without a bid outcome it builds nothing" {
                val state = AddRoundState(roundType = RoundType.OpenMisere, players = playerOne)

                state.round shouldBe null
            }
        }

        "a solo slim round builds once the bid outcome is known" - {
            "taking all slams builds a won solo slim" {
                val state = AddRoundState(roundType = RoundType.SoloSlim, players = playerOne, bidAchieved = true)

                state.round shouldBe Round.SoloSlim(PlayerId.Player1, playerWon = true)
            }

            "missing the slams builds a failed solo slim" {
                val state = AddRoundState(roundType = RoundType.SoloSlim, players = playerOne, bidAchieved = false)

                state.round shouldBe Round.SoloSlim(PlayerId.Player1, playerWon = false)
            }

            "without a bid outcome it builds nothing" {
                val state = AddRoundState(roundType = RoundType.SoloSlim, players = playerOne)

                state.round shouldBe null
            }
        }

        "the screen to resume on is derived from how much of the round is complete" - {
            "an empty state resumes on the round type selection" {
                AddRoundState().screen shouldBe AddRoundScreen.SELECT_ROUND_TYPE
            }

            "an unfinished regular round resumes on the player selection" {
                AddRoundState(roundType = RoundType.Regular).screen shouldBe AddRoundScreen.SELECT_PLAYERS
            }

            "a regular round with players but no slam count resumes on the slam selection" {
                regularPlayers.screen shouldBe AddRoundScreen.SELECT_SLAMS
            }

            "an unfinished single-player round resumes on the bid selection" {
                AddRoundState(roundType = RoundType.Misere, players = playerOne).screen shouldBe
                    AddRoundScreen.SELECT_BID_ACHIEVED
            }

            "a pass round resumes on the summary" {
                AddRoundState(roundType = RoundType.Pass).screen shouldBe AddRoundScreen.SUMMARY
            }

            "a complete round resumes on the summary" {
                AddRoundState(
                    roundType = RoundType.Misere,
                    players = playerOne,
                    bidAchieved = true,
                ).screen shouldBe AddRoundScreen.SUMMARY
            }
        }
    }
}
