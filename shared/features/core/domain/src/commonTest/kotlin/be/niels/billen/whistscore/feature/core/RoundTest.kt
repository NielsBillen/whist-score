package be.niels.billen.whistscore.feature.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentHashSetOf

class RoundTest : FreeSpec() {

    private val allPlayers: Map<PlayerId, Int>
        get() = mapOf(
            PlayerId.Player1 to 0,
            PlayerId.Player2 to 0,
            PlayerId.Player3 to 0,
            PlayerId.Player4 to 0,
        )

    init {
        "a regular round won by two players gives the team the base points and the others the loss" {
            val regular = Round.Regular(
                playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2),
                slams = 8,
            )

            PlayerId.entries.associateWith { regular.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 2,
                PlayerId.Player2 to 2,
                PlayerId.Player3 to -2,
                PlayerId.Player4 to -2,
            )
        }

        "a solo regular round that makes its bid pays the bidder triple and the rest the base loss" {
            val regular = Round.Regular(
                playerIds = persistentHashSetOf(PlayerId.Player1),
                slams = 5,
            )

            PlayerId.entries.associateWith { regular.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 6,
                PlayerId.Player2 to -2,
                PlayerId.Player3 to -2,
                PlayerId.Player4 to -2,
            )
        }

        "a regular round that misses its bid loses the base points and pays the others" {
            val regular = Round.Regular(
                playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2),
                slams = 6,
            )

