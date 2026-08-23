package be.niels.billen.domain

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round

class RoundsTest : FreeSpec() {

    init {

        "accumulates a single round's points for the score" {
            val rounds = Rounds.of(
                listOf(
                    Round.Regular(
                        players = setOf(PlayerId.Player1, PlayerId.Player2),
                        slams = 8,
                    ),
                ),
            )

            rounds.score(PlayerId.Player1) shouldBe 2
            rounds.score(PlayerId.Player3) shouldBe -2
        }

        "a passed previous round doubles the following round" {
            val rounds = Rounds.of(
                listOf(
                    Round.Regular(
                        players = setOf(PlayerId.Player1, PlayerId.Player2),
                        slams = 8,
                        passRound = true,
                    ),
                    Round.Regular(
                        players = setOf(PlayerId.Player1, PlayerId.Player2),
                        slams = 7,
                    ),
                ),
            )

            rounds.score(PlayerId.Player1) shouldBe -4
            rounds.score(PlayerId.Player3) shouldBe 4
        }
    }
}
