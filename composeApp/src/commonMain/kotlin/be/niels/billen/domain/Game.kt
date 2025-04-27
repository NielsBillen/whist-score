package be.niels.billen.domain

import androidx.compose.ui.graphics.Color

data class Game(val players: Map<PlayerId, Player> = defaultPlayers, val rounds: List<Round> = emptyList()) {
    fun score(playerId: PlayerId) = scores.getOrElse(playerId, { 0 })

    private val scores: Map<PlayerId, Int> by lazy {
        PlayerId.entries.associateWith { player -> rounds.fold(0) { score, round -> score + round.points(player) } }
    }

    companion object {
        val defaultPlayers = mapOf(
            PlayerId.Player1 to Player("Player 1", Color(0xFFFFB703)),
            PlayerId.Player2 to Player("Player 2", Color(0xFF219EBC)),
            PlayerId.Player3 to Player("Player 3", Color(0xFF8ECAE6)),
            PlayerId.Player4 to Player("Player 4", Color(0xFFFB8500))
        )
    }
}