            PlayerId.entries.associateWith { regular.points(it) } shouldBe mapOf(
                PlayerId.Player1 to -4,
                PlayerId.Player2 to -4,
                PlayerId.Player3 to 4,
                PlayerId.Player4 to 4,
            )
        }

        "a treble won by the two bidders splits the base points against the rest" {
            val treble = Round.Treble(
                playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2),
                slams = 8,
            )

            PlayerId.entries.associateWith { treble.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 4,
                PlayerId.Player2 to 4,
                PlayerId.Player3 to -4,
                PlayerId.Player4 to -4,
            )
        }

        "a treble that misses its bid loses the doubled shortfall for both bidders" {
            val treble = Round.Treble(
                playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2),
                slams = 7,
            )

            PlayerId.entries.associateWith { treble.points(it) } shouldBe mapOf(
                PlayerId.Player1 to -6,
                PlayerId.Player2 to -6,
                PlayerId.Player3 to 6,
                PlayerId.Player4 to 6,
            )
        }

        "an abandonce that makes the bid pays the bidder triple the penalty and the rest the penalty" {
            val abandonce = Round.Abandonce(
                playerId = PlayerId.Player1,
                playerWon = true,
            )

            PlayerId.entries.associateWith { abandonce.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 9,
                PlayerId.Player2 to -3,
                PlayerId.Player3 to -3,
                PlayerId.Player4 to -3,
            )
        }

        "an abandonce that fails the bid loses triple the penalty for the bidder and pays the rest" {
            val abandonce = Round.Abandonce(
                playerId = PlayerId.Player1,
                playerWon = false,
            )

            PlayerId.entries.associateWith { abandonce.points(it) } shouldBe mapOf(
                PlayerId.Player1 to -9,
                PlayerId.Player2 to 3,
                PlayerId.Player3 to 3,
                PlayerId.Player4 to 3,
            )
        }

        "a misere that takes no slams pays the bidder triple the penalty and the rest the penalty" {
            val misere = Round.Misere(
                playerId = PlayerId.Player1,
                playerWon = true,
            )

            PlayerId.entries.associateWith { misere.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 15,
                PlayerId.Player2 to -5,
                PlayerId.Player3 to -5,
                PlayerId.Player4 to -5,
            )
        }

        "a misere that takes a slam loses triple the penalty for the bidder and pays the rest" {
            val misere = Round.Misere(
                playerId = PlayerId.Player1,
                playerWon = false,
            )

            PlayerId.entries.associateWith { misere.points(it) } shouldBe mapOf(
                PlayerId.Player1 to -15,
                PlayerId.Player2 to 5,
                PlayerId.Player3 to 5,
                PlayerId.Player4 to 5,
            )
        }

        "an open misere pays triple the open misere penalty to the bidder and the rest the penalty" {
            val openMisere = Round.OpenMisere(
                playerId = PlayerId.Player1,
                playerWon = true,
            )

            PlayerId.entries.associateWith { openMisere.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 30,
                PlayerId.Player2 to -10,
                PlayerId.Player3 to -10,
                PlayerId.Player4 to -10,
            )
        }

        "a solo slim that takes all thirteen slams pays the bidder triple the penalty and the rest the penalty" {
            val soloSlim = Round.SoloSlim(
                playerId = PlayerId.Player1,
                playerWon = true,
            )

            PlayerId.entries.associateWith { soloSlim.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 45,
                PlayerId.Player2 to -15,
                PlayerId.Player3 to -15,
                PlayerId.Player4 to -15,
            )
        }

        "a solo slim that fails the bid loses triple the penalty for the bidder and pays the rest" {
            val soloSlim = Round.SoloSlim(
                playerId = PlayerId.Player1,
                playerWon = false,
            )

            PlayerId.entries.associateWith { soloSlim.points(it) } shouldBe mapOf(
                PlayerId.Player1 to -45,
                PlayerId.Player2 to 15,
                PlayerId.Player3 to 15,
                PlayerId.Player4 to 15,
            )
        }

        "a pass round pays nobody and is won by nobody" {
            val pass = Round.PassRound

            PlayerId.entries.associateWith { pass.points(it) } shouldBe allPlayers
            PlayerId.entries.all { !pass.won(it) } shouldBe true
        }

        "a won multi-player round is won by its players and lost by the rest" {
            val regular = Round.Regular(
                playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2),
                slams = 8,
            )

            PlayerId.entries.associateWith { regular.won(it) } shouldBe mapOf(
                PlayerId.Player1 to true,
                PlayerId.Player2 to true,
                PlayerId.Player3 to false,
                PlayerId.Player4 to false,
            )
        }

        "a lost multi-player round is won by the players the losing team failed against" {
            val regular = Round.Regular(
                playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2),
                slams = 6,
            )

            PlayerId.entries.associateWith { regular.won(it) } shouldBe mapOf(
                PlayerId.Player1 to false,
                PlayerId.Player2 to false,
                PlayerId.Player3 to true,
                PlayerId.Player4 to true,
            )
        }

        "a won single-player round is won by the bidder and lost by the rest" {
            val soloSlim = Round.SoloSlim(
                playerId = PlayerId.Player1,
                playerWon = true,
            )

            PlayerId.entries.associateWith { soloSlim.won(it) } shouldBe mapOf(
                PlayerId.Player1 to true,
                PlayerId.Player2 to false,
                PlayerId.Player3 to false,
                PlayerId.Player4 to false,
            )
        }

        "a failed single-player round is lost by the bidder and won by the rest" {
            val misere = Round.Misere(
                playerId = PlayerId.Player1,
                playerWon = false,
            )

            PlayerId.entries.associateWith { misere.won(it) } shouldBe mapOf(
                PlayerId.Player1 to false,
                PlayerId.Player2 to true,
                PlayerId.Player3 to true,
                PlayerId.Player4 to true,
            )
        }

        "every round balances to zero across the roster" {
            val rounds: List<Round> = listOf(
                Round.PassRound,
                Round.Regular(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 8),
                Round.Regular(persistentHashSetOf(PlayerId.Player1), slams = 5),
                Round.Regular(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 6),
                Round.Treble(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 8),
                Round.Treble(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 7),
                Round.Abandonce(PlayerId.Player1, playerWon = true),
                Round.Abandonce(PlayerId.Player1, playerWon = false),
                Round.Misere(PlayerId.Player1, playerWon = true),
                Round.Misere(PlayerId.Player1, playerWon = false),
                Round.OpenMisere(PlayerId.Player1, playerWon = true),
                Round.OpenMisere(PlayerId.Player1, playerWon = false),
                Round.SoloSlim(PlayerId.Player1, playerWon = true),
                Round.SoloSlim(PlayerId.Player1, playerWon = false),
            )

            rounds.forEach { round ->
                PlayerId.entries.associateWith { round.points(it) }.values.sum() shouldBe 0
            }
        }

        "a regular round rejects invalid player counts and slam counts" {
            shouldThrow<IllegalArgumentException> {
                Round.Regular(persistentHashSetOf(), slams = 8)
            }
            shouldThrow<IllegalArgumentException> {
                Round.Regular(
                    persistentHashSetOf(PlayerId.Player1, PlayerId.Player2, PlayerId.Player3),
                    slams = 8,
                )
            }
            shouldThrow<IllegalArgumentException> {
                Round.Regular(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 14)
            }
        }

        "a treble rejects a single player and out-of-range slam counts" {
            shouldThrow<IllegalArgumentException> {
                Round.Treble(persistentHashSetOf(PlayerId.Player1), slams = 8)
            }
            shouldThrow<IllegalArgumentException> {
                Round.Treble(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 14)
            }
        }
    }
}
