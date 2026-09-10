package be.niels.billen.whistscore.feature.addround

import be.niels.billen.whistscore.feature.core.RoundType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class AddRoundNavigatorTest : FreeSpec() {

    private val navigator = AddRoundNavigator()

    init {
        "next" - {
            "starts on select round type when no round type is set" {
                navigator.next(screen = AddRoundScreen.SELECT_ROUND_TYPE, roundType = null) shouldBe AddRoundScreen.SELECT_ROUND_TYPE
            }

            "steps a regular round through players, slams and summary" {
                navigator.next(screen = AddRoundScreen.SELECT_ROUND_TYPE, roundType = RoundType.Regular) shouldBe AddRoundScreen.SELECT_PLAYERS
                navigator.next(screen = AddRoundScreen.SELECT_PLAYERS, roundType = RoundType.Regular) shouldBe AddRoundScreen.SELECT_SLAMS
                navigator.next(screen = AddRoundScreen.SELECT_SLAMS, roundType = RoundType.Regular) shouldBe AddRoundScreen.SUMMARY
                navigator.next(screen = AddRoundScreen.SUMMARY, roundType = RoundType.Regular) shouldBe AddRoundScreen.SUMMARY
            }

            "steps a treble round through players, slams and summary" {
                navigator.next(screen = AddRoundScreen.SELECT_PLAYERS, roundType = RoundType.Treble) shouldBe AddRoundScreen.SELECT_SLAMS
                navigator.next(screen = AddRoundScreen.SELECT_SLAMS, roundType = RoundType.Treble) shouldBe AddRoundScreen.SUMMARY
            }

            "steps an abandonce round through players, bid achieved and summary" {
                navigator.next(screen = AddRoundScreen.SELECT_ROUND_TYPE, roundType = RoundType.Abandonce) shouldBe AddRoundScreen.SELECT_PLAYERS
                navigator.next(screen = AddRoundScreen.SELECT_PLAYERS, roundType = RoundType.Abandonce) shouldBe AddRoundScreen.SELECT_BID_ACHIEVED
                navigator.next(screen = AddRoundScreen.SELECT_BID_ACHIEVED, roundType = RoundType.Abandonce) shouldBe AddRoundScreen.SUMMARY
            }

            "steps a misere round through players, bid achieved and summary" {
                navigator.next(screen = AddRoundScreen.SELECT_PLAYERS, roundType = RoundType.Misere) shouldBe AddRoundScreen.SELECT_BID_ACHIEVED
            }

            "steps an open misere round through players, bid achieved and summary" {
                navigator.next(screen = AddRoundScreen.SELECT_PLAYERS, roundType = RoundType.OpenMisere) shouldBe AddRoundScreen.SELECT_BID_ACHIEVED
            }

            "steps a solo slim round through players, bid achieved and summary" {
                navigator.next(screen = AddRoundScreen.SELECT_PLAYERS, roundType = RoundType.SoloSlim) shouldBe AddRoundScreen.SELECT_BID_ACHIEVED
            }

            "keeps a pass round on the summary" {
                navigator.next(screen = AddRoundScreen.SUMMARY, roundType = RoundType.Pass) shouldBe AddRoundScreen.SUMMARY
            }
        }

        "previous" - {
            "stays on select round type when no round type is set" {
                navigator.previous(screen = AddRoundScreen.SELECT_ROUND_TYPE, roundType = null) shouldBe AddRoundScreen.SELECT_ROUND_TYPE
            }

            "walks a regular round back to select round type" {
                navigator.previous(screen = AddRoundScreen.SELECT_PLAYERS, roundType = RoundType.Regular) shouldBe AddRoundScreen.SELECT_ROUND_TYPE
                navigator.previous(screen = AddRoundScreen.SELECT_SLAMS, roundType = RoundType.Regular) shouldBe AddRoundScreen.SELECT_PLAYERS
                navigator.previous(screen = AddRoundScreen.SUMMARY, roundType = RoundType.Regular) shouldBe AddRoundScreen.SELECT_SLAMS
            }

            "walks an abandonce round back to select round type" {
                navigator.previous(screen = AddRoundScreen.SELECT_BID_ACHIEVED, roundType = RoundType.Abandonce) shouldBe AddRoundScreen.SELECT_PLAYERS
                navigator.previous(screen = AddRoundScreen.SUMMARY, roundType = RoundType.Abandonce) shouldBe AddRoundScreen.SELECT_BID_ACHIEVED
            }

            "stays on select round type at the first screen" {
                navigator.previous(screen = AddRoundScreen.SELECT_ROUND_TYPE, roundType = RoundType.Regular) shouldBe AddRoundScreen.SELECT_ROUND_TYPE
            }

            "walks a pass round back to select round type" {
                navigator.previous(screen = AddRoundScreen.SUMMARY, roundType = RoundType.Pass) shouldBe AddRoundScreen.SELECT_ROUND_TYPE
            }
        }
    }
}
