package be.niels.billen.whistscore.feature.game

import androidx.compose.runtime.Immutable
import be.niels.billen.whistscore.feature.core.*
import be.niels.billen.whistscore.feature.rounds.Rounds
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class Game(
    val players: Players = persistentHashMapOf(),
    val rounds: Rounds = Rounds.EMPTY,
    val order: ImmutableList<PlayerId> = DEFAULT_ORDER
) {
    init {
        require(players.size == 4) { "Each game must have 4 players" }
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
        val DEFAULT_PLAYERS = persistentHashMapOf(
            PlayerId.Player1 to Player(name = "Player 1", color = Color(0xFF3b4863.toInt())),
            PlayerId.Player2 to Player(name = "Player 2", color = Color(0xFFaf945a.toInt())),
            PlayerId.Player3 to Player(name = "Player 3", color = Color(0xFF9a4a4b.toInt())),
            PlayerId.Player4 to Player(name = "Player 4", color = Color(0xFF405850.toInt()))
        )

        val DEFAULT_ORDER = persistentListOf(
            PlayerId.Player1,
            PlayerId.Player2,
            PlayerId.Player3,
            PlayerId.Player4,
        )

        val DEFAULT = Game(players = DEFAULT_PLAYERS)
    }
}
