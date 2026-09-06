package be.niels.billen.domain

import androidx.compose.ui.graphics.Color

data class Game(
    val players: Map<PlayerId, Player> = DEFAULT_PLAYERS,
    val rounds: List<Round> = emptyList()
) {
    init {
        require(players.size == 4) { "Each game must have 4 players" }
    }

    fun clearRounds() = copy(rounds = emptyList())

    fun isPassRound(index: Int) = rounds.isPassRound(index)
    private val scores by lazy { rounds.scores() }

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
            PlayerId.Player1 to Player(name = "Player 1", color = Color(0xFF3b4863)),
            PlayerId.Player2 to Player(name = "Player 2", color = Color(0xFFaf945a)),
            PlayerId.Player3 to Player(name = "Player 3", color = Color(0xFF9a4a4b)),
            PlayerId.Player4 to Player(name = "Player 4", color = Color(0xFF405850))
        )

        val DEFAULT = Game(players = DEFAULT_PLAYERS)
    }
}

private fun List<Round>.isPassRound(index: Int) = index in this.indices && when (this[index]) {
    is Round.PassRound -> true
    is Round.Regular,
    is Round.Treble,
    is Round.Abandonce,
    is Round.Misere,
    is Round.OpenMisere,
    is Round.SoloSlim -> false
}

private fun List<Round>.scores(): Map<PlayerId, Int> =
    PlayerId.entries.associateWith { playerId ->
        foldIndexed(0) { index, running, round ->
            running + round.points(playerId = playerId, passRound = isPassRound(index))
        }
    }
