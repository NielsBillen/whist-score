package be.niels.billen.whistscore.feature.game

import androidx.compose.runtime.Immutable
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.rounds.Rounds
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class Game(
    val rounds: Rounds = Rounds.EMPTY,
    val order: PersistentList<PlayerId> = DEFAULT_ORDER
) {
    init {
        require(order.toSet().size == 4) { "Each game must have 4 players" }
    }

    fun clearRounds() = copy(rounds = Rounds.EMPTY)

    operator fun plus(round: Round) = copy(rounds = rounds + round)

    fun removeAt(index: Int): Game {
        if (index in rounds.indices) {
            return copy(rounds = rounds.removeAt(index))
        }
        return this
    }

    fun score(playerId: PlayerId) = rounds.score(playerId)

    companion object {
        val DEFAULT_ORDER = persistentListOf(
            PlayerId.Player1,
            PlayerId.Player2,
            PlayerId.Player3,
            PlayerId.Player4,
        )

        val DEFAULT = Game()
    }
}
