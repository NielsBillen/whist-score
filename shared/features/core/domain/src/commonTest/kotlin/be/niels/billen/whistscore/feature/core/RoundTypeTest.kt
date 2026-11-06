package be.niels.billen.whistscore.feature.core

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class RoundTypeTest : FreeSpec() {
    init {
        "classifies which round types are played by a single bidder" {
            mapOf(
                RoundType.Pass to false,
                RoundType.Regular to false,
                RoundType.Treble to false,
                RoundType.Abandonce to true,
                RoundType.AbandonceInTrump to true,
                RoundType.Misere to true,
                RoundType.OpenMisere to true,
                RoundType.SoloSlim to true,
            ).forEach { (type, singlePlayer) ->
                type.singlePlayer shouldBe singlePlayer
            }
        }

        "restricts the number of participating players per round type" {
            mapOf(
                RoundType.Pass to 4..4,
                RoundType.Regular to 1..2,
                RoundType.Abandonce to 1..1,
                RoundType.AbandonceInTrump to 1..1,
                RoundType.Misere to 1..1,
                RoundType.Treble to 2..2,
                RoundType.OpenMisere to 1..1,
                RoundType.SoloSlim to 1..1,
            ).forEach { (type, range) ->
                type.playerCountRange shouldBe range
            }
        }
    }
}
