package be.niels.billen.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round

class RoundTest : FreeSpec() {

    init {

        "Regular scores two teams split across the roster, ignoring the pass flag" {
            val regular = Round.Regular(
                players = setOf(PlayerId.Player1, PlayerId.Player2),
                slams = 8,
                passRound = true,
            )

            PlayerId.entries.associateWith { regular.points(it) } shouldBe mapOf(
                PlayerId.Player1 to 2,
                PlayerId.Player2 to 2,
                PlayerId.Player3 to -2,
                PlayerId.Player4 to -2,
            )
        }

        "Treble scores two players against the rest without the pass flag" {
            val treble = Round.Treble(
                players = setOf(PlayerId.Player1, PlayerId.Player2),
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
                player = PlayerId.Player1,
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
                player = PlayerId.Player1,
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
                players = setOf(PlayerId.Player1, PlayerId.Player2),
                slams = 8,
            )

            PlayerId.entries.associateWith { regular.points(it) }.values.sum() shouldBe 0
        }
    }
}
