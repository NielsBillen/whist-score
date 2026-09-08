package be.niels.billen.whistscore.feature.game

import be.niels.billen.whistscore.feature.core.Color
import be.niels.billen.whistscore.feature.core.Player
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.rounds.Rounds

data class Game(
    val players: Map<PlayerId, Player> = DEFAULT_PLAYERS,
    val rounds: List<Round> = emptyList()
) {
    init {
        require(players.size == 4) { "Each game must have 4 players" }
    }

    private val roundList: Rounds by lazy { Rounds(rounds) }
    private val scores by lazy { roundList.scoreSnapshot() }

    fun clearRounds() = copy(rounds = emptyList())

    fun isPassRound(index: Int) = roundList.isPassRound(index)

    operator fun plus(round: Round) = copy(rounds = rounds + round)

    fun removeAt(index: Int): Game {
        if (index in rounds.indices) {
            return copy(rounds = rounds.filterIndexed { i, _ -> i != index })
        }
        return this
    }

    fun score(playerId: PlayerId) = scores.getOrElse(playerId) { 0 }

    companion object {
        val DEFAULT_PLAYERS = mapOf(
            PlayerId.Player1 to Player(name = "Player 1", color = Color(0xFF3b4863.toInt())),
            PlayerId.Player2 to Player(name = "Player 2", color = Color(0xFFaf945a.toInt())),
            PlayerId.Player3 to Player(name = "Player 3", color = Color(0xFF9a4a4b.toInt())),
            PlayerId.Player4 to Player(name = "Player 4", color = Color(0xFF405850.toInt()))
        )

        val DEFAULT = Game(players = DEFAULT_PLAYERS)
    }
}
