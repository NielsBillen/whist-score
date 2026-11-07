package be.niels.billen.whistscore.feature.game

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.collections.immutable.persistentListOf

class GameTest : FreeSpec() {
    init {
        "Game.score accumulates a regular round's points and ignores the pass flag on a pass round" {
            val game = Game.DEFAULT + Round.Regular(playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 8)

            PlayerId.entries.associateWith { game.score(it) } shouldBe
                mapOf(
                    PlayerId.Player1 to 2,
                    PlayerId.Player2 to 2,
                    PlayerId.Player3 to -2,
                    PlayerId.Player4 to -2,
                )
        }

        "a pass round contributes nothing and doubles the following round's points" {
            val game =
                Game.DEFAULT + Round.PassRound +
                    Round.Regular(playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 8)

            PlayerId.entries.associateWith { game.score(it) } shouldBe
                mapOf(
                    PlayerId.Player1 to 4,
                    PlayerId.Player2 to 4,
                    PlayerId.Player3 to -4,
                    PlayerId.Player4 to -4,
                )
        }

        "two consecutive pass rounds still only double the following round once" {
            val game =
                Game.DEFAULT + Round.PassRound + Round.PassRound +
                    Round.Regular(playerIds = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), slams = 8)

            PlayerId.entries.associateWith { game.score(it) } shouldBe
                mapOf(
                    PlayerId.Player1 to 4,
                    PlayerId.Player2 to 4,
                    PlayerId.Player3 to -4,
                    PlayerId.Player4 to -4,
                )
        }

        "a non-regular round after a pass round doubles its full split" {
            val game = Game.DEFAULT + Round.PassRound + Round.Abandonce(PlayerId.Player1)

            PlayerId.entries.associateWith { game.score(it) } shouldBe
                mapOf(
                    PlayerId.Player1 to 18,
                    PlayerId.Player2 to -6,
                    PlayerId.Player3 to -6,
                    PlayerId.Player4 to -6,
                )
        }

        "clearRounds resets every score to zero" {
            val game = (Game.DEFAULT + Round.Regular(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), 8)).clearRounds()
            PlayerId.entries.associateWith { game.score(it) } shouldBe
                mapOf(
                    PlayerId.Player1 to 0,
                    PlayerId.Player2 to 0,
                    PlayerId.Player3 to 0,
                    PlayerId.Player4 to 0,
                )
        }

        "an empty game scores zero for every player" {
            PlayerId.entries.associateWith { Game.DEFAULT.score(it) } shouldBe
                mapOf(
                    PlayerId.Player1 to 0,
                    PlayerId.Player2 to 0,
                    PlayerId.Player3 to 0,
                    PlayerId.Player4 to 0,
                )
        }

        "removing a round before another drops its pass-round doubling" {
            val game =
                Game.DEFAULT + Round.PassRound +
                    Round.Regular(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), 8)
            val removed = game.removeAt(0)

            PlayerId.entries.associateWith { removed.score(it) } shouldBe
                mapOf(
                    PlayerId.Player1 to 2,
                    PlayerId.Player2 to 2,
                    PlayerId.Player3 to -2,
                    PlayerId.Player4 to -2,
                )
        }

        "removing a later round re-scores the surviving rounds" {
            val game =
                Game.DEFAULT +
                    Round.Regular(persistentHashSetOf(PlayerId.Player1, PlayerId.Player2), 8) +
                    Round.PassRound
            val removed = game.removeAt(2)

            PlayerId.entries.associateWith { removed.score(it) } shouldBe
                mapOf(
                    PlayerId.Player1 to 2,
                    PlayerId.Player2 to 2,
                    PlayerId.Player3 to -2,
                    PlayerId.Player4 to -2,
                )
        }

        "removing out of range leaves the game unchanged" {
            val game = Game.DEFAULT + Round.PassRound

            game.removeAt(-1) shouldBe game
            game.removeAt(5) shouldBe game
        }

        "a game requires exactly four distinct players in its order" {
            shouldThrow<IllegalArgumentException> {
                Game(
                    order =
                        persistentListOf(
                            PlayerId.Player1,
                            PlayerId.Player1,
                            PlayerId.Player3,
                            PlayerId.Player4,
                        ),
                )
            }
            shouldThrow<IllegalArgumentException> {
                Game(order = persistentListOf(PlayerId.Player1, PlayerId.Player2, PlayerId.Player3))
            }
        }
    }
}
