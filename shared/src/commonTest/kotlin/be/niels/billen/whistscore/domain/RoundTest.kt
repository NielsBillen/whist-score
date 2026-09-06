package be.niels.billen.whistscore.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class RoundTest : FreeSpec() {

    init {
        "Regular scores two teams split across the roster, ignoring the pass flag" {
            val regular = Round.Regular(
                playerIds = setOf(PlayerId.Player1, PlayerId.Player2),
                slams = 8,
            )

            PlayerId.entries.associateWith { regular.points(it, passRound = false) } shouldBe mapOf(
                PlayerId.Player1 to 2,
                PlayerId.Player2 to 2,
                PlayerId.Player3 to -2,
                PlayerId.Player4 to -2,
            )
        }

        "Treble scores two players against the rest without the pass flag" {
            val treble = Round.Treble(
                playerIds = setOf(PlayerId.Player1, PlayerId.Player2),
                slams = 8,
            )

            PlayerId.entries.associateWith { treble.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 4,
                PlayerId.Player2 to 4,
                PlayerId.Player3 to -4,
                PlayerId.Player4 to -4,
            )
        }

        "Abandonce scores the bidder against the roster without the pass flag" {
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

        "Misere scores the bidder against the roster when failing without the pass flag" {
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

        "a single round balances to zero across the roster" {
            val regular = Round.Regular(
                playerIds = setOf(PlayerId.Player1, PlayerId.Player2),
                slams = 8,
            )

            PlayerId.entries.associateWith { regular.points(it) }.values.sum() shouldBe 0
        }
    }
}
