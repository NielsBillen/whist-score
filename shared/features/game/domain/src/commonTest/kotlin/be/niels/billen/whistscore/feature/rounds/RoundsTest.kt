package be.niels.billen.whistscore.feature.rounds

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.collections.immutable.persistentListOf

class RoundsTest : FreeSpec() {

    private val regular = Round.Regular(playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 8)

    init {
        "a pass round contributes no points of its own" {
            val rounds = Rounds(persistentListOf(Round.PassRound))

            rounds.points(0, PlayerId.Player1) shouldBe 0
        }

        "a round not preceded by a pass round scores its base points" {
            val rounds = Rounds(persistentListOf(regular))

            rounds.points(0, PlayerId.Player1) shouldBe 2
        }

        "a round preceded by a pass round scores double" {
            val rounds = Rounds(persistentListOf(Round.PassRound, regular))

            rounds.points(1, PlayerId.Player1) shouldBe 4
        }

        "two consecutive pass rounds only double the following round once" {
            val rounds = Rounds(persistentListOf(Round.PassRound, Round.PassRound, regular))

            rounds.points(2, PlayerId.Player1) shouldBe 4
        }
    }
}
